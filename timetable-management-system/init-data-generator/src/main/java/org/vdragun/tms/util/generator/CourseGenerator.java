package org.vdragun.tms.util.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;

/**
 * Randomly generates {@link Course} instances using provided data
 * 
 * @author Vitaliy Dragun
 *
 */
public class CourseGenerator {

    private static final int MAX_ATTEMPTS = 1000;

    private static final String COURSE_DESCRIPTION_TEMPLATE = "Awesome %s course";

    /**
     * Generates courses using initial data
     */
    public List<Course> generate(CourseGeneratorData data) {
        Set<String> takenNames = new HashSet<>();
        List<Course> result = new ArrayList<>();

        for (int i = 0; i < data.number(); i++) {
            Teacher teacher = randomTeacher(data);
            Category category = randomCategory(data);
            String courseName = generateName(data, category);

            int attempt = 0;
            while (takenNames.contains(courseName)) {
                category = randomCategory(data);
                courseName = generateName(data, category);
                assertAttempsRemain(attempt++);
            }
            takenNames.add(courseName);
            result.add(new Course(courseName, generateDescription(courseName), category, teacher));
        }

        return result;
    }

    private String generateDescription(String courseName) {
        return String.format(COURSE_DESCRIPTION_TEMPLATE, courseName);
    }

    private void assertAttempsRemain(int attempt) {
        if (attempt > MAX_ATTEMPTS) {
            throw new GenerationException("Fail to generate courses: Maximum number of attempts has been reached");
        }
    }

    private String generateName(CourseGeneratorData data, Category category) {
        return String.format("%s %s", randomPrefix(data), category.getDescription());
    }

    private Teacher randomTeacher(CourseGeneratorData data) {
        return data.teachers().get(position(data.teachers().size()));
    }

    private Category randomCategory(CourseGeneratorData data) {
        return data.categories().get(position(data.categories().size()));
    }

    private String randomPrefix(CourseGeneratorData data) {
        return data.prefixes().get(position(data.prefixes().size()));
    }

    private int position(int bound) {
        return ThreadLocalRandom.current().nextInt(0, bound);
    }

    public static class CourseGeneratorData {

        private int number;

        private List<String> prefixes;

        private List<Category> categories;

        private List<Teacher> teachers;

        public CourseGeneratorData(int number, List<String> prefixes, List<Category> categories,
                List<Teacher> teachers) {
            this.number = number;
            this.prefixes = prefixes;
            this.categories = categories;
            this.teachers = teachers;
        }

        public static CourseGeneratorData from(int number, List<String> prefixes, List<Category> categories,
                List<Teacher> teachers) {
            return new CourseGeneratorData(number, prefixes, categories, teachers);
        }

        public int number() {
            return number;
        }

        public List<String> prefixes() {
            return prefixes;
        }

        public List<Category> categories() {
            return categories;
        }

        public List<Teacher> teachers() {
            return teachers;
        }

    }
}
