package org.vdragun.tms.ui.web.controller;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.core.domain.Title;

/**
 * Convenient class for entity generation for tests purpose
 * 
 * @author Vitaliy Dragun
 *
 */
public class EntityGenerator {

    private static final String FIRST_NAME = "Jack-";
    private static final String LAST_NAME = "Smith-";
    private static final String COURSE = "Course-";
    private static final String DESCRIPTION = "Description";
    private static final String CATEGORY = "ART";
    private static final String GROUP = "ps-";

    public Course generateCourse() {
        Teacher teacher = generateTeacher();
        Category category = generateCategory();
        int randomIdx = randomInt(9999);

        return new Course(
                randomIdx,
                COURSE + randomIdx,
                category,
                DESCRIPTION,
                teacher);
    }
    
    public List<Course> generateCourses(int number) {
        return IntStream.rangeClosed(1, number)
                .mapToObj(idx -> generateCourse())
                .collect(toList());
    }

    public Teacher generateTeacher() {
        int randomIdx = randomInt(9999);
        return new Teacher(
                randomIdx,
                FIRST_NAME + randomIdx,
                LAST_NAME + randomIdx,
                Title.values()[randomInt(0, Title.values().length - 1)],
                LocalDate.now().minusDays(randomInt(90)));
    }

    public List<Teacher> generateTeachers(int number) {
        return IntStream.rangeClosed(1, number)
                .mapToObj(idx -> generateTeacher())
                .collect(toList());
    }

    public Category generateCategory() {
        int randomIdx = randomInt(9999);
        return new Category(randomIdx, CATEGORY, DESCRIPTION);
    }

    public List<Category> generateCategories(int number) {
        return IntStream.rangeClosed(1, number)
                .mapToObj(idx -> generateCategory())
                .collect(toList());
    }

    public Student generateStudent() {
        int randomIdx = randomInt(9999);
        Student student = new Student(
                randomIdx,
                FIRST_NAME + randomIdx,
                LAST_NAME + randomIdx,
                LocalDate.now().minusDays(randomInt(90)));
        student.setGroup(generateGroup());

        return student;
    }

    public List<Student> generateStudents(int number) {
        return IntStream.rangeClosed(1, number)
                .mapToObj(idx -> generateStudent())
                .collect(toList());
    }

    public Timetable generateTimetable() {
        return null;
    }

    public List<Timetable> generateTimetables(int number) {
        return null;
    }

    public Classroom generateClassroom() {
        return new Classroom(randomInt(9999), randomInt(30, 60));
    }

    public Group generateGroup() {
        int randomIdx = randomInt(9999);
        return new Group(randomIdx, GROUP + randomInt(10, 99));
    }

    private int randomInt(int inner, int upper) {
        return ThreadLocalRandom.current().nextInt(inner, upper);
    }

    private int randomInt(int upper) {
        return ThreadLocalRandom.current().nextInt(1, upper);
    }
}
