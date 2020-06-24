package org.vdragun.tms.ui.rest.api.v1.mapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.ui.rest.api.v1.model.TeacherModel;
import org.vdragun.tms.ui.rest.resource.v1.teacher.TeacherResource;

/**
 * @author Vitaliy Dragun
 *
 */
@Mapper(
        uses = CourseModelMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeacherModelMapper extends RepresentationModelMapper<Teacher, TeacherModel> {

    TeacherModelMapper INSTANCE = Mappers.getMapper(TeacherModelMapper.class);

    @Mapping(source = "dateHired", target = "dateHired", dateFormat = "yyyy-MM-dd")
    TeacherModel map(Teacher entity);

    @AfterMapping
    default void addLinks(@MappingTarget TeacherModel model, Teacher teacher) {
        model.add(
                linkTo(methodOn(TeacherResource.class).getTeacherById(model.getId())).withSelfRel(),
                linkTo(methodOn(TeacherResource.class).getAllTeachers()).withRel("teachers"));
    }
}
