package org.vdragun.tms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.rest.api.v1.mapper.CourseModelMapper;
import org.vdragun.tms.ui.rest.api.v1.mapper.ModelAssembler;
import org.vdragun.tms.ui.rest.api.v1.mapper.StudentModelMapper;
import org.vdragun.tms.ui.rest.api.v1.mapper.TeacherModelMapper;
import org.vdragun.tms.ui.rest.api.v1.mapper.TimetableModelMapper;
import org.vdragun.tms.ui.rest.api.v1.model.CourseModel;
import org.vdragun.tms.ui.rest.api.v1.model.StudentModel;
import org.vdragun.tms.ui.rest.api.v1.model.TeacherModel;
import org.vdragun.tms.ui.rest.api.v1.model.TimetableModel;
import org.vdragun.tms.ui.rest.resource.v1.course.CourseResource;
import org.vdragun.tms.ui.rest.resource.v1.student.StudentResource;
import org.vdragun.tms.ui.rest.resource.v1.teacher.TeacherResource;
import org.vdragun.tms.ui.rest.resource.v1.timetable.TimetableResource;

@Configuration
public class ModelAssemblerConfig {

    @Bean
    public ModelAssembler<Course, CourseModel, CourseResource> courseModelAssembler() {
        return new ModelAssembler<>(CourseResource.class, CourseModel.class, CourseModelMapper.INSTANCE);
    }

    @Bean
    public ModelAssembler<Student, StudentModel, StudentResource> studentModelAssembler() {
        return new ModelAssembler<>(StudentResource.class, StudentModel.class, StudentModelMapper.INSTANCE);
    }

    @Bean
    public ModelAssembler<Teacher, TeacherModel, TeacherResource> teacherModelAssembler() {
        return new ModelAssembler<>(TeacherResource.class, TeacherModel.class, TeacherModelMapper.INSTANCE);
    }

    @Bean
    public ModelAssembler<Timetable, TimetableModel, TimetableResource> timetableModelAssembler() {
        return new ModelAssembler<>(TimetableResource.class, TimetableModel.class, TimetableModelMapper.INSTANCE);
    }
}
