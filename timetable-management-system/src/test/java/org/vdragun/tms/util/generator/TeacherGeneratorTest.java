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
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.util.generator.PersonGenerator.PersonGeneratorData;

@DisplayName("Teacher Generator")
class TeacherGeneratorTest {

    private static final List<String> FIRST_NAMES = Arrays.asList("Jack", "Maggy", "Mary");
    private static final List<String> LAST_NAMES = Arrays.asList("Porter", "Smith", "Harris");
    private static final LocalDate BASE_DATE = LocalDate.of(2020, 03, 25);
    private static final int DEVIATION_DAYS = 180;
    private static final int NUMBER_OF_TEACHERS = 500;

    private TeacherGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new TeacherGenerator();
    }

    @Test
    void shouldGenerateCorrectNumberOfTeachers() {
        PersonGeneratorData data = PersonGeneratorData.from(NUMBER_OF_TEACHERS, FIRST_NAMES, LAST_NAMES, BASE_DATE,
                DEVIATION_DAYS);

        List<Teacher> result = generator.generate(data);

        assertThat(result, hasSize(NUMBER_OF_TEACHERS));
    }

    @Test
    void shouldGenerateTeachersWithCorrectProperties() {
        PersonGeneratorData data = PersonGeneratorData.from(NUMBER_OF_TEACHERS, FIRST_NAMES, LAST_NAMES, BASE_DATE,
                DEVIATION_DAYS);

        List<Teacher> result = generator.generate(data);

        assertThat(result, everyItem(validTeacher()));
    }

    private Matcher<Teacher> validTeacher() {
        return new ValidTeacherMatcher();
    }

    private class ValidTeacherMatcher extends TypeSafeMatcher<Teacher> {

        @Override
        protected boolean matchesSafely(Teacher teacher) {
            assertThat(FIRST_NAMES, hasItem(equalTo(teacher.getFirstName())));
            assertThat(LAST_NAMES, hasItem(equalTo(teacher.getLastName())));
            LocalDate dateHired = teacher.getDateHired();
            assertTrue(dateHired.isAfter(BASE_DATE.minusDays(DEVIATION_DAYS + 1)), "date hired too early");
            assertTrue(dateHired.isBefore(BASE_DATE.plusDays(DEVIATION_DAYS + 1)), "date hired to late");
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("valid teacher");
        }

    }

}
