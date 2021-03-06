package org.vdragun.tms.ui.rest.api.v1.mapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.rest.api.v1.model.StudentModel;
import org.vdragun.tms.ui.rest.resource.v1.student.StudentResource;

/**
 * @author Vitaliy Dragun
 *
 */
@Mapper(
        uses = CourseModelMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentModelMapper extends RepresentationModelMapper<Student, StudentModel> {

    StudentModelMapper INSTANCE = Mappers.getMapper(StudentModelMapper.class);

    @Mapping(source = "enrollmentDate", target = "enrollmentDate", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "group.name", target = "group")
    StudentModel map(Student entity);

    @AfterMapping
    default void addLinks(@MappingTarget StudentModel model, Student student) {
        model.add(
                linkTo(methodOn(StudentResource.class).getStudentById(model.getId())).withSelfRel(),
                linkTo(StudentResource.class).withRel("students"));
    }
}
