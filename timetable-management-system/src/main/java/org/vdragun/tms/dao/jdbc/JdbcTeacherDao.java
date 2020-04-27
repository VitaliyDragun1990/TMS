package org.vdragun.tms.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.dao.DaoException;
import org.vdragun.tms.dao.TeacherDao;

/**
 * Implementation of {@link TeacherDao} using Spring JDBC
 * 
 * @author Vitaliy Dragun
 *
 */
@Repository
public class JdbcTeacherDao implements TeacherDao {

    private static final String INSERT_QUERY = "INSERT INTO teachers (t_first_name, t_last_name, title, date_hired) "
            + "VALUES (?, ?, ?, ?);";
    private static final String FIND_ALL_QUERY =
            "SELECT t.teacher_id, t_first_name, t_last_name, title, date_hired, "
            + "cr.course_id, course_name, course_description, "
            + "ca.category_id, category_code, category_description "
            + "FROM teachers AS t LEFT OUTER JOIN courses AS cr ON t.teacher_id = cr.teacher_id "
            + "LEFT OUTER JOIN categories AS ca ON ca.category_id = cr.category_id";
    private static final String FIND_BY_ID_QUERY = FIND_ALL_QUERY + " WHERE t.teacher_id = ?;";
    private static final String FIND_FOR_COURSE_QUERY = FIND_ALL_QUERY + " WHERE cr.course_id = ?;";

    private JdbcTemplate jdbc;
    private TeachersWithCoursesExtractor teachersExtractor;

    public JdbcTeacherDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        teachersExtractor = new TeachersWithCoursesExtractor();
    }

    @Override
    public void save(Teacher teacher) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsInserted = jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, new String[] { "teacher_id" });
            statement.setString(1, teacher.getFirstName());
            statement.setString(2, teacher.getLastName());
            statement.setString(3, teacher.getTitle().asString());
            statement.setObject(4, teacher.getDateHired());
            return statement;
        }, keyHolder);

        if (rowsInserted != 1) {
            throw new DaoException("Fail to save new teacher to the database");
        }

        int teacherId = keyHolder.getKey().intValue();
        teacher.setId(teacherId);
    }

    @Override
    public void saveAll(List<Teacher> teachers) {
        // use this approach because there is no way to retrieve auto-generated keys
        // using jdbcTemplate.batchUpdate(..) method
        teachers.forEach(this::save);
    }

    @Override
    public Optional<Teacher> findById(Integer teacherId) {
        List<Teacher> result = jdbc.query(FIND_BY_ID_QUERY, new Object[] { teacherId }, teachersExtractor);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<Teacher> findAll() {
        return jdbc.query(FIND_ALL_QUERY, teachersExtractor);
    }

    @Override
    public Optional<Teacher> findForCourse(Integer courseId) {
        List<Teacher> result = jdbc.query(FIND_FOR_COURSE_QUERY, new Object[] { courseId }, teachersExtractor);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

}
