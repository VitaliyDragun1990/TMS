package org.vdragun.tms.util.generator;

import org.vdragun.tms.core.domain.Student;

/**
 * Randomly generates {@link Student} instances using provided data
 * 
 * @author Vitaliy Dragun
 *
 */
public class StudentGenerator extends PersonGenerator<Student> {

    @Override
    protected Student generatePerson(PersonGeneratorData data) {
        return new Student(
                data.getFirstNames().get(position(data.getFirstNames().size())),
                data.getLastNames().get(position(data.getLastNames().size())),
                targetDate(data.getBaseDate(), data.getDateDeviationInDays()));
    }

}
