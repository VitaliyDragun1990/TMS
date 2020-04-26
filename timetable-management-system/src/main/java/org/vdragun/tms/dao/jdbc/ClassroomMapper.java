package org.vdragun.tms.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.vdragun.tms.core.domain.Classroom;

/**
 * Implementation of {@link RowMapper} to map result set to {@link Classroom}
 * instance
 * 
 * @author Vitaliy Dragun
 *
 */
public class ClassroomMapper implements RowMapper<Classroom> {

    @Override
    public Classroom mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Classroom(rs.getInt("classroom_id"), rs.getInt("capacity"));
    }

}
