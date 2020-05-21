package org.vdragun.tms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.vdragun.tms.ui.common.converter.LocalDateCustomFormatter;
import org.vdragun.tms.ui.common.converter.LocalDateTimeCustomFormatter;
import org.vdragun.tms.ui.rest.api.v1.converter.CourseToCourseModelConverter;
import org.vdragun.tms.ui.rest.api.v1.converter.StudentToStudentModelConverter;
import org.vdragun.tms.ui.rest.api.v1.converter.TeacherToTeacherModelConverter;
import org.vdragun.tms.ui.rest.api.v1.converter.TimetableToTimetableModelConverter;
import org.vdragun.tms.ui.rest.api.v1.model.DefaultModelConverter;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;

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

    @Bean
    public ModelConverter modelConverter(ConversionService conversionService) {
        return new DefaultModelConverter(conversionService);
    }

}
