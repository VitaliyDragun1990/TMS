package org.vdragun.tms.dao.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.dao.ClassroomDao;
import org.vdragun.tms.dao.DBTestHelper;
import org.vdragun.tms.dao.DaoTestConfig;

@DataJpaTest
@Import({ DaoTestConfig.class })
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DisplayName("Spring Data Classroom DAO")
public class SpringDataClassroomDaoTest {

    private static final int CAPACITY_TEN = 10;

    @Autowired
    private ClassroomDao dao;

    @Autowired
    private DBTestHelper dbHelper;

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldReturnEmptyResultIfNoClassroomWithGivenIdInDatabase() {
        Optional<Classroom> result = dao.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/classroom_data.sql" })
    void shouldFindClassroomById() {
        Classroom expected = dbHelper.findRandomClassroomInDatabase();

        Optional<Classroom> result = dao.findById(expected.getId());

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(expected));
    }

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldSaveClassroomToDatabase() {
        Classroom classroom = new Classroom(CAPACITY_TEN);

        dao.save(classroom);
        dbHelper.flushChangesToDatabase();

        assertClassroomInDatabase(classroom);
    }

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldReturnEmptyListIfNoClassroomAvailable() {
        List<Classroom> result = dao.findAll();

        assertThat(result, hasSize(0));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/classroom_data.sql" })
    void sholdFindAllAvailableClassrooms() {
        List<Classroom> result = dao.findAll();

        assertThat(result, hasSize(2));
    }

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldReturnFalseIfClassroomWithGivenIdentifierDoesNotExist() {
        boolean result = dao.existsById(1);

        assertFalse(result);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/classroom_data.sql" })
    void shouldReturnTrueIfClassroomWithGivenIdentifierExists() {
        Classroom classroom = dbHelper.findRandomClassroomInDatabase();

        boolean result = dao.existsById(classroom.getId());

        assertTrue(result);
    }

    private void assertClassroomInDatabase(Classroom classroom) {
        assertThat("classroom should have id", classroom.getId(), is(not(nullValue())));

        List<Classroom> result = dbHelper.findAllClassroomsInDatabase();
        assertThat(result, hasItems(classroom));
    }

}
