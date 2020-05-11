package org.vdragun.tms.dao.jpa;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.vdragun.tms.core.domain.Title.PROFESSOR;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import org.vdragun.tms.config.JPADaoConfig;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.dao.DBTestHelper;
import org.vdragun.tms.dao.DaoException;
import org.vdragun.tms.dao.DaoTestConfig;
import org.vdragun.tms.dao.TimetableDao;

@SpringJUnitConfig(classes = { JPADaoConfig.class, DaoTestConfig.class })
@DisplayName("JPA Timetable DAO")
@Transactional
public class JPATimetableDaoTest {

    private static final LocalDate ENROLLMENT_DATE = LocalDate.now();
    private static final String ANNA = "Anna";
    private static final String SMITH = "Smith";
    private static final LocalDateTime MARCH_TEN_NINE_THIRTY = LocalDateTime.of(2020, 3, 10, 9, 30);
    private static final LocalDate MARCH_TEN = LocalDate.of(2020, 3, 10);
    private static final LocalDate MARCH_TWENTY_FIFTH = LocalDate.of(2020, 3, 25);
    private static final LocalDateTime MARCH_TEN_TWELVE_THIRTY = LocalDateTime.of(2020, 3, 10, 12, 30);
    private static final LocalDateTime MARCH_TWENTY_FIFTH_NINE_THIRTY = LocalDateTime.of(2020, 3, 25, 9, 30);
    private static final LocalDateTime APRIL_FIFTH_NINE_THIRTY = LocalDateTime.of(2020, 4, 5, 9, 30);
    private static final int DURATION = 90;
    private static final String COURSE_DESCR = "Course description";
    private static final String BIO_TWENTY_FIVE = "bio-25";
    private static final String BIO_TEN = "bio-10";
    private static final String DESCR_BIO = "Biology";
    private static final String CODE_BIO = "BIO";
    private static final int CAPACITY = 30;
    private static final LocalDate DATE_HIRED = ENROLLMENT_DATE;
    private static final String THOMPSON = "Thompson";
    private static final String JACK = "Jack";
    private static final String HARRY = "Harry";
    private static final String SNOW = "Snow";

    @Autowired
    private TimetableDao dao;

    @Autowired
    private DBTestHelper dbHelper;

