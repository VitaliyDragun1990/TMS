package org.vdragun.tms.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.dao.DaoException;
import org.vdragun.tms.dao.GroupDao;

/**
 * Implementation of ({@link GroupDao} using Spring JDBC
 * 
 * @author Vitaliy Dragun
 *
 */
@Repository
public class JdbcGroupDao implements GroupDao {

    private static final String INSERT_QUERY = "INSERT INTO groups (group_name) VALUES (?);";
    private static final String FIND_BY_ID_QUERY = "SELECT group_id, group_name FROM groups WHERE group_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT group_id, group_name FROM groups";

    private JdbcTemplate jdbc;
    private GroupMapper mapper;

    public JdbcGroupDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        mapper = new GroupMapper();
    }

    @Override
    public void save(Group group) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsInserted = jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, new String[] { "group_id" });
            statement.setString(1, group.getName());
            return statement;
        }, keyHolder);

        if (rowsInserted != 1) {
            throw new DaoException("Fail to save new group to the database");
        }

        int groupId = keyHolder.getKey().intValue();
        group.setId(groupId);
    }

    @Override
    public void saveAll(List<Group> groups) {
        // use this approach because there is no way to retrieve auto-generated keys
        // using jdbcTemplate.batchUpdate(..) method
        groups.forEach(this::save);
    }

    @Override
    public Optional<Group> findById(int groupId) {
        List<Group> result = jdbc.query(FIND_BY_ID_QUERY, new Object[] { groupId }, mapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<Group> findAll() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

}
