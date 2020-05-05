package org.vdragun.tms.util.generator;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.core.domain.Title;

@DisplayName("Timetable Generator")
public class TimetableGeneratorTest {

    private static final int NUMBER_OF_MONTH = 1;
    private static final LocalTime TIMETABLE_START_TIME = LocalTime.of(9, 30);
    private static final LocalTime TIMETABLE_END_TIME = LocalTime.of(19, 30);
    private static final int CLASS_DURATION_MINUTES = 60;
    private static final int MAX_CLASSES_PER_WEEK = 3;

    private static final Teacher TEACHER = new Teacher(1, "John", "Doe", Title.PROFESSOR);
    private static final Category CATEGORY = new Category(1, "ART", "Art");

    private static final List<Classroom> CLASSROOMS = Arrays.asList(
            new Classroom(1, 50),
            new Classroom(2, 50),
            new Classroom(3, 50),
            new Classroom(4, 50),
            new Classroom(5, 50),
            new Classroom(6, 50),
            new Classroom(7, 50));

    private static final List<Course> COURSES = Arrays.asList(
            buildCourse(1, "Beginner English"),
            buildCourse(2, "Core Biology"),
            buildCourse(3, "Advanced Math"),
            buildCourse(4, "Intermediate History"));

    private TimetableGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new TimetableGenerator(
                TIMETABLE_START_TIME,
                TIMETABLE_END_TIME,
                CLASS_DURATION_MINUTES,
                NUMBER_OF_MONTH,
                MAX_CLASSES_PER_WEEK);
    }

    @Test
    void shouldGenerateTimetables() {
        List<Timetable> result = generator.generate(COURSES, CLASSROOMS);

        assertFalse(result.isEmpty());
    }

    @Test
    void shouldGenerateTimetablesForEachSpecifiedCourse() {
        List<Timetable> result = generator.generate(COURSES, CLASSROOMS);

        Set<Course> resultCourses = result.stream().map(Timetable::getCourse).collect(toSet());

        assertThat(resultCourses, hasSize(COURSES.size()));
    }
    
    @Test
    void shouldGenerateAtLeastOneTimetablePerCoursePerWeek() {
        List<Timetable> result = generator.generate(COURSES, CLASSROOMS);
        
        Map<Course, Integer> coursesMap = result
                .stream()
                .map(Timetable::getCourse)
                .collect(toMap(identity(), course -> 1, (oldVal, newVal) -> oldVal + newVal));

        for (Integer timetablesPerCourse : coursesMap.values()) {
            assertThat(timetablesPerCourse, is(greaterThanOrEqualTo(NUMBER_OF_MONTH * 4)));
        }
    }

    private static Course buildCourse(Integer id, String name) {
        return new Course(id, name, CATEGORY, name, TEACHER);
    }

}
