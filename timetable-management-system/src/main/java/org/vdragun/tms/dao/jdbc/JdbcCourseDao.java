package org.vdragun.tms.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.dao.CourseDao;
import org.vdragun.tms.dao.DaoException;

/**
 * Implementation of {@link CourseDao} using Spring JDBC
 * 
 * @author Vitaliy Dragun
 *
 */
@Repository
public class JdbcCourseDao implements CourseDao {
    
    private static final String INSERT_QUERY = 
            "INSERT INTO courses (course_name, course_description, category_id, teacher_id) "
            + "VALUES (?, ?, ?, ?);";
    private static final String FIND_ALL_QUERY = "SELECT course_id, course_name, course_description, "
            + "c.teacher_id, t_first_name, t_last_name, title, date_hired, c.category_id, "
            + "category_code, category_description "
            + "FROM courses AS c INNER JOIN teachers AS t ON c.teacher_id = t.teacher_id "
            + "INNER JOIN categories AS ca ON c.category_id = ca.category_id";
    private static final String FIND_BY_ID_QUERY = FIND_ALL_QUERY + " WHERE course_id = ?";
    private static final String FIND_BY_CATEGORY_QUERY = FIND_ALL_QUERY + " WHERE c.category_id = ?";

    private JdbcTemplate jdbc;
    private CourseMapper courseMapper;

    public JdbcCourseDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        courseMapper = new CourseMapper();
    }

    @Override
    public void save(Course course) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsInserted = jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, new String[] { "course_id" });
            ps.setString(1, course.getName());
            ps.setString(2, course.getDescription());
            ps.setInt(3, course.getCategory().getId());
            ps.setInt(4, course.getTeacher().getId());
            return ps;
        }, keyHolder);

        if (rowsInserted != 1) {
            throw new DaoException("Fail to save new course to the database");
        }

        int courseId = keyHolder.getKey().intValue();
        course.setId(courseId);
    }

    @Override
    public void saveAll(List<Course> courses) {
        // use this approach because there is no way to retrieve auto-generated keys
        // using jdbcTemplate.batchUpdate(..) method
        courses.forEach(this::save);
    }

    @Override
    public Optional<Course> findById(Integer courseId) {
        List<Course> result = jdbc.query(FIND_BY_ID_QUERY, new Object[] { courseId }, courseMapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<Course> findAll() {
        return jdbc.query(FIND_ALL_QUERY, courseMapper);
    }

    @Override
    public List<Course> findByCategory(Integer categoryId) {
        return jdbc.query(FIND_BY_CATEGORY_QUERY, new Object[] { categoryId }, courseMapper);
    }

}
