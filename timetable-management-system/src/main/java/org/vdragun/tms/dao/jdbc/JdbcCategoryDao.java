package org.vdragun.tms.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${category.insert}")
    private String insertQuery;

    @Value("${category.findById}")
    private String findByIdQuery;

    @Value("${category.findAll}")
    private String findAllQuery;

    @Value("${category.exists}")
    private String existsQuery;

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
            PreparedStatement statement = connection.prepareStatement(insertQuery, new String[] { "category_id" });
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
        List<Category> result = jdbc.query(findByIdQuery, new Object[] { categoryId }, mapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<Category> findAll() {
        return jdbc.query(findAllQuery, mapper);
    }

    @Override
    public boolean existsById(Integer categoryId) {
        return jdbc.queryForObject(existsQuery, new Object[] { categoryId }, Boolean.class);
    }

}
