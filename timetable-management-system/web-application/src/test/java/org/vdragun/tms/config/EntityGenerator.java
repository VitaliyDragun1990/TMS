package org.vdragun.tms.config;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
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

    private static final List<String> UPPER_CASE_LETTERS = Arrays.stream("abcdefghijklmnopqrstuvwxyz".split(""))
            .map(String::toUpperCase)
            .collect(toList());

    private static final List<String> LOWER_CASE_LETTERS = UPPER_CASE_LETTERS.stream()
            .map(String::toLowerCase)
            .collect(toList());

    private static final String FIRST_NAME = "Jack";

    private static final String LAST_NAME = "Smith";

    private static final String COURSE = "Course ";

    private static final String DESCRIPTION = "Description";

    private static final String GROUP = "ps-";

    public Course generateCourse() {
        Teacher teacher = generateTeacher();
        Category category = generateCategory();
        int randomIdx = randomInt(9999);

        return new Course(
                randomIdx,
                COURSE + generateRandomString(4),
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
                FIRST_NAME + generateRandomString(3),
                LAST_NAME + generateRandomString(
                        3),
                Title.values()[randomInt(0, Title.values().length - 1)],
                LocalDate.now().minusDays(randomInt(90)));
    }

    public List<Teacher> generateTeachers(int number) {
        return IntStream.rangeClosed(1, number)
                .mapToObj(idx -> generateTeacher())
                .collect(toList());
    }

    public List<Teacher> generateTeachersWithCourse(int numberOfTeachers, int coursesPerTeacher) {
        return IntStream.rangeClosed(1, numberOfTeachers)
                .mapToObj(idx -> {
                    Teacher t = generateTeacher();
                    generateCourses(coursesPerTeacher).forEach(c -> t.addCourse(c));
                    return t;
                }).collect(toList());
    }

    public Category generateCategory() {
        int randomIdx = randomInt(9999);
        return new Category(
                randomIdx,
                generateCategoryName(),
                DESCRIPTION);
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
                FIRST_NAME + generateRandomString(3),
                LAST_NAME + generateRandomString(
                        3),
                LocalDate.now().minusDays(randomInt(90)));
        student.setGroup(generateGroup());

        return student;
    }

    public List<Student> generateStudents(int number) {
        return IntStream.rangeClosed(1, number)
                .mapToObj(idx -> generateStudent())
                .collect(toList());
    }

    public List<Student> generateStudentsWithCourses(int numberOfStudents, int coursesPerStudent) {
        return IntStream.rangeClosed(1, numberOfStudents)
                .mapToObj(idx -> {
                    Student s = generateStudent();
                    generateCourses(coursesPerStudent).forEach(c -> s.addCourse(c));
                    return s;
                }).collect(toList());
    }

    public Timetable generateTimetable() {
        return new Timetable(
                randomInt(9999),
                LocalDateTime.now().plusDays(randomInt(1, 30)).truncatedTo(ChronoUnit.MINUTES),
                randomInt(30, 60),
                generateCourse(),
                generateClassroom());
    }

    public List<Timetable> generateTimetables(int number) {
        return IntStream.rangeClosed(1, number)
                .mapToObj(idx -> generateTimetable())
                .collect(toList());
    }

    public Classroom generateClassroom() {
        return new Classroom(randomInt(9999), randomInt(30, 60));
    }

    public List<Classroom> generateClassrooms(int number) {
        return IntStream.rangeClosed(1, number)
                .mapToObj(idx -> generateClassroom())
                .collect(toList());
    }

    public Group generateGroup() {
        int randomIdx = randomInt(9999);
        return new Group(randomIdx, GROUP + randomInt(10, 99));
    }

    public List<Group> generateGroups(int number) {
        return IntStream.rangeClosed(1, number)
                .mapToObj(idx -> generateGroup())
                .collect(toList());
    }

    private int randomInt(int inner, int upper) {
        return ThreadLocalRandom.current().nextInt(inner, upper);
    }

    private int randomInt(int upper) {
        return ThreadLocalRandom.current().nextInt(1, upper);
    }

    private String generateCategoryName() {
        return IntStream.rangeClosed(1, 3)
                .mapToObj(i -> UPPER_CASE_LETTERS.get(randomInt(0, UPPER_CASE_LETTERS.size())))
                .collect(joining());
    }

    private String generateRandomString(int charCount) {
        return IntStream.rangeClosed(1, charCount)
                .mapToObj(i -> LOWER_CASE_LETTERS.get(randomInt(0, LOWER_CASE_LETTERS.size())))
                .collect(joining());
    }
}
