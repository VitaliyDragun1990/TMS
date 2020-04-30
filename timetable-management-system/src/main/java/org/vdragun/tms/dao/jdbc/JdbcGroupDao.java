package org.vdragun.tms.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    private static final Logger LOG = LoggerFactory.getLogger(JdbcGroupDao.class);

    @Value("${group.insert}")
    private String insertQuery;

    @Value("${group.findById}")
    private String findByIdQuery;

    @Value("${group.findAll}")
    private String findAllQuery;

    @Value("${group.exists}")
    private String existsQuery;

    private JdbcTemplate jdbc;
    private GroupMapper mapper;

    public JdbcGroupDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        mapper = new GroupMapper();
    }

    @Override
    public void save(Group group) {
        LOG.debug("Saving new group to the database: {}", group);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsInserted = jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(insertQuery, new String[] { "group_id" });
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
        LOG.debug("Saving {} new groups to the database", groups.size());
        // use this approach because there is no way to retrieve auto-generated keys
        // using jdbcTemplate.batchUpdate(..) method
        groups.forEach(this::save);
    }

    @Override
    public Optional<Group> findById(int groupId) {
        LOG.debug("Searching for group with id={} in the database", groupId);
        List<Group> result = jdbc.query(findByIdQuery, new Object[] { groupId }, mapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<Group> findAll() {
        LOG.debug("Retrieving all groups from the database");
        return jdbc.query(findAllQuery, mapper);
    }

    @Override
    public boolean existsById(Integer groupId) {
        LOG.debug("Checking whether group with id={} exists in the database", groupId);
        return jdbc.queryForObject(existsQuery, Boolean.class, groupId);
    }

}
