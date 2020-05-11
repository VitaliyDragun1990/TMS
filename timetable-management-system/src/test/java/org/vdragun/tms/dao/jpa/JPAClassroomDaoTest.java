package org.vdragun.tms.dao.jpa;

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
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import org.vdragun.tms.config.JPADaoConfig;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.dao.ClassroomDao;
import org.vdragun.tms.dao.DBTestHelper;
import org.vdragun.tms.dao.DaoTestConfig;

@SpringJUnitConfig(classes = { JPADaoConfig.class, DaoTestConfig.class })
@DisplayName("JPA Classroom DAO")
@Transactional
public class JPAClassroomDaoTest {

    private static final int CAPACITY = 10;

    @Autowired
    private ClassroomDao dao;

    @Autowired
    private DBTestHelper dbHelper;

    @Test
    void shouldReturnEmptyResultIfNoClassroomWithGivenIdInDatabase() {
        Optional<Classroom> result = dao.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindClassroomById() throws SQLException {
        Classroom expected = dbHelper.saveClassroomToDatabase(CAPACITY);

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

    @Test
    void shouldReturnEmptyListIfNoClassroomAvailable() {
        List<Classroom> result = dao.findAll();

        assertThat(result, hasSize(0));
    }

    @Test
    void sholdFindAllAvailableClassrooms() throws SQLException {
        Classroom classroomA = dbHelper.saveClassroomToDatabase(CAPACITY);
        Classroom classroomB = dbHelper.saveClassroomToDatabase(CAPACITY);

        List<Classroom> result = dao.findAll();

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(classroomA, classroomB));
    }

    @Test
    void shouldReturnFalseIfClassroomWithGivenIdentifierDoesNotExist() {
        boolean result = dao.existsById(1);

        assertFalse(result);
    }

    @Test
    void shouldReturnTrueIfClassroomWithGivenIdentifierExists() throws SQLException {
        Classroom classroom = dbHelper.saveClassroomToDatabase(CAPACITY);

        boolean result = dao.existsById(classroom.getId());

        assertTrue(result);
    }

    private void assertClassroomInDatabase(Classroom classroom) throws SQLException {
        assertThat("classroom should have id", classroom.getId(), is(not(nullValue())));

        List<Classroom> result = dbHelper.findAllClassroomsInDatabase();
        assertThat(result, hasSize(1));
        assertThat(result, containsInAnyOrder(classroom));
    }

}
