package org.vdragun.tms.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.vdragun.tms.core.domain.Group;

/**
 * Implementation of {@link RowMapper} to map result set to {@link Group}
 * instance
 * 
 * @author Vitaliy Dragun
 *
 */
public class GroupMapper implements RowMapper<Group> {

    @Override
    public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Group(rs.getInt("group_id"), rs.getString("group_name"));
    }

}
