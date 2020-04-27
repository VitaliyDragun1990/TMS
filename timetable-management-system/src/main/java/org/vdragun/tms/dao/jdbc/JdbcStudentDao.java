package org.vdragun.tms.dao.jdbc;

import static java.lang.String.format;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

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

    private static final String INSERT_QUERY = "INSERT INTO students (s_first_name, s_last_name, enrollment_date) "
            + "VALUES (?, ?, ?);";
    private static final String FIND_ALL_QUERY = 
            "SELECT s.student_id, s_first_name, s_last_name, enrollment_date, "
            + "t.teacher_id, t_first_name, t_last_name, title, date_hired, "
            + "c.course_id, course_name, course_description, g.group_id, group_name, "
            + "ca.category_id, category_code, category_description "
            + "FROM students AS s LEFT OUTER JOIN groups AS g ON s.group_id = g.group_id "
            + "LEFT OUTER JOIN student_courses AS sc ON s.student_id = sc.student_id "
            + "LEFT OUTER JOIN courses AS c ON c.course_id = sc.course_id "
            + "LEFT OUTER JOIN categories AS ca ON ca.category_id = c.category_id "
            + "LEFT OUTER JOIN teachers AS t ON t.teacher_id = c.teacher_id";
    private static final String FIND_BY_ID_QUERY = FIND_ALL_QUERY + " WHERE s.student_id = ?";
    private static final String FIND_FOR_COURSE_QUERY =
            "SELECT i.student_id, i.s_first_name, i.s_last_name, i.enrollment_date, "
            + "i.teacher_id, i.t_first_name, i.t_last_name, i.title, i.date_hired, "
            + "i.course_id, i.course_name, "
            + "i.course_description, i.group_id, i.group_name, i.category_id, i.category_code, i.category_description "
            + "FROM courses AS cr INNER JOIN (" + FIND_ALL_QUERY + ") AS i "
            + "ON cr.course_id = i.course_id "
            + "WHERE cr.course_id = ?";
    private static final String FIND_FOR_GROUP_QUERY = FIND_ALL_QUERY + " WHERE s.group_id = ?";
    private static final String ADD_TO_COURSE_QUERY = "INSERT INTO student_courses (student_id, course_id) "
            + "VALUES (?, ?);";
    private static final String REMOVE_FROM_COURSE_QUERY =
            "DELETE FROM student_courses WHERE student_id = ? AND course_id = ?";
    private static final String ADD_TO_GROUP_QUERY = "UPDATE students SET group_id = ? WHERE student_id = ?";
    private static final String REMOVE_FROM_GROUP_QUERY = "UPDATE students SET group_id = NULL WHERE student_id = ?";
    private static final String REMOVE_FROM_ALL_COURSES_QUERY = "DELETE FROM student_courses WHERE student_id = ?";

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
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, new String[] { "student_id" });
            ps.setString(1, student.getFirstName());
            ps.setString(2, student.getLastName());
            ps.setObject(3, student.getEnrollmentDate());
            return ps;
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
        List<Student> result = jdbc.query(FIND_BY_ID_QUERY, new Object[] { studentId }, studentsExtractor);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<Student> findAll() {
        return jdbc.query(FIND_ALL_QUERY, studentsExtractor);
    }

    @Override
    public List<Student> findForCourse(Integer courseId) {
        return jdbc.query(FIND_FOR_COURSE_QUERY, new Object[] { courseId }, studentsExtractor);
    }

    @Override
    public List<Student> findForGroup(Integer groupId) {
        return jdbc.query(FIND_FOR_GROUP_QUERY, new Object[] { groupId }, studentsExtractor);
    }

    @Override
    public void addToCourse(Integer studentId, Integer courseId) {
        int rowsInserted = jdbc.update(ADD_TO_COURSE_QUERY, studentId, courseId);
        if (rowsInserted != 1) {
            throw new DaoException(
                    format("Fail to add student with id=%d to course with id=%d", studentId, courseId));
        }
    }

    @Override
    public void removeFromCourse(Integer studentId, Integer courseId) {
        jdbc.update(REMOVE_FROM_COURSE_QUERY, studentId, courseId);
    }

    @Override
    public void addToGroup(Integer studentId, Integer groupId) {
        int rowsUpdated = jdbc.update(ADD_TO_GROUP_QUERY, groupId, studentId);
        if (rowsUpdated != 1) {
            throw new DaoException(
                    format("Fail to add student with id=%d to group with id=%d", studentId, groupId));
        }
    }

    @Override
    public void removeFromGroup(Integer studentId) {
        jdbc.update(REMOVE_FROM_GROUP_QUERY, studentId);
    }

    @Override
    public void removeFromAllCourses(Integer studentId) {
        jdbc.update(REMOVE_FROM_ALL_COURSES_QUERY, studentId);
    }

}
