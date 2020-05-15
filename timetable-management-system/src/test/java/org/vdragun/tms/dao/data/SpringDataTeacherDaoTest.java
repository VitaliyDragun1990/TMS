package org.vdragun.tms.dao.data;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.allOf;
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
import static org.vdragun.tms.core.domain.Title.INSTRUCTOR;
import static org.vdragun.tms.core.domain.Title.PROFESSOR;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import org.vdragun.tms.config.SpringDataDaoConfig;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.dao.DBTestHelper;
import org.vdragun.tms.dao.DaoTestConfig;
import org.vdragun.tms.dao.FullName;
import org.vdragun.tms.dao.TeacherDao;

@SpringJUnitConfig(classes = { SpringDataDaoConfig.class, DaoTestConfig.class })
@DisplayName("Spring Data Teacher DAO")
@Transactional
public class SpringDataTeacherDaoTest {
    private static final String SMITH = "Smith";
    private static final String SNOW = "Snow";
    private static final String JACK = "Jack";
    private static final String ANNA = "Anna";
    private static final String MIKE = "Mike";
    private static final String JOHN = "John";
    private static final String PORTER = "Porter";

    private static final LocalDate DATE_HIRED = LocalDate.now();

    private static final String ADVANCED_BIOLOGY = "Advanced Biology";
    private static final String INTERMEDIATE_BIOLOGY = "Intermediate Biology";

    @Autowired
    private TeacherDao dao;

    @Autowired
    private DBTestHelper dbHelper;

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldReturnEmptyResultIfNoTeacherWithGivenIdInDatabase() {
        Optional<Teacher> result = dao.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/teacher_data.sql" })
    void shouldFindTeacherById() {
        Teacher expected = dbHelper.findRandomTeacherInDatabase();

        Optional<Teacher> result = dao.findById(expected.getId());

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(expected));
    }

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldSaveNewTeacherToDatabase() {
        Teacher teacher = new Teacher(MIKE, SMITH, PROFESSOR, DATE_HIRED);

        dao.save(teacher);
        dbHelper.flushChangesToDatabase();

        assertTeachersInDatabase(teacher);
    }

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldSaveSeveralNewTeacherToDatabase() {
        Teacher mike = new Teacher(MIKE, SMITH, PROFESSOR, DATE_HIRED);
        Teacher anna = new Teacher(ANNA, SNOW, INSTRUCTOR, DATE_HIRED);

        dao.saveAll(asList(mike, anna));
        dbHelper.flushChangesToDatabase();

        assertTeachersInDatabase(mike, anna);
    }

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldReturnEmptyListIfNoTeachersInDatabase() {
        List<Teacher> result = dao.findAll();

        assertThat(result, hasSize(0));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/teacher_data.sql" })
    void shouldFilnAllTeachersInDatabase() {
        List<Teacher> result = dao.findAll();

        assertTeachersWithNames(
                result,
                FullName.from(JACK, SMITH), FullName.from(ANNA, SMITH), FullName.from(JOHN, PORTER));
    }

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldReturnEmptyResultIfNoCourseWithGivenIdInDatabase() {
        Optional<Teacher> result = dao.findForCourseWithId(1);

        assertFalse(result.isPresent());
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/teacher_data.sql" })
    void shouldReturnTeacherForCourseWithGivenId() {
        Course advancedBiology = dbHelper.findCourseByNameInDatabase(ADVANCED_BIOLOGY);

        Optional<Teacher> result = dao.findForCourseWithId(advancedBiology.getId());

        assertTrue(result.isPresent());
        assertTeacherCourses(result.get(), ADVANCED_BIOLOGY, INTERMEDIATE_BIOLOGY);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/teacher_data.sql" })
    void shouldReturnTeacherWithAllRelatedCourses() {
        Teacher teacher = dbHelper.findTeacherByNameInDatabase(JACK, SMITH);

        Optional<Teacher> result = dao.findById(teacher.getId());

        assertTrue(result.isPresent());
        assertTeacherCourses(result.get(), ADVANCED_BIOLOGY, INTERMEDIATE_BIOLOGY);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/teacher_data.sql" })
    void shouldReturnTrueIfTeacherWithGivenIdentifierExists() {
        Teacher teacher = dbHelper.findTeacherByNameInDatabase(JACK, SMITH);

        boolean result = dao.existsById(teacher.getId());

        assertTrue(result);
    }

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldReturnFalseIfTeacherWithGivenIdentifierNotExist() {
        boolean result = dao.existsById(1);

        assertFalse(result);
    }

    private void assertTeachersWithNames(List<Teacher> result, FullName... expectedNames) {
        assertThat(result, hasSize(expectedNames.length));
        for (FullName expectedName : expectedNames) {
            assertThat(result, hasItem(allOf(
                    hasProperty("firstName", equalTo(expectedName.getFirstName())),
                    hasProperty("lastName", equalTo(expectedName.getLastName())))));
        }
    }

    private void assertTeacherCourses(Teacher teacher, String... expectedNames) {
        assertThat(teacher.getCourses(), hasSize(expectedNames.length));
        for (String expectedName : expectedNames) {
            assertThat(teacher.getCourses(), hasItem(hasProperty("name", equalTo(expectedName))));
        }
    }

    private void assertTeachersInDatabase(Teacher... expected) {
        Arrays.stream(expected)
                .forEach(teacher -> assertThat("teacher should have id", teacher.getId(), is(not(nullValue()))));

        List<Teacher> result = dbHelper.findAllTeachersInDatabase();
        assertThat(result, hasItems(expected));
    }

}
