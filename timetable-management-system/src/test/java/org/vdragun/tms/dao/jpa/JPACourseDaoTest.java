package org.vdragun.tms.dao.jpa;

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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import org.vdragun.tms.config.JPADaoConfig;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.dao.CourseDao;
import org.vdragun.tms.dao.DBTestHelper;
import org.vdragun.tms.dao.DaoTestConfig;

@SpringJUnitConfig(classes = { JPADaoConfig.class, DaoTestConfig.class })
@DisplayName("JPA Course DAO")
@Transactional
public class JPACourseDaoTest {
    private static final String CODE_BIO = "BIO";
    private static final String CODE_ART = "ART";

    private static final String BEGINNER_BIOLOGY = "Beginner Biology";
    private static final String CORE_BIOLOGY = "Core Biology";
    private static final String ADVANCED_BIOLOGY = "Advanced Biology";
    private static final String INTERMEDIATE_BIOLOGY = "Intermediate Biology";
    private static final String CORE_HISTORY = "Core History";

    @Autowired
    private CourseDao dao;

    @Autowired
    private DBTestHelper jdbcHelper;

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldReturnEmptyResultIfNoCourseWithGivenIdInDatabase() {
        Optional<Course> result = dao.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/course_data.sql" })
    void shouldFindCourseById() {
        Course expected = jdbcHelper.findRandomCourseInDatabase();

        Optional<Course> result = dao.findById(expected.getId());

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(expected));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/course_data.sql" })
    void shouldSaveNewCourseToDatabase() {
        Category category = jdbcHelper.findCategoryByCodeInDatabase(CODE_BIO);
        Teacher teacher = jdbcHelper.findRandomTeacherInDatabase();
        Course course = new Course(CORE_BIOLOGY, category, teacher);

        dao.save(course);

        assertCoursesInDatabase(course);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/course_data.sql" })
    void shouldSaveSeveralNewCoursesToDatabase() {
        Category category = jdbcHelper.findCategoryByCodeInDatabase(CODE_BIO);
        Teacher teacher = jdbcHelper.findRandomTeacherInDatabase();
        Course coreBilogy = new Course(CORE_BIOLOGY, category, teacher);
        Course beginnerBiology = new Course(BEGINNER_BIOLOGY, category, teacher);

        dao.saveAll(asList(coreBilogy, beginnerBiology));

        assertCoursesInDatabase(beginnerBiology, coreBilogy);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql" })
    void shouldReturnEmptyListIfNoCoursesInDatabase() {
        List<Course> result = dao.findAll();

        assertThat(result, hasSize(0));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/course_data.sql" })
    void shouldFindAllCoursesInDatabase() {
        List<Course> result = dao.findAll();

        assertCoursesWithNames(result, ADVANCED_BIOLOGY, INTERMEDIATE_BIOLOGY, CORE_HISTORY);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/course_data.sql" })
    void shouldReturnEmptyListIfNoCourseWithSpecifiedCategory() {
        Category categoryArt = jdbcHelper.findCategoryByCodeInDatabase(CODE_ART);

        List<Course> result = dao.findByCategory(categoryArt.getId());

        assertThat(result, hasSize(0));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/course_data.sql" })
    void shouldFindAllCoursesForSpecifiedCategory() {
        Category categoryBio = jdbcHelper.findCategoryByCodeInDatabase(CODE_BIO);

        List<Course> result = dao.findByCategory(categoryBio.getId());

        assertCoursesWithNames(result, ADVANCED_BIOLOGY, INTERMEDIATE_BIOLOGY);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/course_data.sql" })
    void shouldReturnTrueIfCourseWithProvidedIdentifierExists() {
        Course course = jdbcHelper.findRandomCourseInDatabase();

        boolean result = dao.existsById(course.getId());

        assertTrue(result);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql" })
    void shouldReturnFalseIfCourseWithProvidedIdentifierNotExist() {
        boolean result = dao.existsById(1);

        assertFalse(result);
    }

    private void assertCoursesWithNames(List<Course> result, String... expectedNames) {
        assertThat(result, hasSize(expectedNames.length));
        for (String expectedName : expectedNames) {
            assertThat(result, hasItem(hasProperty("name", equalTo(expectedName))));
        }
    }

    private void assertCoursesInDatabase(Course... expected) {
        Arrays.stream(expected)
                .forEach(course -> assertThat("course should have id", course.getId(), is(not(nullValue()))));

        List<Course> result = jdbcHelper.findAllCoursesInDatabase();
        assertThat(result, hasItems(expected));
    }
}
