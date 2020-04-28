package org.vdragun.tms.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.vdragun.tms.core.domain.Category;

/**
 * Implementation of {@link RowMapper} to map result set to {@link Category}
 * instance
 * 
 * @author Vitaliy Dragun
 *
 */
public class CategoryMapper implements RowMapper<Category> {

    @Override
    public Category mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new Category(
                resultSet.getInt("category_id"),
                resultSet.getString("category_code"),
                resultSet.getString("category_description"));
    }

}
