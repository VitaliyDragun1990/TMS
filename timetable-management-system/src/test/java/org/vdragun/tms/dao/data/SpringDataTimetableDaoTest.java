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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
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
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.dao.DBTestHelper;
import org.vdragun.tms.dao.DaoTestConfig;
import org.vdragun.tms.dao.TimetableDao;

@DataJpaTest
@Import({ DaoTestConfig.class })
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DisplayName("Spring Data Timetable DAO")
public class SpringDataTimetableDaoTest {

    private static final LocalDate MARCH_TEN = LocalDate.of(2020, 3, 10);
    private static final LocalDate MARCH_TWENTY_FIFTH = LocalDate.of(2020, 3, 25);

    private static final LocalDateTime MARCH_TEN_NINE_THIRTY = LocalDateTime.of(2020, 3, 10, 9, 30);
    private static final LocalDateTime MARCH_TEN_TWELVE_THIRTY = LocalDateTime.of(2020, 3, 10, 12, 30);
    private static final LocalDateTime MARCH_TWENTY_FIFTH_NINE_THIRTY = LocalDateTime.of(2020, 3, 25, 9, 30);

    private static final LocalDateTime APRIL_FIFTH_NINE_THIRTY = LocalDateTime.of(2020, 4, 5, 9, 30);
    private static final LocalDateTime APRIL_TEN_NINE_THIRTY = LocalDateTime.of(2020, 4, 10, 9, 30);

    private static final int DURATION_NINETY = 90;
    private static final int CAPACITY_SIXTY = 60;

    private static final String MARY = "Mary";
    private static final String AMANDA = "Amanda";
    private static final String BIRKIN = "Birkin";
    private static final String JACK = "Jack";
    private static final String JOHN = "John";
    private static final String SMITH = "Smith";
    private static final String PORTER = "Porter";

    private static final String ADVANCED_BIOLOGY = "Advanced Biology";

    @Autowired
    private TimetableDao dao;

