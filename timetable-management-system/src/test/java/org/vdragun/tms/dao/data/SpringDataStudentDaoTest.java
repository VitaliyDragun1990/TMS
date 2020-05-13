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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.dao.DBTestHelper;
import org.vdragun.tms.dao.DaoTestConfig;
import org.vdragun.tms.dao.StudentDao;
import org.vdragun.tms.dao.jpa.FullName;

@SpringJUnitConfig(classes = { SpringDataDaoConfig.class, DaoTestConfig.class })
@DisplayName("Spring Data Student DAO")
@Transactional
public class SpringDataStudentDaoTest {

    private static final LocalDate ENROLLMENT_DATE = LocalDate.now();

    private static final String MARY = "Mary";
    private static final String AMANDA = "Amanda";
    private static final String WILLIAM = "William";
    private static final String BIRKIN = "Birkin";

    private static final String CORE_HISORY = "Core History";
    private static final String CORE_BIOLOGY = "Core Biology";
    private static final String ADVANCED_BIOLOGY = "Advanced Biology";

    private static final String PS_TWENTY = "ps-20";
    private static final String MH_TEN = "mh-10";

    @Autowired
    private StudentDao dao;

    @Autowired
    private DBTestHelper dbHelper;

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldReturnEmptyResultIfNoStudentWithGivenIdInDatabase() {
        Optional<Student> result = dao.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/student_data.sql" })
    void shouldFindStudentById() {
        Student expected = dbHelper.findStudentByNameInDatabase(MARY, BIRKIN);

        Optional<Student> result = dao.findById(expected.getId());

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(expected));
    }

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldSaveNewStudentToDatabase() {
        Student student = new Student(MARY, BIRKIN, ENROLLMENT_DATE);

        dao.save(student);

        assertStudentsInDatabase(student);
    }

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldSaveSeveralNewStudentToDatabase() {
        Student mary = new Student(MARY, BIRKIN, ENROLLMENT_DATE);
        Student amanda = new Student(AMANDA, BIRKIN, ENROLLMENT_DATE);
        Student william = new Student(WILLIAM, BIRKIN, ENROLLMENT_DATE);

        dao.saveAll(asList(mary, amanda, william));

        assertStudentsInDatabase(mary, amanda, william);
    }

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldReturnEmptyListIfNoStudentsInDatabase() {
        List<Student> result = dao.findAll();

        assertThat(result, hasSize(0));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/student_data.sql" })
    void shouldFindAllStudentsInDatabase() {
        List<Student> result = dao.findAll();

        assertStudentsWithNames(
                result,
                FullName.from(MARY, BIRKIN), FullName.from(AMANDA, BIRKIN), FullName.from(WILLIAM, BIRKIN));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/student_data.sql" })
    void shouldReturnEmptyListIfNoStudentsForGivenCourse() {
        Course coreHistory = dbHelper.findCourseByNameInDatabase(CORE_HISORY);

        List<Student> result = dao.findForCourse(coreHistory.getId());

        assertThat(result, hasSize(0));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/student_data.sql" })
    void shouldFindAllStudentsAssignedToCourseWithGivenId() {
        Course advancedBilogy = dbHelper.findCourseByNameInDatabase(ADVANCED_BIOLOGY);

        List<Student> result = dao.findForCourse(advancedBilogy.getId());

        assertStudentsWithNames(
                result,
                FullName.from(MARY, BIRKIN), FullName.from(AMANDA, BIRKIN), FullName.from(WILLIAM, BIRKIN));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/student_data.sql" })
    void shouldFindAllStudentsWithAllCoursesAssignedToCourseWithGivenId() {
        Course coreBilogy = dbHelper.findCourseByNameInDatabase(CORE_BIOLOGY);

        List<Student> result = dao.findForCourse(coreBilogy.getId());

        assertStudentsWithNames(
                result,
                FullName.from(MARY, BIRKIN));
        assertStudentCourses(result.get(0), CORE_BIOLOGY, ADVANCED_BIOLOGY);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/student_data.sql" })
    void shouldReturnEmptyListIfNoStudentsForGivenGroupInDatabase() {
        Group psTwenty = dbHelper.findGroupByNameInDatabase(PS_TWENTY);

        List<Student> result = dao.findForGroup(psTwenty.getId());

        assertThat(result, hasSize(0));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/student_data.sql" })
    void shouldFindAllStudentsAssignedToGroupWithGivenIdInDatabase() {
        Group mhTen = dbHelper.findGroupByNameInDatabase(MH_TEN);

        List<Student> result = dao.findForGroup(mhTen.getId());

        assertStudentsWithNames(
                result,
                FullName.from(MARY, BIRKIN), FullName.from(AMANDA, BIRKIN), FullName.from(WILLIAM, BIRKIN));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/student_data.sql" })
    void shouldAddStudentToSpecifiedCourse() {
        Student mary = dbHelper.findStudentByNameInDatabase(MARY, BIRKIN);
        Course coreHistory = dbHelper.findCourseByNameInDatabase(CORE_HISORY);

        dao.addToCourse(mary.getId(), coreHistory.getId());

        assertStudentCoursesInDatabase(mary, CORE_BIOLOGY, ADVANCED_BIOLOGY, CORE_HISORY);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/student_data.sql" })
    void shouldRemoveStudentFromSpecificCourse() {
        Student mary = dbHelper.findStudentByNameInDatabase(MARY, BIRKIN);
        Course coreBiology = dbHelper.findCourseByNameInDatabase(CORE_BIOLOGY);

        dao.removeFromCourse(mary.getId(), coreBiology.getId());

        assertStudentCoursesInDatabase(mary, ADVANCED_BIOLOGY);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/student_data.sql" })
    void shouldRemoveStudentFromAllAssignedCourse() {
        Student mary = dbHelper.findStudentByNameInDatabase(MARY, BIRKIN);

        dao.removeFromAllCourses(mary.getId());

        assertNoCoursesForStudentInDatabase(mary);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/student_data.sql" })
    void shouldAddStudentToGroupWithSpecifiedId() {
        Group group = dbHelper.findGroupByNameInDatabase(PS_TWENTY);
        Student mary = dbHelper.findStudentByNameInDatabase(MARY, BIRKIN);

        dao.addToGroup(mary.getId(), group.getId());

        assertGroupStudentsInDatabase(group, mary);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/student_data.sql" })
    void shouldRemoveStudentFromCurrentlyAssignedGroup() {
        Student mary = dbHelper.findStudentByNameInDatabase(MARY, BIRKIN);

        dao.removeFromGroup(mary.getId());

        assertStudentWithoutGroupInDatabase(mary);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/student_data.sql" })
    void shouldReturnTrueIfStudentWithGivenIdentifierExists() {
        Student student = dbHelper.findStudentByNameInDatabase(MARY, BIRKIN);

        boolean result = dao.existsById(student.getId());

        assertTrue(result);
    }

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldReturnFalseIfStudentWithGivenIdentifierNotExist() {
        boolean result = dao.existsById(1);

        assertFalse(result);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/student_data.sql" })
    void shouldReturnStudentWithAllAssignedCourses() {
        Student student = dbHelper.findStudentByNameInDatabase(MARY, BIRKIN);

        Optional<Student> result = dao.findById(student.getId());

        assertTrue(result.isPresent());
        assertStudentCourses(result.get(), CORE_BIOLOGY, ADVANCED_BIOLOGY);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/student_data.sql" })
    void shouldDeleteStudentByGivenIdentifier() {
        Student student = dbHelper.findStudentByNameInDatabase(MARY, BIRKIN);

        dao.deleteById(student.getId());

        assertNoGivenStudentsInDatabase(student);
    }
    
    private void assertStudentWithoutGroupInDatabase(Student student) {
        Student result = dbHelper.findStudentByNameInDatabase(student.getFirstName(), student.getLastName());
        assertThat(result.getGroup(), is(nullValue()));
    }

    private void assertStudentCourses(Student student, String... expectedNames) {
        assertThat(student.getCourses(), hasSize(expectedNames.length));
        for (String expectedName : expectedNames) {
            assertThat(student.getCourses(), hasItem(hasProperty("name", equalTo(expectedName))));
        }
    }

    private void assertStudentsWithNames(List<Student> result, FullName... expectedNames) {
        assertThat(result, hasSize(expectedNames.length));
        for (FullName expectedName : expectedNames) {
            assertThat(result, hasItem(allOf(
                    hasProperty("firstName", equalTo(expectedName.getFirstName())),
                    hasProperty("lastName", equalTo(expectedName.getLastName())))));
        }
    }

    private void assertNoGivenStudentsInDatabase(Student... expected) {
        List<Student> result = dbHelper.findAllStudentsInDatabase();
        assertThat(result, not(hasItems(expected)));
    }

    private void assertGroupStudentsInDatabase(Group group, Student... expected) {
        List<Student> result = dbHelper.findAllGroupStudentsInDatabase(group);
        assertThat(result, hasSize(expected.length));
        assertThat(result, containsInAnyOrder(expected));
    }

    private void assertNoCoursesForStudentInDatabase(Student student) {
        List<Course> result = dbHelper.findAllStudentCoursesInDatabase(student);
        assertThat(result, hasSize(0));
    }

    private void assertStudentCoursesInDatabase(Student student, String... expectedNames) {
        List<Course> result = dbHelper.findAllStudentCoursesInDatabase(student);
        assertThat(result, hasSize(expectedNames.length));
        for (String expectedName : expectedNames) {
            assertThat(student.getCourses(), hasItem(hasProperty("name", equalTo(expectedName))));
        }
    }

    private void assertStudentsInDatabase(Student... expected) {
        Arrays.stream(expected)
                .forEach(student -> assertThat("student should have id", student.getId(), is(not(nullValue()))));

        List<Student> result = dbHelper.findAllStudentsInDatabase();
        assertThat(result, hasItems(expected));
    }
}
