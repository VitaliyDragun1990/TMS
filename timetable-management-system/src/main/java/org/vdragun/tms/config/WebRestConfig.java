package org.vdragun.tms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.vdragun.tms.ui.rest.api.v1.converter.CourseToCourseModelConverter;
import org.vdragun.tms.ui.rest.api.v1.converter.StudentToStudentModelConverter;
import org.vdragun.tms.ui.rest.api.v1.converter.TeacherToTeacherModelConverter;
import org.vdragun.tms.ui.rest.api.v1.converter.TimetableToTimetableModelConverter;
import org.vdragun.tms.ui.rest.api.v1.model.DefaultModelConverter;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.util.localizer.TemporalLocalizer;

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
    public StudentToStudentModelConverter studentToStudentModelConverter(TemporalLocalizer localizer) {
        return new StudentToStudentModelConverter(courseToCourseModelConverter(), localizer);
    }

    @Bean
    public TeacherToTeacherModelConverter teacherToTeacherModelConverter(TemporalLocalizer localizer) {
        return new TeacherToTeacherModelConverter(courseToCourseModelConverter(), localizer);
    }

    @Bean
    public TimetableToTimetableModelConverter timetableToTimetableModelConverter(TemporalLocalizer localizer) {
        return new TimetableToTimetableModelConverter(courseToCourseModelConverter(), localizer);
    }

    @Bean
    public ModelConverter modelConverter(ConversionService conversionService) {
        return new DefaultModelConverter(conversionService);
    }

}
