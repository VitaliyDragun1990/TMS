package org.vdragun.tms.dao.jdbc;

import static java.lang.String.format;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.dao.DaoException;
import org.vdragun.tms.dao.StudentDao;

/**
 * Implementation of {@link StudentDao} using Spring JDBC
 * 
 * @author Vitaliy Dragun
 *
 */
@Repository
public class JdbcStudentDao implements StudentDao {

    @Value("${student.insert}")
    private String insertQuery;

    @Value("${student.findAll}")
    private String findAllQuery;

    @Value("${student.findById}")
    private String findByIdQuery;

    @Value("${student.findForCourse}")
    private String findForCourseQuery;

    @Value("${student.findForGroup}")
    private String findForGroupQuery;

    @Value("${student.addToCourse}")
    private String addToCourseQuery;

    @Value("${student.removeFromCourse}")
    private String removeFromCourseQuery;

    @Value("${student.addToGroup}")
    private String addToGroupQuery;

    @Value("${student.removeFromGroup}")
    private String removeFromGroupQuery;

    @Value("${student.removeFromAllCourses}")
    private String removeFromAllCoursesQuery;

    @Value("${student.exists}")
    private String existsQuery;

    private JdbcTemplate jdbc;
    private StudentsWithCoursesExtractor studentsExtractor;

    public JdbcStudentDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        studentsExtractor = new StudentsWithCoursesExtractor();
    }

    @Override
    public void save(Student student) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsInserted = jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(insertQuery, new String[] { "student_id" });
            statement.setString(1, student.getFirstName());
            statement.setString(2, student.getLastName());
            statement.setObject(3, student.getEnrollmentDate());
            return statement;
        }, keyHolder);

        if (rowsInserted != 1) {
            throw new DaoException("Fail to save new student to the database");
        }

        int studentId = keyHolder.getKey().intValue();
        student.setId(studentId);
    }

    @Override
    public void saveAll(List<Student> students) {
        // use this approach because there is no way to retrieve auto-generated keys
        // using jdbcTemplate.batchUpdate(..) method
        students.forEach(this::save);
    }

    @Override
    public Optional<Student> findById(Integer studentId) {
        List<Student> result = jdbc.query(findByIdQuery, new Object[] { studentId }, studentsExtractor);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<Student> findAll() {
        return jdbc.query(findAllQuery, studentsExtractor);
    }

    @Override
    public List<Student> findForCourse(Integer courseId) {
        return jdbc.query(findForCourseQuery, new Object[] { courseId }, studentsExtractor);
    }

    @Override
    public List<Student> findForGroup(Integer groupId) {
        return jdbc.query(findForGroupQuery, new Object[] { groupId }, studentsExtractor);
    }

    @Override
    public void addToCourse(Integer studentId, Integer courseId) {
        int rowsInserted = jdbc.update(addToCourseQuery, studentId, courseId);
        if (rowsInserted != 1) {
            throw new DaoException(
                    format("Fail to add student with id=%d to course with id=%d", studentId, courseId));
        }
    }

    @Override
    public void removeFromCourse(Integer studentId, Integer courseId) {
        jdbc.update(removeFromCourseQuery, studentId, courseId);
    }

    @Override
    public void addToGroup(Integer studentId, Integer groupId) {
        int rowsUpdated = jdbc.update(addToGroupQuery, groupId, studentId);
        if (rowsUpdated != 1) {
            throw new DaoException(
                    format("Fail to add student with id=%d to group with id=%d", studentId, groupId));
        }
    }

    @Override
    public boolean existsById(Integer studentId) {
        return jdbc.queryForObject(existsQuery, Boolean.class, studentId);
    }

    @Override
    public void removeFromGroup(Integer studentId) {
        jdbc.update(removeFromGroupQuery, studentId);
    }

    @Override
    public void removeFromAllCourses(Integer studentId) {
        jdbc.update(removeFromAllCoursesQuery, studentId);
    }

}
