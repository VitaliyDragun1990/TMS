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
import org.vdragun.tms.dao.CourseDao;

@SpringJUnitConfig(classes = { DaoConfig.class, TestDaoConfig.class })
@Sql(scripts = { "/sql/db_schema.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Jdbc Course DAO")
public class JdbcCourseDaoTest {

    private static final String COURSE_DESCRIPTION = "Course description";
    private static final String COURSE_BIO_25 = "bio-25";
    private static final String COURSE_BIO_10 = "bio-10";
    private static final String DESC_BIOLOGY = "Biology";
    private static final String DESC_ART = "Art";
    private static final String CODE_BIO = "BIO";
    private static final String CODE_ART = "ART";
    private static final String SMITH = "Smith";
    private static final String JACK = "Jack";

    @Autowired
    private CourseDao dao;

    @Autowired
    private JdbcTestHelper jdbcHelper;

    @Test
    void shouldReturnEmptyResultIfNoCourseWithGivenIdInDatabase() {
        Optional<Course> result = dao.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindCourseById() throws SQLException {
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESC_BIOLOGY);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, SMITH, PROFESSOR, LocalDate.now());
        Course expected = jdbcHelper.saveCourseToDatabase(COURSE_BIO_25, COURSE_DESCRIPTION, category, teacher);

        Optional<Course> result = dao.findById(expected.getId());

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(expected));
    }

    @Test
    void shouldSaveNewCourseToDatabase() throws SQLException {
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESC_BIOLOGY);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, SMITH, PROFESSOR, LocalDate.now());
        Course course = new Course(COURSE_BIO_25, category, teacher);

        dao.save(course);

        assertCoursesInDatabase(course);
    }

    @Test
    void shouldSaveSeveralNewCoursesToDatabase() throws SQLException {
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESC_BIOLOGY);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, SMITH, PROFESSOR, LocalDate.now());
        Course bio25 = new Course(COURSE_BIO_25, category, teacher);
        Course bio10 = new Course(COURSE_BIO_10, category, teacher);

        dao.saveAll(asList(bio25, bio10));

        assertCoursesInDatabase(bio10, bio25);
    }

    @Test
    void shouldReturnEmptyListIfNoCoursesInDatabase() {
        List<Course> result = dao.findAll();

        assertThat(result, hasSize(0));
    }

    @Test
    void shouldFindAllCoursesInDatabase() throws SQLException {
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESC_BIOLOGY);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, SMITH, PROFESSOR, LocalDate.now());
        Course bio25 = jdbcHelper.saveCourseToDatabase(COURSE_BIO_25, COURSE_DESCRIPTION, category, teacher);
        Course bio10 = jdbcHelper.saveCourseToDatabase(COURSE_BIO_10, COURSE_DESCRIPTION, category, teacher);

        List<Course> result = dao.findAll();

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(bio25, bio10));
    }

    @Test
    void shouldReturnEmptyListIfNoCourseWithSpecifiedCategory() throws SQLException {
        Category categoryBio = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESC_BIOLOGY);
        Category categoryArt = jdbcHelper.saveCategoryToDatabase(CODE_ART, DESC_ART);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, SMITH, PROFESSOR, LocalDate.now());
        jdbcHelper.saveCourseToDatabase(COURSE_BIO_25, COURSE_DESCRIPTION, categoryBio, teacher);
        jdbcHelper.saveCourseToDatabase(COURSE_BIO_10, COURSE_DESCRIPTION, categoryBio, teacher);

        List<Course> result = dao.findByCategory(categoryArt.getId());

        assertThat(result, hasSize(0));
    }

    @Test
    void shouldFindAllCoursesForSpecifiedCategory() throws SQLException {
        Category categoryBio = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESC_BIOLOGY);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, SMITH, PROFESSOR, LocalDate.now());
        Course bio25 = jdbcHelper.saveCourseToDatabase(COURSE_BIO_25, COURSE_DESCRIPTION, categoryBio, teacher);
        Course bio10 = jdbcHelper.saveCourseToDatabase(COURSE_BIO_10, COURSE_DESCRIPTION, categoryBio, teacher);

        List<Course> result = dao.findByCategory(categoryBio.getId());

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(bio25, bio10));
    }

    private void assertCoursesInDatabase(Course... expected) throws SQLException {
        Arrays.stream(expected)
                .forEach(course -> assertThat("course should have id", course.getId(), is(not(nullValue()))));

        List<Course> result = jdbcHelper.findAllCoursesInDatabase();
        assertThat(result, hasSize(expected.length));
        assertThat(result, containsInAnyOrder(expected));
    }

}
