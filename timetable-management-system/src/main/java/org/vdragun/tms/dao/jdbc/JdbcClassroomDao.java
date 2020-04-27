package org.vdragun.tms.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.dao.ClassroomDao;
import org.vdragun.tms.dao.DaoException;

/**
 * Implementation of {@link ClassroomDao} using Spring JDBC
 * 
 * @author Vitaliy Dragun
 *
 */
@Repository
public class JdbcClassroomDao implements ClassroomDao {

    private static final String INSERT_QUERY = "INSERT INTO classrooms (capacity) VALUES (?);";
    private static final String FIND_BY_ID_QUERY =
            "SELECT classroom_id, capacity FROM classrooms WHERE classroom_id = ?;";

    private JdbcTemplate jdbc;
    private ClassroomMapper mapper;

    public JdbcClassroomDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        mapper = new ClassroomMapper();
    }

    @Override
    public void save(Classroom classroom) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsInserted = jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, new String[] { "classroom_id" });
            statement.setInt(1, classroom.getCapacity());
            return statement;
        }, keyHolder);

        if (rowsInserted != 1) {
            throw new DaoException("Fail to save new classroom to the database");
        }

        int classroomId = keyHolder.getKey().intValue();
        classroom.setId(classroomId);
    }

    @Override
    public Optional<Classroom> findById(Integer classroomId) {
        List<Classroom> result = jdbc.query(FIND_BY_ID_QUERY, new Object[] { classroomId }, mapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }
}
