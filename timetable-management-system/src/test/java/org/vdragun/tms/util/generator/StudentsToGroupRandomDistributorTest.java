package org.vdragun.tms.util.generator;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.core.domain.Student;

@DisplayName("Students to group random distributor")
public class StudentsToGroupRandomDistributorTest {

    private static final int MAX_STUDENTS_PER_GROUP = 30;
    private static final int MIN_STUDENTS_PER_GROUP = 10;
    private static final int NUMBER_OF_STUDENTS = 200;
    private static final int NUMBER_OF_GROUPS = 10;

    private StudentsToGroupRandomDistributor distributor;

    @BeforeEach
    void setUp() {
        distributor = new StudentsToGroupRandomDistributor();
    }

    @Test
    void shouldRandomlyAssignStudentsToGroups() {
        List<Group> groups = TestDataGenerator.generateGroups(NUMBER_OF_GROUPS);
        List<Student> students = TestDataGenerator.generateStudents(NUMBER_OF_STUDENTS);

        distributor.assignStudentsToGroups(students, groups, MIN_STUDENTS_PER_GROUP, MAX_STUDENTS_PER_GROUP);

        assertThatMajorityOfStudentsHasGroup(students);
        assertThatMajorityOfGroupsHasStudents(groups, students);
        assertThatNumberOfStudentsInGroupsIsBetweenSpecifiedRange(students, MIN_STUDENTS_PER_GROUP,
                MAX_STUDENTS_PER_GROUP);
    }

    private void assertThatMajorityOfStudentsHasGroup(List<Student> students) {
        int studentsWithGroup = (int) students.stream().filter(student -> student.getGroup() != null).count();
        assertThat(studentsWithGroup, greaterThan(students.size() / 2));
    }

    private void assertThatMajorityOfGroupsHasStudents(List<Group> groups, List<Student> students) {
        int groupsWithStudents = (int) students.stream()
                .filter(student -> student.getGroup() != null)
                .map(Student::getGroup)
                .filter(group -> group != null)
                .map(Group::getId)
                .distinct().count();
        assertThat(groupsWithStudents, is(greaterThan(groups.size() / 2)));
    }

    private void assertThatNumberOfStudentsInGroupsIsBetweenSpecifiedRange(
            List<Student> students,
            int minStudentsPerGroup,
            int maxStudentsPerGroup) {
        Map<Integer, Integer> groupsWithStudents = students
                .stream()
                .map(Student::getGroup)
                .filter(group -> group != null)
                .map(Group::getId)
                .collect(toMap(identity(), id -> 1, (oldVal, newVal) -> oldVal + newVal));

        for (int numberOfStudents : groupsWithStudents.values()) {
            assertThat(
                    "number of students in one group should be greater than or equal to " + minStudentsPerGroup,
                    numberOfStudents,
                    is(greaterThanOrEqualTo(minStudentsPerGroup)));
            assertThat(
                    "number of students in one group should be less than or equal to " + maxStudentsPerGroup,
                    numberOfStudents,
                    is(lessThanOrEqualTo(maxStudentsPerGroup)));
        }
    }

}
