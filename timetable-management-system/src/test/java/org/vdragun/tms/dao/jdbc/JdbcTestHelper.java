package org.vdragun.tms.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Classroom;

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

    public JdbcTestHelper(DataSource dataSource) {
        this.dataSource = dataSource;
        classroomMapper = new ClassroomMapper();
        categoryMapper = new CategoryMapper();
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
}
