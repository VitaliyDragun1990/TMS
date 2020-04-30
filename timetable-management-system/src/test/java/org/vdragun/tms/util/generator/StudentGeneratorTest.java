package org.vdragun.tms.util.generator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.util.generator.PersonGenerator.PersonGeneratorData;

@DisplayName("Student Generator")
class StudentGeneratorTest {

    private static final List<String> FIRST_NAMES = Arrays.asList("Jack", "Maggy", "Mary");
    private static final List<String> LAST_NAMES = Arrays.asList("Porter", "Smith", "Harris");
    private static final LocalDate BASE_DATE = LocalDate.of(2020, 03, 25);
    private static final int DEVIATION_DAYS = 180;
    private static final int NUMBER_OF_STUDENTS = 500;

    private StudentGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new StudentGenerator();
    }

    @Test
    void shouldGenerateCorrectNumberOfStudents() {
        PersonGeneratorData data = PersonGeneratorData.from(NUMBER_OF_STUDENTS, FIRST_NAMES, LAST_NAMES, BASE_DATE,
                DEVIATION_DAYS);

        List<Student> result = generator.generate(data);

        assertThat(result, hasSize(NUMBER_OF_STUDENTS));
    }

    @Test
    void shouldGenerateStudentsWithCorrectProperties() {
        PersonGeneratorData data = PersonGeneratorData.from(NUMBER_OF_STUDENTS, FIRST_NAMES, LAST_NAMES, BASE_DATE,
                DEVIATION_DAYS);

        List<Student> result = generator.generate(data);

        assertThat(result, everyItem(validStudent()));
    }

    private Matcher<Student> validStudent() {
        return new ValidStudentMatcher();
    }

    private class ValidStudentMatcher extends TypeSafeMatcher<Student> {

        @Override
        protected boolean matchesSafely(Student student) {
            assertThat(FIRST_NAMES, hasItem(equalTo(student.getFirstName())));
            assertThat(LAST_NAMES, hasItem(equalTo(student.getLastName())));
            LocalDate enrollmentDate = student.getEnrollmentDate();
            assertTrue(enrollmentDate.isAfter(BASE_DATE.minusDays(DEVIATION_DAYS + 1)), "enrollmand date too early");
            assertTrue(enrollmentDate.isBefore(BASE_DATE.plusDays(DEVIATION_DAYS + 1)), "enrollment date to late");
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("valid student");
        }

    }
}
