package org.vdragun.tms.dao.jdbc;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.vdragun.tms.config.DaoConfig;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.dao.GroupDao;

@SpringJUnitConfig(classes = { DaoConfig.class, TestDaoConfig.class })
@Sql(scripts = { "/sql/db_schema.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Jdbc Group DAO")
public class JdbcGroupDaoTest {

    private static final String MH_10 = "mh-10";
    private static final String PS_25 = "ps-25";

    @Autowired
    private GroupDao dao;

    @Autowired
    private JdbcTestHelper jdbcHelper;

    @Test
    void shouldReturnEmptyResultIfNoGroupWithGivenIdInDatabase() {
        Optional<Group> result = dao.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindExistingGroupById() throws SQLException {
        Group expected = jdbcHelper.saveGroupToDatabase(PS_25);

        Optional<Group> result = dao.findById(expected.getId());

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(expected));
    }

    @Test
    void shouldSaveNewGroupToDatabase() throws SQLException {
        Group ps25 = new Group(PS_25);

        dao.save(ps25);

        assertGroupsInDatabase(ps25);
    }

    @Test
    void shouldSaveSeveralNewGroupToDatabase() throws SQLException {
        Group ps25 = new Group(PS_25);
        Group mh10 = new Group(MH_10);

        dao.saveAll(asList(ps25, mh10));

        assertGroupsInDatabase(ps25, mh10);
    }

    @Test
    void shouldReturnEmptyListIfNoGroupsInDatabase() {
        List<Group> result = dao.findAll();

        assertThat(result, hasSize(0));
    }

    @Test
    void shouldFindAllGroupInstancesInDatabase() throws SQLException {
        Group ps25 = jdbcHelper.saveGroupToDatabase(PS_25);
        Group mh10 = jdbcHelper.saveGroupToDatabase(MH_10);

        List<Group> result = dao.findAll();

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(ps25, mh10));
    }

    private void assertGroupsInDatabase(Group... expected) throws SQLException {
        Arrays.stream(expected)
                .forEach(group -> assertThat("group should have id", group.getId(), is(not(nullValue()))));

        List<Group> result = jdbcHelper.findAllGroupsInDatabase();
        assertThat(result, hasSize(expected.length));
        assertThat(result, containsInAnyOrder(expected));
    }

}
