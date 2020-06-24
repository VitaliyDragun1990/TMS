package org.vdragun.tms.ui.rest.api.v1.mapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.vdragun.tms.util.Constants.Attribute.REQUEST_URI;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.ui.rest.api.v1.model.TeacherModel;
import org.vdragun.tms.ui.rest.resource.v1.teacher.TeacherResource;

/**
 * @author Vitaliy Dragun
 *
 */
@Mapper(uses = CourseModelMapper.class)
public interface TeacherModelMapper extends RepresentationModelMapper<Teacher, TeacherModel> {

    TeacherModelMapper INSTANCE = Mappers.getMapper(TeacherModelMapper.class);

    @Mapping(source = "dateHired", target = "dateHired", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "add", ignore = true)
    TeacherModel map(Teacher entity);

    @AfterMapping
    default void addLinks(@MappingTarget TeacherModel model, Teacher teacher) {
        model.add(
                linkTo(methodOn(TeacherResource.class).getTeacherById(model.getId(), REQUEST_URI)).withSelfRel(),
                linkTo(methodOn(TeacherResource.class).getAllTeachers(REQUEST_URI)).withRel("teachers"));
    }
}
