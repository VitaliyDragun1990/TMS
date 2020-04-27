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
    public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Student(
                rs.getInt("student_id"),
                rs.getString("s_first_name"),
                rs.getString("s_last_name"),
                rs.getObject("enrollment_date", LocalDate.class));
    }

}
