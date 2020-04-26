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
    public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Category(
                rs.getInt("category_id"),
                rs.getString("category_code"),
                rs.getString("category_description"));
    }

}
