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
import static org.vdragun.tms.core.domain.Title.ASSOCIATE_PROFESSOR;

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
import org.vdragun.tms.config.JdbcDaoConfig;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.dao.StudentDao;

@SpringJUnitConfig(classes = { JdbcDaoConfig.class, DBTestConfig.class })
@Sql(scripts = { "/sql/db_schema_seq.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Jdbc Student DAO")
public class JdbcStudentDaoTest {

    private static final String MA_TWELVE = "ma-12";
    private static final String PH_TWENTY_FIVE = "ph-25";
    private static final String COURSE_DESCR = "course description";
    private static final String ART_TEN = "art-10";
    private static final String ART_TWENTY_FIVE = "art-25";
    private static final String DESC_ART = "Art";
    private static final String CODE_ART = "ART";
    private static final String THOMPSON = "Thompson";
    private static final String JOHN = "John";
    private static final String SMITH = "Smith";
    private static final String SNOW = "Snow";
    private static final String JACK = "Jack";
    private static final String ANNA = "Anna";
    private static final LocalDate ENROLLMENT_DATE = LocalDate.now();

    @Autowired
    private StudentDao dao;

    @Autowired
    private JdbcTestHelper jdbcHelper;

    @Test
    void shouldReturnEmptyResultIfNoStudentWithGivenIdInDatabase() {
        Optional<Student> result = dao.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindStudentById() throws SQLException {
        Student expected = jdbcHelper.saveStudentToDatabase(JACK, SMITH, ENROLLMENT_DATE);

        Optional<Student> result = dao.findById(expected.getId());

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(expected));
    }

    @Test
    void shouldSaveNewStudentToDatabase() throws SQLException {
        Student student = new Student(JACK, SMITH, ENROLLMENT_DATE);

        dao.save(student);

        assertStudentsInDatabase(student);
    }

    @Test
    void shouldSaveSeveralNewStudentToDatabase() throws SQLException {
        Student jack = new Student(JACK, SMITH, ENROLLMENT_DATE);
        Student anna = new Student(ANNA, SNOW, ENROLLMENT_DATE);

        dao.saveAll(asList(jack, anna));

        assertStudentsInDatabase(jack, anna);
    }

    @Test
    void shouldReturnEmptyListIfNoStudentsInDatabase() {
        List<Student> result = dao.findAll();

        assertThat(result, hasSize(0));
    }

    @Test
    void shouldFindAllStudentsInDatabase() throws SQLException {
        Student jack = jdbcHelper.saveStudentToDatabase(JACK, SMITH, ENROLLMENT_DATE);
        Student anna = jdbcHelper.saveStudentToDatabase(ANNA, SNOW, ENROLLMENT_DATE);

        List<Student> result = dao.findAll();

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(jack, anna));
    }

    @Test
    void shouldReturnEmptyListIfNoStudentsForGivenCourse() throws SQLException {
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JOHN, THOMPSON, ASSOCIATE_PROFESSOR, ENROLLMENT_DATE);
        Category artCategory = jdbcHelper.saveCategoryToDatabase(CODE_ART, DESC_ART);
        Course artTwentyFive = jdbcHelper.saveCourseToDatabase(ART_TWENTY_FIVE, COURSE_DESCR, artCategory, teacher);
        Course artTen = jdbcHelper.saveCourseToDatabase(ART_TEN, COURSE_DESCR, artCategory, teacher);
        Student jack = jdbcHelper.saveStudentToDatabase(JACK, SMITH, ENROLLMENT_DATE);
        jdbcHelper.addStudentsToCourseInDatabase(artTwentyFive, jack);

        List<Student> result = dao.findForCourse(artTen.getId());

        assertThat(result, hasSize(0));
    }

    @Test
    void shouldFindAllStudentsAssignedToCourseWithGivenId() throws SQLException {
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JOHN, THOMPSON, ASSOCIATE_PROFESSOR, ENROLLMENT_DATE);
        Category artCategory = jdbcHelper.saveCategoryToDatabase(CODE_ART, DESC_ART);
        Course artTwentyFive = jdbcHelper.saveCourseToDatabase(ART_TWENTY_FIVE, COURSE_DESCR, artCategory, teacher);
        Student jack = jdbcHelper.saveStudentToDatabase(JACK, SMITH, ENROLLMENT_DATE);
        Student anna = jdbcHelper.saveStudentToDatabase(ANNA, SNOW, ENROLLMENT_DATE);
        jdbcHelper.addStudentsToCourseInDatabase(artTwentyFive, jack, anna);

        List<Student> result = dao.findForCourse(artTwentyFive.getId());

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(jack, anna));
    }

    @Test
    void shouldReturnEmptyListIfNoStudentsForGivenGroupInDatabase() throws SQLException {
        Group phTwentyFive = jdbcHelper.saveGroupToDatabase(PH_TWENTY_FIVE);
        Group maTwelve = jdbcHelper.saveGroupToDatabase(MA_TWELVE);
        Student jack = jdbcHelper.saveStudentToDatabase(JACK, SMITH, ENROLLMENT_DATE);
        jdbcHelper.addStudentsToGroupInDatabase(phTwentyFive, jack);

        List<Student> result = dao.findForGroup(maTwelve.getId());

        assertThat(result, hasSize(0));
    }

    @Test
    void shouldFindAllStudentsAssignedToGroupWithGivenIdInDatabase() throws SQLException {
        Group phTwentyFive = jdbcHelper.saveGroupToDatabase(PH_TWENTY_FIVE);
        Student jack = jdbcHelper.saveStudentToDatabase(JACK, SMITH, ENROLLMENT_DATE);
        Student anna = jdbcHelper.saveStudentToDatabase(ANNA, SNOW, ENROLLMENT_DATE);
        jdbcHelper.addStudentsToGroupInDatabase(phTwentyFive, jack, anna);

        List<Student> result = dao.findForGroup(phTwentyFive.getId());

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(jack, anna));
    }

    @Test
    void shouldAddStudentToSpecifiedCourse() throws SQLException {
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JOHN, THOMPSON, ASSOCIATE_PROFESSOR, ENROLLMENT_DATE);
        Category artCategory = jdbcHelper.saveCategoryToDatabase(CODE_ART, DESC_ART);
        Course artTwentyFive = jdbcHelper.saveCourseToDatabase(ART_TWENTY_FIVE, COURSE_DESCR, artCategory, teacher);
        Course artTen = jdbcHelper.saveCourseToDatabase(ART_TEN, COURSE_DESCR, artCategory, teacher);
        Student jack = jdbcHelper.saveStudentToDatabase(JACK, SMITH, ENROLLMENT_DATE);

        dao.addToCourse(jack.getId(), artTwentyFive.getId());
        dao.addToCourse(jack.getId(), artTen.getId());

        assertStudentCoursesInDatabase(jack, artTwentyFive, artTen);
    }

    @Test
    void shouldRemoveStudentFromSpecificCourse() throws SQLException {
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JOHN, THOMPSON, ASSOCIATE_PROFESSOR, ENROLLMENT_DATE);
        Category artCategory = jdbcHelper.saveCategoryToDatabase(CODE_ART, DESC_ART);
        Course artTwentyFive = jdbcHelper.saveCourseToDatabase(ART_TWENTY_FIVE, COURSE_DESCR, artCategory, teacher);
        Course artTen = jdbcHelper.saveCourseToDatabase(ART_TEN, COURSE_DESCR, artCategory, teacher);
        Student jack = jdbcHelper.saveStudentToDatabase(JACK, SMITH, ENROLLMENT_DATE);
        jdbcHelper.addStudentToCoursesInDatabase(jack, artTen, artTwentyFive);

        dao.removeFromCourse(jack.getId(), artTwentyFive.getId());

        assertStudentCoursesInDatabase(jack, artTen);
    }

    @Test
    void shouldRemoveStudentFromAllAssignedCourse() throws SQLException {
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JOHN, THOMPSON, ASSOCIATE_PROFESSOR, ENROLLMENT_DATE);
        Category artCategory = jdbcHelper.saveCategoryToDatabase(CODE_ART, DESC_ART);
        Course artTwentyFive = jdbcHelper.saveCourseToDatabase(ART_TWENTY_FIVE, COURSE_DESCR, artCategory, teacher);
        Course artTen = jdbcHelper.saveCourseToDatabase(ART_TEN, COURSE_DESCR, artCategory, teacher);
        Student jack = jdbcHelper.saveStudentToDatabase(JACK, SMITH, ENROLLMENT_DATE);
        jdbcHelper.addStudentToCoursesInDatabase(jack, artTen, artTwentyFive);

        dao.removeFromAllCourses(jack.getId());

        assertNoCoursesForStudentInDatabase(jack);
    }

    @Test
    void shouldAddStudentToGroupWithSpecifiedId() throws SQLException {
        Group group = jdbcHelper.saveGroupToDatabase(PH_TWENTY_FIVE);
        Student jack = jdbcHelper.saveStudentToDatabase(JACK, SMITH, ENROLLMENT_DATE);
        Student anna = jdbcHelper.saveStudentToDatabase(ANNA, SNOW, ENROLLMENT_DATE);

        dao.addToGroup(jack.getId(), group.getId());
        dao.addToGroup(anna.getId(), group.getId());

        assertGroupStudentsInDatabase(group, jack, anna);
    }

    @Test
    void shouldRemoveStudentFromCurrentlyAssignedGroup() throws SQLException {
        Group group = jdbcHelper.saveGroupToDatabase(PH_TWENTY_FIVE);
        Student jack = jdbcHelper.saveStudentToDatabase(JACK, SMITH, ENROLLMENT_DATE);
        Student anna = jdbcHelper.saveStudentToDatabase(ANNA, SNOW, ENROLLMENT_DATE);
        jdbcHelper.addStudentsToGroupInDatabase(group, jack, anna);

        dao.removeFromGroup(jack.getId());
        dao.removeFromGroup(anna.getId());

        assertNoStudentsForGroupInDatabase(group);
    }

    @Test
    void shouldReturnTrueIfStudentWithGivenIdentifierExists() throws SQLException {
        Student student = jdbcHelper.saveStudentToDatabase(JACK, SMITH, ENROLLMENT_DATE);

        boolean result = dao.existsById(student.getId());

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseIfStudentWithGivenIdentifierNotExist() {
        boolean result = dao.existsById(1);

        assertFalse(result);
    }

    @Test
    void shouldReturnStudentWithAllAssignedCourses() throws SQLException {
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JOHN, THOMPSON, ASSOCIATE_PROFESSOR, ENROLLMENT_DATE);
        Category categroy = jdbcHelper.saveCategoryToDatabase(CODE_ART, DESC_ART);
        Course courseA = jdbcHelper.saveCourseToDatabase(ART_TWENTY_FIVE, COURSE_DESCR, categroy, teacher);
        Course courseB = jdbcHelper.saveCourseToDatabase(ART_TEN, COURSE_DESCR, categroy, teacher);
        Student student = jdbcHelper.saveStudentToDatabase(JACK, SMITH, ENROLLMENT_DATE);
        jdbcHelper.addStudentToCoursesInDatabase(student, courseB, courseA);

        Optional<Student> result = dao.findById(student.getId());

        assertThat(result.get().getCourses(), containsInAnyOrder(courseA, courseB));
    }

    @Test
    void shouldDeleteStudentByGivenIdentifier() throws SQLException {
        Student student = jdbcHelper.saveStudentToDatabase(JACK, SMITH, ENROLLMENT_DATE);

        dao.deleteById(student.getId());

        assertNoGivenStudentsInDatabase(student);
    }

    private void assertNoGivenStudentsInDatabase(Student... expected) throws SQLException {
        List<Student> result = jdbcHelper.findAllStudentsInDatabase();
        assertThat(result, not(containsInAnyOrder(expected)));
    }

    private void assertNoStudentsForGroupInDatabase(Group group) throws SQLException {
        List<Student> result = jdbcHelper.findAllGroupStudentsInDatabase(group);
        assertThat(result, hasSize(0));
    }

    private void assertGroupStudentsInDatabase(Group group, Student... expected) throws SQLException {
        List<Student> result = jdbcHelper.findAllGroupStudentsInDatabase(group);
        assertThat(result, hasSize(expected.length));
        assertThat(result, containsInAnyOrder(expected));
    }

    private void assertNoCoursesForStudentInDatabase(Student student) throws SQLException {
        List<Course> result = jdbcHelper.findAllStudentCoursesInDatabase(student);
        assertThat(result, hasSize(0));
    }

    private void assertStudentCoursesInDatabase(Student student, Course... expected) throws SQLException {
        List<Course> result = jdbcHelper.findAllStudentCoursesInDatabase(student);
        assertThat(result, hasSize(expected.length));
        assertThat(result, containsInAnyOrder(expected));
    }

    private void assertStudentsInDatabase(Student... expected) throws SQLException {
        Arrays.stream(expected)
                .forEach(student -> assertThat("student should have id", student.getId(), is(not(nullValue()))));

        List<Student> result = jdbcHelper.findAllStudentsInDatabase();
        assertThat(result, hasSize(expected.length));
        assertThat(result, containsInAnyOrder(expected));
    }

}
