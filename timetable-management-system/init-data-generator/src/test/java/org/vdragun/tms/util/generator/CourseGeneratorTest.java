package org.vdragun.tms.util.generator;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.vdragun.tms.core.domain.Title.ASSOCIATE_PROFESSOR;
import static org.vdragun.tms.core.domain.Title.INSTRUCTOR;
import static org.vdragun.tms.core.domain.Title.PROFESSOR;

import java.time.LocalDate;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.util.generator.CourseGenerator.CourseGeneratorData;

@DisplayName("Course Generator")
public class CourseGeneratorTest {

    private static final int NUMBER_OF_COURSES = 10;

    private static final List<String> PREFIXES = asList("Beginner", "Intermediate", "Basic", "Advanced");

    private static final List<Category> CATEGORIES = asList(
            new Category("ART", "Art"),
            new Category("BIO", "Biology"),
            new Category("HIS", "History"),
            new Category("ENG", "English"));

    private static final List<Teacher> TEACHERS = asList(
            new Teacher("Jack", "Smith", ASSOCIATE_PROFESSOR, LocalDate.now()),
            new Teacher("Anna", "Snow", PROFESSOR, LocalDate.now()),
            new Teacher("Maria", "Porter", INSTRUCTOR, LocalDate.now()));

    private CourseGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new CourseGenerator();
    }

    @Test
    void shouldGenerateCorrectNumberOfCourses() {
        CourseGeneratorData data = CourseGeneratorData.from(NUMBER_OF_COURSES, PREFIXES, CATEGORIES, TEACHERS);

        List<Course> result = generator.generate(data);

        assertThat(result, hasSize(NUMBER_OF_COURSES));
    }

    @Test
    void shouldGenerateCoursesWithCorrectData() {
        CourseGeneratorData data = CourseGeneratorData.from(NUMBER_OF_COURSES, PREFIXES, CATEGORIES, TEACHERS);

        List<Course> result = generator.generate(data);

        assertThat(result, everyItem(validCourse()));
    }

    private Matcher<Course> validCourse() {
        return new ValidCourseMatcher();
    }

    private class ValidCourseMatcher extends TypeSafeMatcher<Course> {

        @Override
        public void describeTo(Description description) {
            description.appendText("valid course");
        }

        @Override
        protected boolean matchesSafely(Course course) {
            String namePrefix = course.getName().split(" ")[0];
            String description = course.getDescription();

            assertThat(course.getTeacher(), is(not(nullValue())));
            assertThat(course.getCategory(), is(not(nullValue())));
            assertThat(course.getName(), containsString(course.getCategory().getDescription()));
            assertThat(PREFIXES, hasItem(equalTo(namePrefix)));
            assertThat(description, containsString(course.getName()));
            return true;
        }

    }

}
