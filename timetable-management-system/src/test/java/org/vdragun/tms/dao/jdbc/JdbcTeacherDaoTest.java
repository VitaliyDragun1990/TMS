package org.vdragun.tms.dao.jdbc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.vdragun.tms.core.domain.Title.INSTRUCTOR;
import static org.vdragun.tms.core.domain.Title.PROFESSOR;

import java.sql.SQLException;
import java.time.LocalDate;
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
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.dao.TeacherDao;


@SpringJUnitConfig(classes = { DaoConfig.class, TestDaoConfig.class })
@Sql(scripts = { "/sql/db_schema.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Jdbc Teacher DAO")
public class JdbcTeacherDaoTest {

    private static final String DESC_BIO = "any description";
    private static final String COURSE_BIO_25 = "bio-25";
    private static final String COURSE_BIO_10 = "bio-10";
    private static final String CAT_DESC = "Biology";
    private static final String CAT_CODE = "BIO";
    private static final String SMITH = "Smith";
    private static final String SNOW = "Snow";
    private static final String JACK = "Jack";
    private static final String ANNA = "Anna";
    private static final LocalDate DATE_HIRED = LocalDate.now();

    @Autowired
    private TeacherDao dao;

    @Autowired
    private JdbcTestHelper jdbcHelper;

    @Test
    void shouldReturnEmptyResultIfNoTeacherWithGivenIdInDatabase() {
        Optional<Teacher> result = dao.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindTeacherById() throws SQLException {
        Teacher expected = jdbcHelper.saveTeacherToDatabase(JACK, SMITH, PROFESSOR, DATE_HIRED);

        Optional<Teacher> result = dao.findById(expected.getId());

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(expected));
    }

    @Test
    void shouldSaveNewTeacherToDatabase() throws SQLException {
        Teacher teacher = new Teacher(JACK, SMITH, PROFESSOR, DATE_HIRED);

        dao.save(teacher);

        assertTeachersInDatabase(teacher);
    }

    @Test
    void shouldSaveSeveralNewTeacherToDatabase() throws SQLException {
        Teacher jack = new Teacher(JACK, SMITH, PROFESSOR, DATE_HIRED);
        Teacher anna = new Teacher(ANNA, SNOW, INSTRUCTOR, DATE_HIRED);

        dao.saveAll(Arrays.asList(jack, anna));

        assertTeachersInDatabase(jack, anna);
    }

    @Test
    void shouldReturnEmptyListIfNoTeachersInDatabase() {
        List<Teacher> result = dao.findAll();

        assertThat(result, hasSize(0));
    }

    @Test
    void shouldFilnAllTeachersInDatabase() throws SQLException {
        Teacher jack = jdbcHelper.saveTeacherToDatabase(JACK, SMITH, PROFESSOR, DATE_HIRED);
        Teacher anna = jdbcHelper.saveTeacherToDatabase(ANNA, SNOW, INSTRUCTOR, DATE_HIRED);

        List<Teacher> result = dao.findAll();

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(jack, anna));
    }

    @Test
    void shouldReturnEmptyResultIfNoCourseWithGivenIdInDatabase() {
        Optional<Teacher> result = dao.findForCourse(1);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnTeacherForCourseWithGivenId() throws SQLException {
        Category courseCategory = jdbcHelper.saveCategoryToDatabase(CAT_CODE, CAT_DESC);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, SMITH, PROFESSOR, DATE_HIRED);
        Course course = jdbcHelper.saveCourseToDatabase(COURSE_BIO_25, DESC_BIO, courseCategory, teacher);

        Optional<Teacher> result = dao.findForCourse(course.getId());

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(teacher));
    }

    @Test
    void shouldReturnTeacherWithAllRelatedCourses() throws SQLException {
        Category courseCategory = jdbcHelper.saveCategoryToDatabase(CAT_CODE, CAT_DESC);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, SMITH, PROFESSOR, DATE_HIRED);
        Course bio25 = jdbcHelper.saveCourseToDatabase(COURSE_BIO_25, DESC_BIO, courseCategory, teacher);
        Course bio10 = jdbcHelper.saveCourseToDatabase(COURSE_BIO_10, DESC_BIO, courseCategory, teacher);

        Optional<Teacher> result = dao.findById(teacher.getId());

        assertTeacherCourses(result.get(), bio25, bio10);
    }

    private void assertTeacherCourses(Teacher teacher, Course... expected) {
        assertThat(teacher.getCourses(), hasSize(expected.length));
        assertThat(teacher.getCourses(), containsInAnyOrder(expected));
    }

    private void assertTeachersInDatabase(Teacher... expected) throws SQLException {
        Arrays.stream(expected)
                .forEach(teacher -> assertThat("teacher should have id", teacher.getId(), is(not(nullValue()))));

        List<Teacher> result = jdbcHelper.findAllTeachersInDatabase();
        assertThat(result, hasSize(expected.length));
        assertThat(result, containsInAnyOrder(expected));
    }

}
