package org.vdragun.tms.util.generator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.vdragun.tms.core.domain.Classroom;

@DisplayName("Classroom generator")
class ClassroomGeneratorTest {

    private static final int NUMBER_OF_CLASSROOMS = 30;
    private static final int FROM_CAPACITY = 30;
    private static final int TO_CAPACITY = 60;

    private ClassroomGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new ClassroomGenerator();
    }

    @Test
    void shouldGenerateSpecifiedNumberOfClassrooms() {
        List<Classroom> result = generator.generate(NUMBER_OF_CLASSROOMS, FROM_CAPACITY, TO_CAPACITY);

        assertThat(result, hasSize(NUMBER_OF_CLASSROOMS));
    }

    @Test
    void shouldGenerateClassroomWithCapacityWithingSpecifiedRange() {
        List<Classroom> result = generator.generate(NUMBER_OF_CLASSROOMS, FROM_CAPACITY, TO_CAPACITY);

        result.forEach(this::assertCapacityWithinRange);
    }

    private void assertCapacityWithinRange(Classroom classroom) {
        assertThat(classroom.getCapacity(), greaterThanOrEqualTo(FROM_CAPACITY));
        assertThat(classroom.getCapacity(), lessThanOrEqualTo(TO_CAPACITY));
    }

}
