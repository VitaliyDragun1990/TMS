package org.vdragun.tms.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    private static final Logger LOG = LoggerFactory.getLogger(JdbcTeacherDao.class);

    @Value("${teacher.insert}")
    private String insertQuery;

    @Value("${teacher.findAll}")
    private String findAllQuery;

    @Value("${teacher.findById}")
    private String findByIdQuery;

    @Value("${teacher.findForCourse}")
    private String findForCourseQuery;

    @Value("${teacher.exists}")
    private String existsQuery;

    private JdbcTemplate jdbc;
    private TeachersWithCoursesExtractor teachersExtractor;

    public JdbcTeacherDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        teachersExtractor = new TeachersWithCoursesExtractor();
    }

    @Override
    public void save(Teacher teacher) {
        LOG.debug("Saving new teacher to the database: {}", teacher);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsInserted = jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(insertQuery, new String[] { "teacher_id" });
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
        LOG.debug("Saving {} new teachers to the database", teachers.size());
        // use this approach because there is no way to retrieve auto-generated keys
        // using jdbcTemplate.batchUpdate(..) method
        teachers.forEach(this::save);
    }

    @Override
    public Optional<Teacher> findById(Integer teacherId) {
        LOG.debug("Searching for teacher with id={} in the database", teacherId);
        List<Teacher> result = jdbc.query(findByIdQuery, new Object[] { teacherId }, teachersExtractor);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<Teacher> findAll() {
        LOG.debug("Retrieving all teachers from the database");
        return jdbc.query(findAllQuery, teachersExtractor);
    }

    @Override
    public Optional<Teacher> findForCourse(Integer courseId) {
        LOG.debug("Searching for teacher assigned to course with id={} in the database", courseId);
        List<Teacher> result = jdbc.query(findForCourseQuery, new Object[] { courseId }, teachersExtractor);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public boolean existsById(Integer teacherId) {
        LOG.debug("Checking whether teacher with id={} exists in the database", teacherId);
        return jdbc.queryForObject(existsQuery, new Object[] { teacherId }, Boolean.class);
    }

}
