package org.vdragun.tms.util.generator;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.vdragun.tms.core.domain.Classroom;

/**
 * Creates {@link Classroom} instances with randomly generated capacity
 * according to specified range
 * 
 * @author Vitaliy Dragun
 *
 */
public class ClassroomGenerator {

    /**
     * Generates provided number of classrooms with capacity greater or equal to
     * {@code fromCapacity} and capacity less than or equal to {@code toCapacity}
     */
    public List<Classroom> generate(int number, int fromCapacity, int toCapacity) {
        return IntStream.rangeClosed(1, number)
                .mapToObj(n -> generateClassroom(fromCapacity, toCapacity))
                .collect(toList());
    }

    private Classroom generateClassroom(int fromCapacity, int toCapacity) {
        return new Classroom(capacity(fromCapacity, toCapacity));
    }

    private int capacity(int fromCapacity, int toCapacity) {
        return ThreadLocalRandom.current().nextInt(fromCapacity, toCapacity + 1);
    }
}
