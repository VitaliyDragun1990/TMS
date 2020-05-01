package org.vdragun.tms.util.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.core.domain.Student;

/**
 * Randomly assigns given students between given groups.
 * 
 * @author Vitaliy Dragun
 *
 */
public class StudentsToGroupRandomDistributor {

    /**
     * Randomly assigns specified students among given groups. Each group could
     * contain from {@code minStudentPerGroup} to {@code maxStudentPerGroup}
     * students. It is possible that some groups will be left without students and
     * some students without group.
     * 
     * @param students            students to assign to group
     * @param groups              groups to assign students to
     * @param minStudentsPerGroup minimum number of students that can be assigned to
     *                            single group
     * @param maxStudentsPerGroup maximum number of student that can be assigned to
     *                            single group
     */
    public void assignStudentsToGroups(List<Student> students, List<Group> groups, int minStudentsPerGroup,
            int maxStudentsPerGroup) {
        List<Student> studentsToAssign = new ArrayList<>(students);

        for (Group group : groups) {
            if (randomlySkipGroup()) {
                continue;
            }

            int groupCapacity = generateGroupCapacity(minStudentsPerGroup, maxStudentsPerGroup);

            for (int i = 0; i < groupCapacity && !studentsToAssign.isEmpty(); i++) {
                Student student = getRandomStudent(studentsToAssign);
                student.setGroup(group);
            }
        }
    }

    private Student getRandomStudent(List<Student> studentsToAssign) {
        int studentIndex = generateRandomNumber(studentsToAssign.size());
        return studentsToAssign.remove(studentIndex);
    }

    private int generateGroupCapacity(int minStudentsPerGroup, int maxStudentsPerGroup) {
        return generateRandomNumber(minStudentsPerGroup, maxStudentsPerGroup + 1);
    }

    private int generateRandomNumber(int bound) {
        return generateRandomNumber(0, bound);
    }

    private int generateRandomNumber(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }

    private boolean randomlySkipGroup() {
        return ThreadLocalRandom.current().nextBoolean() && !ThreadLocalRandom.current().nextBoolean();
    }
}
