package org.vdragun.tms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vdragun.tms.ui.common.converter.LocalDateCustomFormatter;
import org.vdragun.tms.ui.common.converter.LocalDateTimeCustomFormatter;
import org.vdragun.tms.ui.rest.api.v1.converter.CourseToCourseModelConverter;
import org.vdragun.tms.ui.rest.api.v1.converter.StudentToStudentModelConverter;
import org.vdragun.tms.ui.rest.api.v1.converter.TeacherToTeacherModelConverter;
import org.vdragun.tms.ui.rest.api.v1.converter.TimetableToTimetableModelConverter;

/**
 * Contains configuration specific to RESTful resources
 * 
 * @author Vitaliy Dragun
 *
 */
@Configuration
public class WebRestConfig {
    
    @Bean
    public CourseToCourseModelConverter courseToCourseModelConverter() {
        return new CourseToCourseModelConverter();
    }

    @Bean
    public StudentToStudentModelConverter studentToStudentModelConverter(LocalDateCustomFormatter formatter) {
        return new StudentToStudentModelConverter(courseToCourseModelConverter(), formatter);
    }

    @Bean
    public TeacherToTeacherModelConverter teacherToTeacherModelConverter(LocalDateCustomFormatter formatter) {
        return new TeacherToTeacherModelConverter(courseToCourseModelConverter(), formatter);
    }

    @Bean
    public TimetableToTimetableModelConverter timetableToTimetableModelConverter(
            LocalDateTimeCustomFormatter formatter) {
        return new TimetableToTimetableModelConverter(courseToCourseModelConverter(), formatter);
    }

}
