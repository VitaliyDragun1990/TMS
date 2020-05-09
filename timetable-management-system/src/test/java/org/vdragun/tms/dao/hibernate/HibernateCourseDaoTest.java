package org.vdragun.tms.dao.hibernate;

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
import org.vdragun.tms.config.HibernateDaoConfig;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.dao.CourseDao;
import org.vdragun.tms.dao.jdbc.DBTestConfig;
import org.vdragun.tms.dao.jdbc.JdbcTestHelper;

@SpringJUnitConfig(classes = { HibernateDaoConfig.class, DBTestConfig.class })
@Sql(scripts = { "/sql/db_schema_seq.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Hibernate Course DAO")
public class HibernateCourseDaoTest {
    private static final String COURSE_DESCR = "Course description";
    private static final String BIO_TWENTY_FIVE = "bio-25";
    private static final String BIO_TEN = "bio-10";
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
        Course expected = jdbcHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, category, teacher);

        Optional<Course> result = dao.findById(expected.getId());

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(expected));
    }

    @Test
    void shouldSaveNewCourseToDatabase() throws SQLException {
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESC_BIOLOGY);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, SMITH, PROFESSOR, LocalDate.now());
        Course course = new Course(BIO_TWENTY_FIVE, category, teacher);

        dao.save(course);

        assertCoursesInDatabase(course);
    }

    @Test
    void shouldSaveSeveralNewCoursesToDatabase() throws SQLException {
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESC_BIOLOGY);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, SMITH, PROFESSOR, LocalDate.now());
        Course bioTwentyFive = new Course(BIO_TWENTY_FIVE, category, teacher);
        Course bioTen = new Course(BIO_TEN, category, teacher);

        dao.saveAll(asList(bioTwentyFive, bioTen));

        assertCoursesInDatabase(bioTen, bioTwentyFive);
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
        Course bioTwentyFive = jdbcHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, category, teacher);
        Course bioTen = jdbcHelper.saveCourseToDatabase(BIO_TEN, COURSE_DESCR, category, teacher);

        List<Course> result = dao.findAll();

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(bioTwentyFive, bioTen));
    }

    @Test
    void shouldReturnEmptyListIfNoCourseWithSpecifiedCategory() throws SQLException {
        Category categoryBio = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESC_BIOLOGY);
        Category categoryArt = jdbcHelper.saveCategoryToDatabase(CODE_ART, DESC_ART);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, SMITH, PROFESSOR, LocalDate.now());
        jdbcHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, categoryBio, teacher);
        jdbcHelper.saveCourseToDatabase(BIO_TEN, COURSE_DESCR, categoryBio, teacher);

        List<Course> result = dao.findByCategory(categoryArt.getId());

        assertThat(result, hasSize(0));
    }

    @Test
    void shouldFindAllCoursesForSpecifiedCategory() throws SQLException {
        Category categoryBio = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESC_BIOLOGY);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, SMITH, PROFESSOR, LocalDate.now());
        Course bioTwentyFive = jdbcHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, categoryBio, teacher);
        Course bioTen = jdbcHelper.saveCourseToDatabase(BIO_TEN, COURSE_DESCR, categoryBio, teacher);

        List<Course> result = dao.findByCategory(categoryBio.getId());

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(bioTwentyFive, bioTen));
    }

    @Test
    void shouldReturnTrueIfCourseWithProvidedIdentifierExists() throws SQLException {
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESC_BIOLOGY);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, SMITH, PROFESSOR, LocalDate.now());
        Course course = jdbcHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, category, teacher);

        boolean result = dao.existsById(course.getId());

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseIfCourseWithProvidedIdentifierNotExist() {
        boolean result = dao.existsById(1);

        assertFalse(result);
    }

    private void assertCoursesInDatabase(Course... expected) throws SQLException {
        Arrays.stream(expected)
                .forEach(course -> assertThat("course should have id", course.getId(), is(not(nullValue()))));

        List<Course> result = jdbcHelper.findAllCoursesInDatabase();
        assertThat(result, hasSize(expected.length));
        assertThat(result, containsInAnyOrder(expected));
    }

}
