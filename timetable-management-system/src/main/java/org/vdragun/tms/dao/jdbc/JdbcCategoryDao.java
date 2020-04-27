package org.vdragun.tms.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.dao.CategoryDao;
import org.vdragun.tms.dao.DaoException;

/**
 * Implementation of ({@link CategoryDao} using Spring JDBC
 * 
 * @author Vitaliy Dragun
 *
 */
@Repository
public class JdbcCategoryDao implements CategoryDao {

    private static final String INSERT_QUERY = "INSERT INTO categories (category_code, category_description) "
            + "VALUES (?, ?);";
    private static final String FIND_BY_ID_QUERY = "SELECT category_id, category_code, category_description "
            + "FROM categories WHERE category_id = ?;";
    private static final String FIND_ALL_QUERY = "SELECT category_id, category_code, category_description "
            + "FROM categories;";

    private JdbcTemplate jdbc;
    private CategoryMapper mapper;

    public JdbcCategoryDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        mapper = new CategoryMapper();
    }

    @Override
    public void save(Category category) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsInserted = jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, new String[] { "category_id" });
            statement.setString(1, category.getCode());
            statement.setString(2, category.getDescription());
            return statement;
        }, keyHolder);

        if (rowsInserted != 1) {
            throw new DaoException("Fail to save new category to the database");
        }

        int categoryId = keyHolder.getKey().intValue();
        category.setId(categoryId);

    }

    @Override
    public void saveAll(List<Category> categories) {
        // use this approach because there is no way to retrieve auto-generated keys
        // using jdbcTemplate.batchUpdate(..) method
        categories.forEach(this::save);
    }

    @Override
    public Optional<Category> findById(Integer categoryId) {
        List<Category> result = jdbc.query(FIND_BY_ID_QUERY, new Object[] { categoryId }, mapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<Category> findAll() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

}
