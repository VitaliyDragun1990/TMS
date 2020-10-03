package org.vdragun.tms.util.generator;

import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Title;

/**
 * Randomly generates {@link Teacher} instances using provided data
 * 
 * @author Vitaliy Dragun
 *
 */
public class TeacherGenerator extends PersonGenerator<Teacher> {

    @Override
    protected Teacher generatePerson(PersonGeneratorData data) {
        Title[] titles = Title.values();
        return new Teacher(
                data.getFirstNames().get(position(data.getFirstNames().size())),
                data.getLastNames().get(position(data.getLastNames().size())),
                titles[position(titles.length)],
                targetDate(data.getBaseDate(), data.getDateDeviationInDays()));
    }

}
