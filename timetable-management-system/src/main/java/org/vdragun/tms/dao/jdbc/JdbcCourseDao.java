package org.vdragun.tms.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${course.insert}")
    private String insertQuery;

    @Value("${course.findAll}")
    private String findAllQuery;

    @Value("${course.findById}")
    private String findByIdQuery;

    @Value("${course.findByCategory}")
    private String findByCategoryQuery;

    @Value("${course.exists}")
    private String existsQuery;

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
            PreparedStatement statement = connection.prepareStatement(insertQuery, new String[] { "course_id" });
            statement.setString(1, course.getName());
            statement.setString(2, course.getDescription());
            statement.setInt(3, course.getCategory().getId());
            statement.setInt(4, course.getTeacher().getId());
            return statement;
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
        List<Course> result = jdbc.query(findByIdQuery, new Object[] { courseId }, courseMapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<Course> findAll() {
        return jdbc.query(findAllQuery, courseMapper);
    }

    @Override
    public List<Course> findByCategory(Integer categoryId) {
        return jdbc.query(findByCategoryQuery, new Object[] { categoryId }, courseMapper);
    }

    @Override
    public boolean existsById(Integer courseId) {
        return jdbc.queryForObject(existsQuery, Boolean.class, courseId);
    }

}