    @Autowired
    private DBTestHelper dbHelper;

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldReturnEmptyResultIfNoTimetableWithGivenIdInDatabase() {
        Optional<Timetable> result = dao.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/timetable_data.sql" })
    void shouldFindTimetableById() {
        Timetable expected = dbHelper.findRandomTimetableInDatabase();

        Optional<Timetable> result = dao.findById(expected.getId());

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(expected));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/timetable_data.sql" })
    void shouldSaveNewTimetableToDatabase() {
        Classroom classroom = dbHelper.findRandomClassroomInDatabase();
        Teacher teacher = dbHelper.findTeacherByNameInDatabase(JACK, SMITH);
        Course course = dbHelper.findCourseByNameInDatabase(ADVANCED_BIOLOGY);
        Timetable timetable = new Timetable(APRIL_FIFTH_NINE_THIRTY, DURATION_NINETY, course, classroom, teacher);

        dao.save(timetable);
        dbHelper.flushChangesToDatabase();

        assertTimetablesInDatabase(timetable);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/timetable_data.sql" })
    void shouldSaveSeveralNewTimetablesToDatabase() {
        Classroom classroom = dbHelper.findRandomClassroomInDatabase();
        Teacher teacher = dbHelper.findTeacherByNameInDatabase(JACK, SMITH);
        Course course = dbHelper.findCourseByNameInDatabase(ADVANCED_BIOLOGY);
        Timetable timetableA = new Timetable(APRIL_FIFTH_NINE_THIRTY, DURATION_NINETY, course, classroom, teacher);
        Timetable timetableB = new Timetable(APRIL_TEN_NINE_THIRTY, DURATION_NINETY, course, classroom, teacher);

        dao.saveAll(asList(timetableA, timetableB));
        dbHelper.flushChangesToDatabase();

        assertTimetablesInDatabase(timetableA, timetableB);
    }

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldReturnEmptyListIfNoTimetablesInDatabase() {
        List<Timetable> result = dao.findAll();

        assertThat(result, hasSize(0));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/timetable_data.sql" })
    void shouldFindAllTimetablesInDatabase() {
        List<Timetable> result = dao.findAll();

        assertTimetablesForDates(
                result,
                MARCH_TEN_NINE_THIRTY, MARCH_TEN_TWELVE_THIRTY, MARCH_TWENTY_FIFTH_NINE_THIRTY);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/timetable_data.sql" })
    void shouldReturnEmptyListIfNoTimetablesForStudentForGivenDay() {
        Student amanda = dbHelper.findStudentByNameInDatabase(AMANDA, BIRKIN);

        List<Timetable> result = dao.findDailyForStudent(amanda.getId(), MARCH_TWENTY_FIFTH);

        assertThat(result, hasSize(0));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/timetable_data.sql" })
    void shouldFindAllTimetablesForStudentForGivenDay() {
        Student mary = dbHelper.findStudentByNameInDatabase(MARY, BIRKIN);

        List<Timetable> result = dao.findDailyForStudent(mary.getId(), MARCH_TEN);

        assertTimetablesForDates(
                result,
                MARCH_TEN_NINE_THIRTY, MARCH_TEN_TWELVE_THIRTY);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/timetable_data.sql" })
    void shouldReturnEmptyListIfNoTimetablesForTeacherForGivenDay() {
        Teacher john = dbHelper.findTeacherByNameInDatabase(JOHN, PORTER);

        List<Timetable> result = dao.findDailyForTeacher(john.getId(), MARCH_TWENTY_FIFTH);

        assertThat(result, hasSize(0));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/timetable_data.sql" })
    void shouldFindAllTimetablesForTeacherForGivenDay() {
        Teacher jack = dbHelper.findTeacherByNameInDatabase(JACK, SMITH);

        List<Timetable> result = dao.findDailyForTeacher(jack.getId(), MARCH_TEN);

        assertTimetablesForDates(
                result,
                MARCH_TEN_NINE_THIRTY);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/timetable_data.sql" })
    void shouldReturnEmptyListIfNoTimetablesForStudentForGivenMonth() {
        Student mary = dbHelper.findStudentByNameInDatabase(MARY, BIRKIN);

        List<Timetable> result = dao.findMonthlyForStudent(mary.getId(), Month.APRIL);

        assertThat(result, hasSize(0));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/timetable_data.sql" })
    void shouldFindAllTimetablesForStudentForGivenMonth() {
        Student mary = dbHelper.findStudentByNameInDatabase(MARY, BIRKIN);

        List<Timetable> result = dao.findMonthlyForStudent(mary.getId(), Month.MARCH);

        assertTimetablesForDates(
                result,
                MARCH_TEN_NINE_THIRTY, MARCH_TEN_TWELVE_THIRTY, MARCH_TWENTY_FIFTH_NINE_THIRTY);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/timetable_data.sql" })
    void shouldReturnEmptyListIfNoTimetablesForTeacherForGivenMonth() {
        Teacher john = dbHelper.findTeacherByNameInDatabase(JOHN, PORTER);

        List<Timetable> result = dao.findMonthlyForTeacher(john.getId(), Month.APRIL);

        assertThat(result, hasSize(0));
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/timetable_data.sql" })
    void shouldFindAllTimetablesForTeacherForGivenMonth() {
        Teacher jack = dbHelper.findTeacherByNameInDatabase(JACK, SMITH);

        List<Timetable> result = dao.findMonthlyForTeacher(jack.getId(), Month.MARCH);

        assertTimetablesForDates(
                result,
                MARCH_TEN_NINE_THIRTY, MARCH_TWENTY_FIFTH_NINE_THIRTY);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/timetable_data.sql" })
    void shouldDeleteTimetableById() {
        Timetable expected = dbHelper.findRandomTimetableInDatabase();

        dao.deleteById(expected.getId());
        dbHelper.flushChangesToDatabase();

        assertNoGivenTimetablesInDatabase(expected);
    }

    @Test
    @Sql(scripts = "/sql/clear_database.sql")
    void shouldReturnFalseIfNoTimetableWithGivenIdentifier() {
        boolean result = dao.existsById(1);

        assertFalse(result);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/timetable_data.sql" })
    void shouldReturnTrueIfTimetableWithGivenIdentifierExists() {
        Timetable expected = dbHelper.findRandomTimetableInDatabase();

        boolean result = dao.existsById(expected.getId());

        assertTrue(result);
    }

    @Test
    @Sql(scripts = { "/sql/clear_database.sql", "/sql/timetable_data.sql" })
    void shouldUpdateExistingTimetable() {
        Timetable existing = dbHelper.findRandomTimetableInDatabase();

        Teacher newTeacher = dbHelper.findTeacherByNameInDatabase(JOHN, PORTER);
        Classroom newClassroom = dbHelper.findClassroomByCapacity(CAPACITY_SIXTY);
        int newDuration = DURATION_NINETY;
        LocalDateTime newTime = APRIL_FIFTH_NINE_THIRTY;

        existing.setTeacher(newTeacher);
        existing.setClassroom(newClassroom);
        existing.setDurationInMinutes(newDuration);
        existing.setStartTime(newTime);

        dao.update(existing);
        dbHelper.flushChangesToDatabase();

        assertTimetableUpdatedInDatabase(existing);
    }

    private void assertTimetablesForDates(List<Timetable> result, LocalDateTime... expectedDates) {
        assertThat(result, hasSize(expectedDates.length));
        for (LocalDateTime expectedDate : expectedDates) {
            assertThat(result, hasItem(hasProperty("startTime", equalTo(expectedDate))));
        }
    }

    private void assertTimetableUpdatedInDatabase(Timetable expected) {
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
        assertThat(result, not(hasItems(expected)));
    }

    private void assertTimetablesInDatabase(Timetable... expected) {
        Arrays.stream(expected)
                .forEach(timetable -> assertThat("timetable should have id", timetable.getId(), is(not(nullValue()))));

        List<Timetable> result = dbHelper.findAllTimetablesInDatabase();
        assertThat(result, hasItems(expected));
    }

}
