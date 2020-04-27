package org.vdragun.tms.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.jdbc.core.RowMapper;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Title;

/**
 * Implementation of {@link RowMapper} to map result set to {@link Teacher}
 * instance
 * 
 * @author Vitaliy Dragun
 *
 */
public class TeacherMapper implements RowMapper<Teacher> {

    @Override
    public Teacher mapRow(ResultSet rs, int rowNum) throws SQLException {
        return mapTeacher(rs);
    }

    private Teacher mapTeacher(ResultSet rs) throws SQLException {
        int id = rs.getInt("teacher_id");
        String firstName = rs.getString("t_first_name");
        String lastName = rs.getString("t_last_name");
        Title title = Title.fromString(rs.getString("title"));
        LocalDate dateHired = rs.getObject("date_hired", LocalDate.class);

        return new Teacher(id, firstName, lastName, title, dateHired);
    }

}
