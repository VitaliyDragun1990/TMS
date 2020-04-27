package org.vdragun.tms.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Title;

/**
 * Contains helper methods to facilitate JDBC DAO testing. Provides direct work
 * with database.
 * 
 * @author Vitaliy Dragun
 *
 */
public class JdbcTestHelper {

    private DataSource dataSource;

    private ClassroomMapper classroomMapper;
    private CategoryMapper categoryMapper;
    private GroupMapper groupMapper;
    private TeacherMapper teacherMapper;
    private StudentMapper studentMapper;
    private CourseMapper courseMapper;

    public JdbcTestHelper(DataSource dataSource) {
        this.dataSource = dataSource;
        classroomMapper = new ClassroomMapper();
        categoryMapper = new CategoryMapper();
        groupMapper = new GroupMapper();
        teacherMapper = new TeacherMapper();
        studentMapper = new StudentMapper();
        courseMapper = new CourseMapper();
    }

    public Classroom saveClassroomToDatabase(int capacity) throws SQLException {
        String sql = "INSERT INTO classrooms (capacity) VALUES (?);";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, capacity);

                ps.executeUpdate();
                conn.commit();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    return new Classroom(keys.getInt("classroom_id"), capacity);
                }
            }
        }
    }

    public List<Classroom> findAllClassroomsInDatabase() throws SQLException {
        String sql = "SELECT classroom_id, capacity FROM classrooms;";
        
        List<Classroom> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            int rowNum = 1;
            while (rs.next()) {
                result.add(classroomMapper.mapRow(rs, rowNum++));
            }
        }
        return result;
    }

    public Category saveCategoryToDatabase(String code, String description) throws SQLException {
        String sql = "INSERT INTO categories (category_code, category_description) VALUES (?, ?);";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, code);
                ps.setString(2, description);

                ps.executeUpdate();
                conn.commit();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    return new Category(keys.getInt("category_id"), code, description);
                }
            }
        }
    }

    public List<Category> findAllCategoriesInDatabase() throws SQLException {
        String sql = "SELECT category_id, category_code, category_description FROM categories;";

        List<Category> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            int rowNum = 1;
            while (rs.next()) {
                result.add(categoryMapper.mapRow(rs, rowNum++));
            }
        }
        return result;
    }

    public Group saveGroupToDatabase(String name) throws SQLException {
        String sql = "INSERT INTO groups (group_name) VALUES (?);";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);

                ps.executeUpdate();
                conn.commit();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    return new Group(keys.getInt("group_id"), name);
                }
            }
        }
    }

    public List<Group> findAllGroupsInDatabase() throws SQLException {
        String sql = "SELECT group_id, group_name FROM groups";

        List<Group> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            int rowNum = 1;
            while (rs.next()) {
                result.add(groupMapper.mapRow(rs, rowNum++));
            }
        }
        return result;
    }

    public Teacher saveTeacherToDatabase(String firstName, String lastName, Title title, LocalDate dateHired)
            throws SQLException {
        String sql = "INSERT INTO teachers (t_first_name, t_last_name, title, date_hired) VALUES (?, ?, ?, ?);";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, title.asString());
                ps.setObject(4, dateHired);

                ps.executeUpdate();
                conn.commit();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    return new Teacher(keys.getInt("teacher_id"), firstName, lastName, title, dateHired);
                }
            }
        }
    }

    public List<Teacher> findAllTeachersInDatabase() throws SQLException {
        String sql = "SELECT teacher_id, t_first_name, t_last_name, title, date_hired "
                + "FROM teachers";

        List<Teacher> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            int rowNum = 1;
            while (rs.next()) {
                result.add(teacherMapper.mapRow(rs, rowNum++));
            }
        }
        return result;
    }

    public Course saveCourseToDatabase(String name, String desc, Category category, Teacher teacher)
            throws SQLException {
        String sql = "INSERT INTO courses (course_name, course_description, category_id, teacher_id) VALUES (?, ?, ?, ?);";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, desc);
                ps.setInt(3, category.getId());
                ps.setInt(4, teacher.getId());

                ps.executeUpdate();
                conn.commit();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    return new Course(keys.getInt("course_id"), name, category, desc, teacher);
                }
            }
        }
    }

    public Student saveStudentToDatabase(String firstName, String lastName, LocalDate enrollmentDate, Group group)
            throws SQLException {
        String sql = "INSERT INTO students (s_first_name, s_last_name, enrollment_date, group_id) VALUES (?, ?, ?, ?);";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setObject(3, enrollmentDate);
                Integer groupId = group != null ? group.getId() : null;
                ps.setObject(4, groupId);

                ps.executeUpdate();
                conn.commit();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    Student s = new Student(keys.getInt("student_id"), firstName, lastName, enrollmentDate);
                    s.setGroup(group);
                    return s;
                }
            }
        }
    }

    public Student saveStudentToDatabase(String firstName, String lastName, LocalDate enrollmentDate)
            throws SQLException {
        return saveStudentToDatabase(firstName, lastName, enrollmentDate, null);
    }

    public List<Student> findAllStudentsInDatabase() throws SQLException {
        String sql = "SELECT student_id, s_first_name, s_last_name, group_id, enrollment_date "
                + "FROM students";

        List<Student> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            int rowNum = 1;
            while (rs.next()) {
                result.add(studentMapper.mapRow(rs, rowNum++));
            }
        }
        return result;
    }

    public void addStudentsToCourseInDatabase(Course course, Student... students) throws SQLException {
        String sql = "INSERT INTO student_courses (student_id, course_id) VALUES (?, ?);";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Student student : students) {
                    ps.setInt(1, student.getId());
                    ps.setInt(2, course.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        }
    }

    public void addStudentToCoursesInDatabase(Student student, Course... courses) throws SQLException {
        String sql = "INSERT INTO student_courses (student_id, course_id) VALUES (?, ?);";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Course course : courses) {
                    ps.setInt(1, student.getId());
                    ps.setInt(2, course.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        }
    }

    public void addStudentsToGroupInDatabase(Group group, Student... students) throws SQLException {
        String sql = "UPDATE students SET group_id = ? WHERE student_id = ?;";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Student student : students) {
                    ps.setInt(1, group.getId());
                    ps.setInt(2, student.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        }
    }

    public List<Course> findAllStudentCoursesInDatabase(Student student) throws SQLException {
        String sql = "SELECT c.course_id, course_name, course_description, ca.category_id, category_code, "
                + "category_description, t.teacher_id, t_first_name, t_last_name, "
                + "title, date_hired "
                + "FROM courses AS c INNER JOIN teachers AS t ON c.teacher_id = t.teacher_id "
                + "INNER JOIN categories AS ca ON c.category_id = ca.category_id "
                + "INNER JOIN student_courses AS sc ON c.course_id = sc.course_id "
                + "WHERE sc.student_id = ?";

        List<Course> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, student.getId());

            int rowNum = 1;
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(courseMapper.mapRow(rs, rowNum++));
                }
            }
        }
        return result;
    }

    public List<Student> findAllGroupStudentsInDatabase(Group group) throws SQLException {
        String sql = "SELECT s.student_id, s_first_name, s_last_name, enrollment_date "
                + "FROM students AS s INNER JOIN groups AS g ON s.group_id = g.group_id "
                + "WHERE g.group_id = ?";

        List<Student> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, group.getId());

            int rowNum = 1;
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(studentMapper.mapRow(rs, rowNum++));
                }
            }
        }
        return result;
    }

    public List<Course> findAllCoursesInDatabase() throws SQLException {
        String sql = "SELECT c.course_id, course_name, course_description, ca.category_id, category_code, "
                + "category_description, t.teacher_id, t_first_name, t_last_name, "
                + "title, date_hired "
                + "FROM courses AS c INNER JOIN teachers AS t ON c.teacher_id = t.teacher_id "
                + "INNER JOIN categories AS ca ON c.category_id = ca.category_id;";

        List<Course> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            int rowNum = 1;
            while (rs.next()) {
                result.add(courseMapper.mapRow(rs, rowNum++));
            }
        }
        return result;
    }
}
