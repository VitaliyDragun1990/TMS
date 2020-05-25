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
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.dao.CourseDao;
import org.vdragun.tms.dao.DBTestHelper;
import org.vdragun.tms.dao.DaoTestConfig;

@DataJpaTest
@Import({ SpringDataDaoConfig.class, DaoTestConfig.class })
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DisplayName("Spring Data Course DAO")
public class SpringDataCourseDaoTest {
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
    private DBTestHelper dbHelper;

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldReturnEmptyResultIfNoCourseWithGivenIdInDatabase() {
        Optional<Course> result = dao.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/course_data.sql" })
    void shouldFindCourseById() {
        Course expected = dbHelper.findRandomCourseInDatabase();

        Optional<Course> result = dao.findById(expected.getId());

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(expected));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/course_data.sql" })
    void shouldSaveNewCourseToDatabase() {
        Category category = dbHelper.findCategoryByCodeInDatabase(CODE_BIO);
        Teacher teacher = dbHelper.findRandomTeacherInDatabase();
        Course course = new Course(CORE_BIOLOGY, category, teacher);

        dao.save(course);
        dbHelper.flushChangesToDatabase();

        assertCoursesInDatabase(course);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/course_data.sql" })
    void shouldSaveSeveralNewCoursesToDatabase() {
        Category category = dbHelper.findCategoryByCodeInDatabase(CODE_BIO);
        Teacher teacher = dbHelper.findRandomTeacherInDatabase();
        Course coreBilogy = new Course(CORE_BIOLOGY, category, teacher);
        Course beginnerBiology = new Course(BEGINNER_BIOLOGY, category, teacher);

        dao.saveAll(asList(coreBilogy, beginnerBiology));
        dbHelper.flushChangesToDatabase();

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
        Category categoryArt = dbHelper.findCategoryByCodeInDatabase(CODE_ART);

        List<Course> result = dao.findByCategoryId(categoryArt.getId());

        assertThat(result, hasSize(0));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/course_data.sql" })
    void shouldFindAllCoursesForSpecifiedCategory() {
        Category categoryBio = dbHelper.findCategoryByCodeInDatabase(CODE_BIO);

        List<Course> result = dao.findByCategoryId(categoryBio.getId());

        assertCoursesWithNames(result, ADVANCED_BIOLOGY, INTERMEDIATE_BIOLOGY);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/course_data.sql" })
    void shouldReturnTrueIfCourseWithProvidedIdentifierExists() {
        Course course = dbHelper.findRandomCourseInDatabase();

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

        List<Course> result = dbHelper.findAllCoursesInDatabase();
        assertThat(result, hasItems(expected));
    }
}
