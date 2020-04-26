package org.vdragun.tms.dao.jdbc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.vdragun.tms.config.DaoConfig;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.dao.ClassroomDao;

@SpringJUnitConfig(classes = { DaoConfig.class, TestDaoConfig.class })
@Sql(scripts = { "/sql/db_schema.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Jdbc Classroom DAO")
public class JdbcClassroomDaoTest {

    private static final int CAPACITY = 10;

    @Autowired
    private ClassroomDao dao;

    @Autowired
    private JdbcTestHelper jdbcHelper;

    @Test
    void shouldReturnEmptyResultIfNoClassroomWithGivenIdInDatabase() {
        Optional<Classroom> result = dao.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindClassroomById() throws SQLException {
        Classroom expected = jdbcHelper.saveClassroomToDatabase(CAPACITY);

        Optional<Classroom> result = dao.findById(expected.getId());

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(expected));
    }

    @Test
    void shouldSaveClassroomToDatabase() throws SQLException {
        Classroom classroom = new Classroom(10);

        dao.save(classroom);

        assertClassroomInDatabase(classroom);
    }

    private void assertClassroomInDatabase(Classroom classroom) throws SQLException {
        assertThat("classroom should have id", classroom.getId(), is(not(nullValue())));

        List<Classroom> result = jdbcHelper.findAllClassroomsInDatabase();
        assertThat(result, hasSize(1));
        assertThat(result, contains(classroom));
    }

}
