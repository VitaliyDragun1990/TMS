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
import static org.vdragun.tms.core.domain.Title.PROFESSOR;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
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
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.dao.TimetableDao;

@SpringJUnitConfig(classes = { DaoConfig.class, DBTestConfig.class })
@Sql(scripts = { "/sql/db_schema.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Jdbc Timetable DAO")
public class JdbcTimetableDaoTest {

    private static final LocalDate ENROLLMENT_DATE = LocalDate.now();
    private static final String ANNA = "Anna";
    private static final String SMITH = "Smith";
    private static final LocalDateTime MARCH_10_9_30 = LocalDateTime.of(2020, 3, 10, 9, 30);
    private static final LocalDate MARCH_10 = LocalDate.of(2020, 3, 10);
    private static final LocalDate MARCH_25 = LocalDate.of(2020, 3, 25);
    private static final LocalDateTime MARCH_10_12_30 = LocalDateTime.of(2020, 3, 10, 12, 30);
    private static final LocalDateTime MARCH_25_9_30 = LocalDateTime.of(2020, 3, 25, 9, 30);
    private static final LocalDateTime APRIL_5_90_30 = LocalDateTime.of(2020, 4, 5, 9, 30);
    private static final int DURATION_90 = 90;
    private static final String COURSE_DESCRIPTION = "Course description";
    private static final String NAME_BIO_25 = "bio-25";
    private static final String NAME_BIO_10 = "bio-10";
    private static final String DESCR_BIO = "Biology";
    private static final String CODE_BIO = "BIO";
    private static final int CAPACITY_30 = 30;
    private static final LocalDate DATE_HIRED = ENROLLMENT_DATE;
    private static final String THOMPSON = "Thompson";
    private static final String JACK = "Jack";
    private static final String HARRY = "Harry";
    private static final String SNOW = "Snow";

    @Autowired
    private TimetableDao dao;

    @Autowired
    private JdbcTestHelper jdbcHelper;

    @Test
    void shouldReturnEmptyResultIfNoTimetableWithGivenIdInDatabase() {
        Optional<Timetable> result = dao.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindTimetableById() throws SQLException {
        Classroom classroom = jdbcHelper.saveClassroomToDatabase(CAPACITY_30);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = jdbcHelper.saveCourseToDatabase(NAME_BIO_25, COURSE_DESCRIPTION, category, teacher);
        Timetable expected = jdbcHelper.saveTimetableToDatabase(MARCH_10_9_30, DURATION_90, course, teacher, classroom);

        Optional<Timetable> result = dao.findById(expected.getId());

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(expected));
    }

    @Test
    void shouldSaveNewTimetableToDatabase() throws SQLException {
        Classroom classroom = jdbcHelper.saveClassroomToDatabase(CAPACITY_30);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = jdbcHelper.saveCourseToDatabase(NAME_BIO_25, COURSE_DESCRIPTION, category, teacher);
        Timetable timetable = new Timetable(MARCH_10_9_30, DURATION_90, course, classroom, teacher);

        dao.save(timetable);

        assertTimetablesInDatabase(timetable);
    }

    @Test
    void shouldSaveSeveralNewTimetablesToDatabase() throws SQLException {
        Classroom classroom = jdbcHelper.saveClassroomToDatabase(CAPACITY_30);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = jdbcHelper.saveCourseToDatabase(NAME_BIO_25, COURSE_DESCRIPTION, category, teacher);
        Timetable timetableA = new Timetable(MARCH_10_9_30, DURATION_90, course, classroom, teacher);
        Timetable timetableB = new Timetable(MARCH_25_9_30, DURATION_90, course, classroom, teacher);

        dao.saveAll(Arrays.asList(timetableA, timetableB));

        assertTimetablesInDatabase(timetableA, timetableB);
    }

    @Test
    void shouldReturnEmptyListIfNoTimetablesInDatabase() {
        List<Timetable> result = dao.findAll();

        assertThat(result, hasSize(0));
    }

    @Test
    void shouldFindAllTimetablesInDatabase() throws SQLException {
        Classroom classroom = jdbcHelper.saveClassroomToDatabase(CAPACITY_30);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = jdbcHelper.saveCourseToDatabase(NAME_BIO_25, COURSE_DESCRIPTION, category, teacher);
        Timetable timetableA = jdbcHelper.saveTimetableToDatabase(MARCH_10_9_30, DURATION_90, course, teacher, classroom);
        Timetable timetableB = jdbcHelper.saveTimetableToDatabase(MARCH_25_9_30, DURATION_90, course, teacher, classroom);

        List<Timetable> result = dao.findAll();

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(timetableA, timetableB));
    }
    
    @Test
    void shouldReturnEmptyListIfNoTimetablesForStudentForGivenDay() throws SQLException {
        Classroom classroom = jdbcHelper.saveClassroomToDatabase(CAPACITY_30);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = jdbcHelper.saveCourseToDatabase(NAME_BIO_25, COURSE_DESCRIPTION, category, teacher);
        Student student = jdbcHelper.saveStudentToDatabase(ANNA, SMITH, ENROLLMENT_DATE);

        jdbcHelper.addStudentToCoursesInDatabase(student, course);

        jdbcHelper.saveTimetableToDatabase(MARCH_10_9_30, DURATION_90, course, teacher, classroom);
        jdbcHelper.saveTimetableToDatabase(MARCH_10_12_30, DURATION_90, course, teacher, classroom);

        List<Timetable> result = dao.findDailyForStudent(student.getId(), MARCH_25);

        assertThat(result, hasSize(0));
    }

    @Test
    void shouldFindAllTimetablesForStudentForGivenDay() throws SQLException {
        Classroom classroom = jdbcHelper.saveClassroomToDatabase(CAPACITY_30);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = jdbcHelper.saveCourseToDatabase(NAME_BIO_25, COURSE_DESCRIPTION, category, teacher);
        Student student = jdbcHelper.saveStudentToDatabase(ANNA, SMITH, ENROLLMENT_DATE);

        jdbcHelper.addStudentToCoursesInDatabase(student, course);

        Timetable timetableA = jdbcHelper.saveTimetableToDatabase(MARCH_10_9_30, DURATION_90, course, teacher,
                classroom);
        Timetable timetableB = jdbcHelper.saveTimetableToDatabase(MARCH_10_12_30, DURATION_90, course, teacher,
                classroom);
        Timetable timetableC = jdbcHelper.saveTimetableToDatabase(MARCH_25_9_30, DURATION_90, course, teacher,
                classroom);

        List<Timetable> result = dao.findDailyForStudent(student.getId(), MARCH_10);

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(timetableA, timetableB));
        assertThat(result, not(containsInAnyOrder(timetableC)));
    }

    @Test
    void shouldReturnEmptyListIfNoTimetablesForTeacherForGivenDay() throws SQLException {
        Classroom classroom = jdbcHelper.saveClassroomToDatabase(CAPACITY_30);
        Teacher jack = jdbcHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Teacher harry = jdbcHelper.saveTeacherToDatabase(HARRY, SNOW, PROFESSOR, DATE_HIRED);
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course bio25 = jdbcHelper.saveCourseToDatabase(NAME_BIO_25, COURSE_DESCRIPTION, category, jack);
        Course bio10 = jdbcHelper.saveCourseToDatabase(NAME_BIO_10, COURSE_DESCRIPTION, category, harry);

        jdbcHelper.saveTimetableToDatabase(MARCH_10_9_30, DURATION_90, bio25, jack, classroom);
        jdbcHelper.saveTimetableToDatabase(MARCH_25_9_30, DURATION_90, bio10, harry, classroom);

        List<Timetable> result = dao.findDailyForTeacher(jack.getId(), MARCH_25);

        assertThat(result, hasSize(0));
    }

    @Test
    void shouldFindAllTimetablesForTeacherForGivenDay() throws SQLException {
        Classroom classroom = jdbcHelper.saveClassroomToDatabase(CAPACITY_30);
        Teacher harry = jdbcHelper.saveTeacherToDatabase(HARRY, SNOW, PROFESSOR, DATE_HIRED);
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course bio25 = jdbcHelper.saveCourseToDatabase(NAME_BIO_25, COURSE_DESCRIPTION, category, harry);
        Course bio10 = jdbcHelper.saveCourseToDatabase(NAME_BIO_10, COURSE_DESCRIPTION, category, harry);

        Timetable timetableA = jdbcHelper.saveTimetableToDatabase(MARCH_10_9_30, DURATION_90, bio25, harry, classroom);
        Timetable timetableB = jdbcHelper.saveTimetableToDatabase(MARCH_10_12_30, DURATION_90, bio10, harry, classroom);

        List<Timetable> result = dao.findDailyForTeacher(harry.getId(), MARCH_10);

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(timetableA, timetableB));
    }

    @Test
    void shouldReturnEmptyListIfNoTimetablesForStudentForGivenMonth() throws SQLException {
        Classroom classroom = jdbcHelper.saveClassroomToDatabase(CAPACITY_30);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = jdbcHelper.saveCourseToDatabase(NAME_BIO_25, COURSE_DESCRIPTION, category, teacher);
        Student student = jdbcHelper.saveStudentToDatabase(ANNA, SMITH, ENROLLMENT_DATE);

        jdbcHelper.addStudentToCoursesInDatabase(student, course);

        jdbcHelper.saveTimetableToDatabase(MARCH_10_9_30, DURATION_90, course, teacher, classroom);
        jdbcHelper.saveTimetableToDatabase(MARCH_25_9_30, DURATION_90, course, teacher, classroom);

        List<Timetable> result = dao.findMonthlyForStudent(student.getId(), Month.APRIL);

        assertThat(result, hasSize(0));
    }

    @Test
    void shouldFindAllTimetablesForStudentForGivenMonth() throws SQLException {
        Classroom classroom = jdbcHelper.saveClassroomToDatabase(CAPACITY_30);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = jdbcHelper.saveCourseToDatabase(NAME_BIO_25, COURSE_DESCRIPTION, category, teacher);
        Student student = jdbcHelper.saveStudentToDatabase(ANNA, SMITH, ENROLLMENT_DATE);

        jdbcHelper.addStudentToCoursesInDatabase(student, course);

        Timetable timetableA = jdbcHelper.saveTimetableToDatabase(MARCH_10_9_30, DURATION_90, course, teacher,
                classroom);
        Timetable timetableB = jdbcHelper.saveTimetableToDatabase(MARCH_25_9_30, DURATION_90, course, teacher,
                classroom);
        jdbcHelper.saveTimetableToDatabase(APRIL_5_90_30, DURATION_90, course, teacher, classroom);

        List<Timetable> result = dao.findMonthlyForStudent(student.getId(), Month.MARCH);

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(timetableA, timetableB));
    }

    @Test
    void shouldReturnEmptyListIfNoTimetablesForTeacherForGivenMonth() throws SQLException {
        Classroom classroom = jdbcHelper.saveClassroomToDatabase(CAPACITY_30);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = jdbcHelper.saveCourseToDatabase(NAME_BIO_25, COURSE_DESCRIPTION, category, teacher);

        jdbcHelper.saveTimetableToDatabase(MARCH_10_9_30, DURATION_90, course, teacher, classroom);
        jdbcHelper.saveTimetableToDatabase(MARCH_25_9_30, DURATION_90, course, teacher, classroom);

        List<Timetable> result = dao.findMonthlyForTeacher(teacher.getId(), Month.APRIL);

        assertThat(result, hasSize(0));
    }

    @Test
    void shouldFindAllTimetablesForTeacherForTeacherForGivenMonth() throws SQLException {
        Classroom classroom = jdbcHelper.saveClassroomToDatabase(CAPACITY_30);
        Teacher teacher = jdbcHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = jdbcHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = jdbcHelper.saveCourseToDatabase(NAME_BIO_25, COURSE_DESCRIPTION, category, teacher);

        Timetable timetableA = jdbcHelper.saveTimetableToDatabase(MARCH_10_9_30, DURATION_90, course, teacher,
                classroom);
        Timetable timetableB = jdbcHelper.saveTimetableToDatabase(MARCH_25_9_30, DURATION_90, course, teacher,
                classroom);
        jdbcHelper.saveTimetableToDatabase(APRIL_5_90_30, DURATION_90, course, teacher, classroom);

        List<Timetable> result = dao.findMonthlyForTeacher(teacher.getId(), Month.MARCH);

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(timetableA, timetableB));
    }

    private void assertTimetablesInDatabase(Timetable... expected) throws SQLException {
        Arrays.stream(expected)
                .forEach(timetable -> assertThat("timetable should have id", timetable.getId(), is(not(nullValue()))));

        List<Timetable> result = jdbcHelper.findAllTimetablesInDatabase();
        assertThat(result, hasSize(expected.length));
        assertThat(result, containsInAnyOrder(expected));
    }

}
