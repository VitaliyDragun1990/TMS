package org.vdragun.tms.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.jdbc.core.RowMapper;
import org.vdragun.tms.core.domain.Student;

/**
 * Implementation of {@link RowMapper} to map result set to {@link Student}
 * instance
 * 
 * @author Vitaliy Dragun
 *
 */
public class StudentMapper implements RowMapper<Student> {

    @Override
    public Student mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new Student(
                resultSet.getInt("student_id"),
                resultSet.getString("s_first_name"),
                resultSet.getString("s_last_name"),
                resultSet.getObject("enrollment_date", LocalDate.class));
    }

}