    @Test
    void shouldReturnEmptyResultIfNoTimetableWithGivenIdInDatabase() {
        Optional<Timetable> result = dao.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindTimetableById() {
        Classroom classroom = dbHelper.saveClassroomToDatabase(CAPACITY);
        Teacher teacher = dbHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = dbHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = dbHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, category, teacher);
        Timetable expected = dbHelper.saveTimetableToDatabase(MARCH_TEN_NINE_THIRTY, DURATION, course,
                teacher,
                classroom);

        Optional<Timetable> result = dao.findById(expected.getId());

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(expected));
    }

    @Test
    void shouldSaveNewTimetableToDatabase() {
        Classroom classroom = dbHelper.saveClassroomToDatabase(CAPACITY);
        Teacher teacher = dbHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = dbHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = dbHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, category, teacher);
        Timetable timetable = new Timetable(MARCH_TEN_NINE_THIRTY, DURATION, course, classroom, teacher);

        dao.save(timetable);

        assertTimetablesInDatabase(timetable);
    }

    @Test
    void shouldSaveSeveralNewTimetablesToDatabase() {
        Classroom classroom = dbHelper.saveClassroomToDatabase(CAPACITY);
        Teacher teacher = dbHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = dbHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = dbHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, category, teacher);
        Timetable timetableA = new Timetable(MARCH_TEN_NINE_THIRTY, DURATION, course, classroom, teacher);
        Timetable timetableB = new Timetable(MARCH_TWENTY_FIFTH_NINE_THIRTY, DURATION, course, classroom, teacher);

        dao.saveAll(Arrays.asList(timetableA, timetableB));

        assertTimetablesInDatabase(timetableA, timetableB);
    }

    @Test
    void shouldReturnEmptyListIfNoTimetablesInDatabase() {
        List<Timetable> result = dao.findAll();

        assertThat(result, hasSize(0));
    }

    @Test
    void shouldFindAllTimetablesInDatabase() {
        Classroom classroom = dbHelper.saveClassroomToDatabase(CAPACITY);
        Teacher teacher = dbHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = dbHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = dbHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, category, teacher);
        Timetable timetableA = dbHelper.saveTimetableToDatabase(MARCH_TEN_NINE_THIRTY, DURATION, course,
                teacher,
                classroom);
        Timetable timetableB = dbHelper.saveTimetableToDatabase(MARCH_TWENTY_FIFTH_NINE_THIRTY, DURATION,
                course,
                teacher,
                classroom);

        List<Timetable> result = dao.findAll();

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(timetableA, timetableB));
    }

    @Test
    void shouldReturnEmptyListIfNoTimetablesForStudentForGivenDay() {
        Classroom classroom = dbHelper.saveClassroomToDatabase(CAPACITY);
        Teacher teacher = dbHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = dbHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = dbHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, category, teacher);
        Student student = dbHelper.saveStudentToDatabase(ANNA, SMITH, ENROLLMENT_DATE);

        dbHelper.addStudentToCoursesInDatabase(student, course);

        dbHelper.saveTimetableToDatabase(MARCH_TEN_NINE_THIRTY, DURATION, course, teacher, classroom);
        dbHelper.saveTimetableToDatabase(MARCH_TEN_TWELVE_THIRTY, DURATION, course, teacher, classroom);

        List<Timetable> result = dao.findDailyForStudent(student.getId(), MARCH_TWENTY_FIFTH);

        assertThat(result, hasSize(0));
    }

    @Test
    void shouldFindAllTimetablesForStudentForGivenDay() {
        Classroom classroom = dbHelper.saveClassroomToDatabase(CAPACITY);
        Teacher teacher = dbHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = dbHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = dbHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, category, teacher);
        Student student = dbHelper.saveStudentToDatabase(ANNA, SMITH, ENROLLMENT_DATE);

        dbHelper.addStudentToCoursesInDatabase(student, course);

        Timetable timetableA = dbHelper.saveTimetableToDatabase(MARCH_TEN_NINE_THIRTY, DURATION, course,
                teacher,
                classroom);
        Timetable timetableB = dbHelper.saveTimetableToDatabase(MARCH_TEN_TWELVE_THIRTY, DURATION, course,
                teacher,
                classroom);
        Timetable timetableC = dbHelper.saveTimetableToDatabase(MARCH_TWENTY_FIFTH_NINE_THIRTY, DURATION,
                course,
                teacher,
                classroom);

        List<Timetable> result = dao.findDailyForStudent(student.getId(), MARCH_TEN);

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(timetableA, timetableB));
        assertThat(result, not(containsInAnyOrder(timetableC)));
    }

    @Test
    void shouldReturnEmptyListIfNoTimetablesForTeacherForGivenDay() {
        Classroom classroom = dbHelper.saveClassroomToDatabase(CAPACITY);
        Teacher jack = dbHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Teacher harry = dbHelper.saveTeacherToDatabase(HARRY, SNOW, PROFESSOR, DATE_HIRED);
        Category category = dbHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course bio25 = dbHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, category, jack);
        Course bio10 = dbHelper.saveCourseToDatabase(BIO_TEN, COURSE_DESCR, category, harry);

        dbHelper.saveTimetableToDatabase(MARCH_TEN_NINE_THIRTY, DURATION, bio25, jack, classroom);
        dbHelper.saveTimetableToDatabase(MARCH_TWENTY_FIFTH_NINE_THIRTY, DURATION, bio10, harry, classroom);

        List<Timetable> result = dao.findDailyForTeacher(jack.getId(), MARCH_TWENTY_FIFTH);

        assertThat(result, hasSize(0));
    }

    @Test
    void shouldFindAllTimetablesForTeacherForGivenDay() {
        Classroom classroom = dbHelper.saveClassroomToDatabase(CAPACITY);
        Teacher harry = dbHelper.saveTeacherToDatabase(HARRY, SNOW, PROFESSOR, DATE_HIRED);
        Category category = dbHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course bio25 = dbHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, category, harry);
        Course bio10 = dbHelper.saveCourseToDatabase(BIO_TEN, COURSE_DESCR, category, harry);

        Timetable timetableA = dbHelper.saveTimetableToDatabase(MARCH_TEN_NINE_THIRTY, DURATION, bio25,
                harry,
                classroom);
        Timetable timetableB = dbHelper.saveTimetableToDatabase(MARCH_TEN_TWELVE_THIRTY, DURATION, bio10,
                harry,
                classroom);

        List<Timetable> result = dao.findDailyForTeacher(harry.getId(), MARCH_TEN);

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(timetableA, timetableB));
    }

    @Test
    void shouldReturnEmptyListIfNoTimetablesForStudentForGivenMonth() {
        Classroom classroom = dbHelper.saveClassroomToDatabase(CAPACITY);
        Teacher teacher = dbHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = dbHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = dbHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, category, teacher);
        Student student = dbHelper.saveStudentToDatabase(ANNA, SMITH, ENROLLMENT_DATE);

        dbHelper.addStudentToCoursesInDatabase(student, course);

        dbHelper.saveTimetableToDatabase(MARCH_TEN_NINE_THIRTY, DURATION, course, teacher, classroom);
        dbHelper.saveTimetableToDatabase(MARCH_TWENTY_FIFTH_NINE_THIRTY, DURATION, course, teacher, classroom);

        List<Timetable> result = dao.findMonthlyForStudent(student.getId(), Month.APRIL);

        assertThat(result, hasSize(0));
    }

    @Test
    void shouldFindAllTimetablesForStudentForGivenMonth() {
        Classroom classroom = dbHelper.saveClassroomToDatabase(CAPACITY);
        Teacher teacher = dbHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = dbHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = dbHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, category, teacher);
        Student student = dbHelper.saveStudentToDatabase(ANNA, SMITH, ENROLLMENT_DATE);

        dbHelper.addStudentToCoursesInDatabase(student, course);

        Timetable timetableA = dbHelper.saveTimetableToDatabase(MARCH_TEN_NINE_THIRTY, DURATION, course,
                teacher,
                classroom);
        Timetable timetableB = dbHelper.saveTimetableToDatabase(MARCH_TWENTY_FIFTH_NINE_THIRTY, DURATION,
                course,
                teacher,
                classroom);
        dbHelper.saveTimetableToDatabase(APRIL_FIFTH_NINE_THIRTY, DURATION, course, teacher, classroom);

        List<Timetable> result = dao.findMonthlyForStudent(student.getId(), Month.MARCH);

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(timetableA, timetableB));
    }

    @Test
    void shouldReturnEmptyListIfNoTimetablesForTeacherForGivenMonth() {
        Classroom classroom = dbHelper.saveClassroomToDatabase(CAPACITY);
        Teacher teacher = dbHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = dbHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = dbHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, category, teacher);

        dbHelper.saveTimetableToDatabase(MARCH_TEN_NINE_THIRTY, DURATION, course, teacher, classroom);
        dbHelper.saveTimetableToDatabase(MARCH_TWENTY_FIFTH_NINE_THIRTY, DURATION, course, teacher, classroom);

        List<Timetable> result = dao.findMonthlyForTeacher(teacher.getId(), Month.APRIL);

        assertThat(result, hasSize(0));
    }

    @Test
    void shouldFindAllTimetablesForTeacherForTeacherForGivenMonth() {
        Classroom classroom = dbHelper.saveClassroomToDatabase(CAPACITY);
        Teacher teacher = dbHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = dbHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = dbHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, category, teacher);

        Timetable timetableA = dbHelper.saveTimetableToDatabase(MARCH_TEN_NINE_THIRTY, DURATION, course,
                teacher,
                classroom);
        Timetable timetableB = dbHelper.saveTimetableToDatabase(MARCH_TWENTY_FIFTH_NINE_THIRTY, DURATION,
                course,
                teacher,
                classroom);
        dbHelper.saveTimetableToDatabase(APRIL_FIFTH_NINE_THIRTY, DURATION, course, teacher, classroom);

        List<Timetable> result = dao.findMonthlyForTeacher(teacher.getId(), Month.MARCH);

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(timetableA, timetableB));
    }

    @Test
    void shouldDeleteTimetableById() {
        Classroom classroom = dbHelper.saveClassroomToDatabase(CAPACITY);
        Teacher teacher = dbHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = dbHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = dbHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, category, teacher);
        Timetable expected = dbHelper.saveTimetableToDatabase(MARCH_TEN_NINE_THIRTY, DURATION, course,
                teacher,
                classroom);

        dao.deleteById(expected.getId());

        assertNoGivenTimetablesInDatabase(expected);
    }

    @Test
    void shouldReturnFalseIfNoTimetableWithGivenIdentifier() {
        boolean result = dao.existsById(1);

        assertFalse(result);
    }

    @Test
    void shouldReturnTrueIfTimetableWithGivenIdentifierExists() {
        Classroom classroom = dbHelper.saveClassroomToDatabase(CAPACITY);
        Teacher teacher = dbHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = dbHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = dbHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, category, teacher);
        Timetable expected = dbHelper.saveTimetableToDatabase(MARCH_TEN_NINE_THIRTY, DURATION, course,
                teacher,
                classroom);

        boolean result = dao.existsById(expected.getId());

        assertTrue(result);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingTimetable() {
        Classroom classroom = dbHelper.saveClassroomToDatabase(CAPACITY);
        Teacher teacher = dbHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = dbHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = dbHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, category, teacher);

        Timetable timetable = new Timetable(MARCH_TEN_NINE_THIRTY, DURATION, course, classroom, teacher);

        assertThrows(DaoException.class, () -> dao.update(timetable));
    }

    @Test
    void shouldUpdateExistingTimetable() {
        Classroom classroom = dbHelper.saveClassroomToDatabase(CAPACITY);
        Teacher teacher = dbHelper.saveTeacherToDatabase(JACK, THOMPSON, PROFESSOR, DATE_HIRED);
        Category category = dbHelper.saveCategoryToDatabase(CODE_BIO, DESCR_BIO);
        Course course = dbHelper.saveCourseToDatabase(BIO_TWENTY_FIVE, COURSE_DESCR, category, teacher);
        Timetable existing = dbHelper.saveTimetableToDatabase(MARCH_TEN_NINE_THIRTY, DURATION, course,
                teacher,
                classroom);

        Classroom newClassroom = dbHelper.saveClassroomToDatabase(CAPACITY + 10);
        int newDuration = DURATION + 30;
        LocalDateTime newTime = MARCH_TWENTY_FIFTH_NINE_THIRTY;
        existing.setClassroom(newClassroom);
        existing.setDurationInMinutes(newDuration);
        existing.setStartTime(newTime);

        dao.update(existing);

        assertTimetableInDatabase(existing);
    }

    private void assertTimetableInDatabase(Timetable expected) {
        Optional<Timetable> result = dbHelper.findAllTimetablesInDatabase()
                .stream()
                .filter(timetable -> timetable.equals(expected))
                .findFirst();

        assertTrue(result.isPresent());
        Timetable actual = result.get();
        assertThat(actual.getStartTime(), equalTo(expected.getStartTime()));
        assertThat(actual.getDurationInMinutes(), equalTo(expected.getDurationInMinutes()));
        assertThat(actual.getClassroom(), equalTo(expected.getClassroom()));
        assertThat(actual.getTeacher(), equalTo(expected.getTeacher()));
        assertThat(actual.getCourse(), equalTo(expected.getCourse()));
    }

    private void assertNoGivenTimetablesInDatabase(Timetable... expected) {
        List<Timetable> result = dbHelper.findAllTimetablesInDatabase();
        assertThat(result, not(containsInAnyOrder(expected)));
    }

    private void assertTimetablesInDatabase(Timetable... expected) {
        Arrays.stream(expected)
                .forEach(timetable -> assertThat("timetable should have id", timetable.getId(), is(not(nullValue()))));

        List<Timetable> result = dbHelper.findAllTimetablesInDatabase();
        assertThat(result, hasSize(expected.length));
        assertThat(result, containsInAnyOrder(expected));
    }

}
