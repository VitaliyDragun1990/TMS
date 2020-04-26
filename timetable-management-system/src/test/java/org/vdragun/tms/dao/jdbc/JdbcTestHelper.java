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

    public JdbcTestHelper(DataSource dataSource) {
        this.dataSource = dataSource;
        classroomMapper = new ClassroomMapper();
        categoryMapper = new CategoryMapper();
        groupMapper = new GroupMapper();
        teacherMapper = new TeacherMapper();
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
        String sql = "INSERT INTO teachers (first_name, last_name, title, date_hired) VALUES (?, ?, ?, ?);";

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
        String sql = "SELECT teacher_id, first_name, last_name, title, date_hired "
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
}
