package org.vdragun.tms.util.generator;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.core.domain.Student;

/**
 * Convenient class for generating data for tests
 * 
 * @author Vitaliy Dragun
 *
 */
public class TestDataGenerator {

    /**
     * Generates fake {@link Student} instances.
     * 
     * @param number number of student instances to generate
     */
    public static List<Student> generateStudents(int number) {
        return IntStream.rangeClosed(1, number)
                .mapToObj(id -> new Student(id, "fname-" + id, "lname-" + id, LocalDate.now()))
                .collect(toList());
    }

    /**
     * Generates fake {@link Group} instances.
     * 
     * @param number number of group instances to generate
     */
    public static List<Group> generateGroups(int number) {
        return IntStream.rangeClosed(1, number)
                .mapToObj(id -> new Group(id, "name-" + id))
                .collect(toList());
    }

}
