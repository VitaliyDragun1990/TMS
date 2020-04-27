package org.vdragun.tms.dao.jdbc;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

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
    
    private static final String INSERT_QUERY =
            "INSERT INTO timetables (start_date_time, duration, classroom_id, course_id, teacher_id) "
            + "VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_ALL_QUERY = 
            "SELECT timetable_id, start_date_time, duration, tm.classroom_id, tm.course_id, tm.teacher_id, "
            + "t_first_name, t_last_name, title, date_hired, capacity, course_name, course_description, "
            + "ca.category_id, category_code, category_description "
            + "FROM timetables AS tm "
            + "INNER JOIN classrooms AS cr ON tm.classroom_id = cr.classroom_id "
            + "INNER JOIN teachers AS t ON tm.teacher_id = t.teacher_id "
            + "INNER JOIN courses AS c ON tm.course_id = c.course_id "
            + "INNER JOIN categories AS ca ON c.category_id = ca.category_id ";
    private static final String FIND_BY_ID_QUERY = FIND_ALL_QUERY + "WHERE timetable_id = ?";
    private static final String FIND_FOR_STUDENT_QUERY =
            FIND_ALL_QUERY 
            + "INNER JOIN student_courses AS sc ON c.course_id = sc.course_id "
            + "INNER JOIN students AS s ON sc.student_id = s.student_id "
            + "WHERE s.student_id = ? ";
    private static final String DAY_PREDICATE = "AND EXTRACT(DOY FROM start_date_time) = ?";
    private static final String MONTH_PREDICATE = "AND EXTRACT(MONTH FROM start_date_time) = ?";
    private static final String FIND_FOR_STUDENT_DAILY_QUERY = FIND_FOR_STUDENT_QUERY + DAY_PREDICATE;
    private static final String FIND_FOR_STUDENT_MONTHLY_QUERY = FIND_FOR_STUDENT_QUERY + MONTH_PREDICATE;
    private static final String FIND_FOR_TEACHER_QUERY = FIND_ALL_QUERY + "WHERE tm.teacher_id = ? ";
    private static final String FIND_FOR_TEACHER_DAILY_QUERY = FIND_FOR_TEACHER_QUERY + DAY_PREDICATE;
    private static final String FIND_FOR_TEACHER_MONTHLY_QUERY = FIND_FOR_TEACHER_QUERY + MONTH_PREDICATE;

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
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, new String[] { "timetable_id" });
            ps.setObject(1, timetable.getStartTime());
            ps.setInt(2, timetable.getDurationInMinutes());
            ps.setInt(3, timetable.getClassroom().getId());
            ps.setInt(4, timetable.getCourse().getId());
            ps.setInt(5, timetable.getTeacher().getId());
            return ps;
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
        List<Timetable> result = jdbc.query(FIND_BY_ID_QUERY, new Object[] { timetableId }, timetableMapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<Timetable> findAll() {
        return jdbc.query(FIND_ALL_QUERY, timetableMapper);
    }

    @Override
    public List<Timetable> findDailyForStudent(Integer studentId, LocalDate date) {
        return jdbc.query(
                FIND_FOR_STUDENT_DAILY_QUERY,
                new Object[] { studentId, date.getDayOfYear() },
                timetableMapper);
    }

    @Override
    public List<Timetable> findDailyForTeacher(Integer teacherId, LocalDate date) {
        return jdbc.query(
                FIND_FOR_TEACHER_DAILY_QUERY,
                new Object[] { teacherId, date.getDayOfYear() },
                timetableMapper);
    }

    @Override
    public List<Timetable> findMonthlyForStudent(Integer studentId, Month month) {
        return jdbc.query(
                FIND_FOR_STUDENT_MONTHLY_QUERY,
                new Object[] { studentId, month.getValue() },
                timetableMapper);
    }

    @Override
    public List<Timetable> findMonthlyForTeacher(Integer teacherId, Month month) {
        return jdbc.query(
                FIND_FOR_TEACHER_MONTHLY_QUERY,
                new Object[] { teacherId, month.getValue() },
                timetableMapper);
    }

}
