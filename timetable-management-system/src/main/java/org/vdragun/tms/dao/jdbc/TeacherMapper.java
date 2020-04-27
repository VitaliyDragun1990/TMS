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
    public Teacher mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("teacher_id");
        String firstName = resultSet.getString("t_first_name");
        String lastName = resultSet.getString("t_last_name");
        Title title = Title.fromString(resultSet.getString("title"));
        LocalDate dateHired = resultSet.getObject("date_hired", LocalDate.class);
        
        return new Teacher(id, firstName, lastName, title, dateHired);
    }

}
