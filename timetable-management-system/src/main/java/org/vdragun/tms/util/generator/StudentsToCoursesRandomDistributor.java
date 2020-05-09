package org.vdragun.tms.util.generator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Student;

/**
 * Randomly assigns given students between given courses.
 * 
 * @author Vitaliy Dragun
 *
 */
public class StudentsToCoursesRandomDistributor {

    /**
     * Randomly assigns specified students to given courses.
     * 
     * @param students             student to assign to courses
     * @param courses              courses to assign students to
     * @param maxCoursesPerStudent maximum number of courses each student could be
     *                             assigned to
     */
    public void assignStudentsToCourses(List<Student> students, List<Course> courses, int maxCoursesPerStudent) {
        for (Student student : students) {
            int numberOfCourses = getRandomNumberOfCourses(maxCoursesPerStudent);
            student.setCourses(getStudentCourses(numberOfCourses, courses));
        }
    }

    private Set<Course> getStudentCourses(int numberOfCourses, List<Course> courses) {
        Set<Course> result = new HashSet<>();

        while (result.size() != numberOfCourses) {
            result.add(getRandomCourse(courses));
        }
        return new HashSet<>(result);
    }

    private Course getRandomCourse(List<Course> courses) {
        int courseIndex = generateRandomNumber(0, courses.size());
        return courses.get(courseIndex);
    }

    private int getRandomNumberOfCourses(int maxCoursesPerStudent) {
        return generateRandomNumber(1, maxCoursesPerStudent + 1);
    }

    private int generateRandomNumber(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }
}
