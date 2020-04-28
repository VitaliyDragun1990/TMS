package org.vdragun.tms.dao.jdbc;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.dao.DaoException;
import org.vdragun.tms.dao.TimetableDao;

/**
 * Implementation of {@link TimetableDao} using Spring JDBC
 * 
 * @author Vitaliy Dragun
 *
 */
@Repository
public class JdbcTimetableDao implements TimetableDao {
    
    @Value("${timetable.insert}")
    private String insertQuery;

    @Value("${timetable.findAll}")
    private String findAllQuery;

    @Value("${timetable.findById}")
    private String findByIdQuery;

    @Value("${timetable.findForStudentDaily}")
    private String findForStudentDailyQuery;

    @Value("${timetable.findForStudentMonthly}")
    private String findForStudentMonthlyQuery;

    @Value("${timetable.findForTeacherDaily}")
    private String findForTeacherDailyQuery;

    @Value("${timetable.findForTeacherMonthly}")
    private String findForTeacherMonthlyQuery;

    private JdbcTemplate jdbc;
    private TimetableMapper timetableMapper;

    public JdbcTimetableDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        timetableMapper = new TimetableMapper();
    }

    @Override
    public void save(Timetable timetable) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsInserted = jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(insertQuery, new String[] { "timetable_id" });
            statement.setObject(1, timetable.getStartTime());
            statement.setInt(2, timetable.getDurationInMinutes());
            statement.setInt(3, timetable.getClassroom().getId());
            statement.setInt(4, timetable.getCourse().getId());
            statement.setInt(5, timetable.getTeacher().getId());
            return statement;
        }, keyHolder);

        if (rowsInserted != 1) {
            throw new DaoException("Fail to save new timetable to the database");
        }

        int timetableId = keyHolder.getKey().intValue();
        timetable.setId(timetableId);
    }

    @Override
    public void saveAll(List<Timetable> timetables) {
        // use this approach because there is no way to retrieve auto-generated keys
        // using jdbcTemplate.batchUpdate(..) method
        timetables.forEach(this::save);
    }

    @Override
    public Optional<Timetable> findById(Integer timetableId) {
        List<Timetable> result = jdbc.query(findByIdQuery, new Object[] { timetableId }, timetableMapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<Timetable> findAll() {
        return jdbc.query(findAllQuery, timetableMapper);
    }

    @Override
    public List<Timetable> findDailyForStudent(Integer studentId, LocalDate date) {
        return jdbc.query(
                findForStudentDailyQuery,
                new Object[] { studentId, date.getDayOfYear() },
                timetableMapper);
    }

    @Override
    public List<Timetable> findDailyForTeacher(Integer teacherId, LocalDate date) {
        return jdbc.query(
                findForTeacherDailyQuery,
                new Object[] { teacherId, date.getDayOfYear() },
                timetableMapper);
    }

    @Override
    public List<Timetable> findMonthlyForStudent(Integer studentId, Month month) {
        return jdbc.query(
                findForStudentMonthlyQuery,
                new Object[] { studentId, month.getValue() },
                timetableMapper);
    }

    @Override
    public List<Timetable> findMonthlyForTeacher(Integer teacherId, Month month) {
        return jdbc.query(
                findForTeacherMonthlyQuery,
                new Object[] { teacherId, month.getValue() },
                timetableMapper);
    }

}
