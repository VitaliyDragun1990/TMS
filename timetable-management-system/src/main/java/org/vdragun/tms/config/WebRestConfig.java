package org.vdragun.tms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.vdragun.tms.ui.common.util.Translator;
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
    public StudentToStudentModelConverter studentToStudentModelConverter(Translator translator) {
        return new StudentToStudentModelConverter(courseToCourseModelConverter(), translator);
    }

    @Bean
    public TeacherToTeacherModelConverter teacherToTeacherModelConverter(Translator translator) {
        return new TeacherToTeacherModelConverter(courseToCourseModelConverter(), translator);
    }

    @Bean
    public TimetableToTimetableModelConverter timetableToTimetableModelConverter(Translator translator) {
        return new TimetableToTimetableModelConverter(courseToCourseModelConverter(), translator);
    }

    @Bean
    public ModelConverter modelConverter(ConversionService conversionService) {
        return new DefaultModelConverter(conversionService);
    }

}
