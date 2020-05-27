package org.vdragun.tms.dao.data;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.vdragun.tms.config.SpringDataDaoConfig;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.dao.DBTestHelper;
import org.vdragun.tms.dao.DaoTestConfig;
import org.vdragun.tms.dao.GroupDao;

@DataJpaTest
@Import({ SpringDataDaoConfig.class, DaoTestConfig.class })
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DisplayName("Spring Data Group DAO")
public class SpringDataGroupDaoTest {
    private static final String MH_TEN = "mh-10";
    private static final String PS_TWENTY = "ps-20";
    private static final String PH_THIRTY = "ph-30";

    @Autowired
    private GroupDao dao;

    @Autowired
    private DBTestHelper dbHelper;

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldReturnEmptyResultIfNoGroupWithGivenIdInDatabase() {
        Optional<Group> result = dao.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/group_data.sql" })
    void shouldFindExistingGroupById() {
        Group expected = dbHelper.findGroupByNameInDatabase(PS_TWENTY);

        Optional<Group> result = dao.findById(expected.getId());

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(expected));
    }

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldSaveNewGroupToDatabase() {
        Group group = new Group(PS_TWENTY);

        dao.save(group);
        dbHelper.flushChangesToDatabase();

        assertGroupsInDatabase(group);
    }

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldSaveSeveralNewGroupToDatabase() {
        Group psTwentyFive = new Group(PS_TWENTY);
        Group mhTen = new Group(MH_TEN);

        dao.saveAll(asList(psTwentyFive, mhTen));
        dbHelper.flushChangesToDatabase();

        assertGroupsInDatabase(psTwentyFive, mhTen);
    }

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldReturnEmptyListIfNoGroupsInDatabase() {
        List<Group> result = dao.findAll();

        assertThat(result, hasSize(0));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/group_data.sql" })
    void shouldFindAllGroupInstancesInDatabase() {
        List<Group> result = dao.findAll();

        assertContainsGroupsWithNames(result, MH_TEN, PS_TWENTY, PH_THIRTY);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/group_data.sql" })
    void shouldReturnTrueIfGroupWithGivenIdentifierExists() {
        Group group = dbHelper.findGroupByNameInDatabase(PS_TWENTY);

        boolean result = dao.existsById(group.getId());

        assertTrue(result);
    }

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldReturnFalseIfGroupWithGivenIdentifierNotExist() {
        boolean result = dao.existsById(1);

        assertFalse(result);
    }

    private void assertContainsGroupsWithNames(List<Group> result, String... expectedNames) {
        assertThat(result, hasSize(expectedNames.length));
        for (String expectedName : expectedNames) {
            assertThat(result, hasItem(hasProperty("name", equalTo(expectedName))));
        }
    }

    private void assertGroupsInDatabase(Group... expected) {
        Arrays.stream(expected)
                .forEach(group -> assertThat("group should have id", group.getId(), is(not(nullValue()))));

        List<Group> result = dbHelper.findAllGroupsInDatabase();
        assertThat(result, hasItems(expected));
    }

}
