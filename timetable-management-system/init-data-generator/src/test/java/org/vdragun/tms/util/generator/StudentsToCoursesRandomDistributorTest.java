package org.vdragun.tms.util.generator;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Student;

@DisplayName("Students to courses random distributor")
public class StudentsToCoursesRandomDistributorTest {

    private static final int MAX_COURSES_PER_STUDENT = 3;

    private StudentsToCoursesRandomDistributor distributor;

    @BeforeEach
    void setUp() {
        distributor = new StudentsToCoursesRandomDistributor();
    }

    @Test
    void shouldAssignStudentsToCourses() {
        List<Student> students = TestDataGenerator.generateStudents(200);
        List<Course> courses = TestDataGenerator.generateCourses(10);

        distributor.assignStudentsToCourses(students, courses, MAX_COURSES_PER_STUDENT);

        assertThatAllStudentsWithCourses(students);
        assertThatEachStudentNotExceedMaxNumberOfCourses(students);
    }

    private void assertThatEachStudentNotExceedMaxNumberOfCourses(List<Student> students) {
        assertThat(students, everyItem(hasProperty("courses", hasSize(lessThanOrEqualTo(MAX_COURSES_PER_STUDENT)))));

    }

    private void assertThatAllStudentsWithCourses(List<Student> students) {
        assertThat(students, everyItem(hasProperty("courses", hasSize(greaterThan(0)))));
    }

}
