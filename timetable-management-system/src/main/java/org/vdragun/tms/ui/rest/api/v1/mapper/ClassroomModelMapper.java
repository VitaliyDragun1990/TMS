package org.vdragun.tms.ui.rest.api.v1.mapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.ui.rest.api.v1.model.ClassroomModel;
import org.vdragun.tms.ui.rest.resource.v1.dictionary.DictionaryResource;

/**
 * @author Vitaliy Dragun
 *
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClassroomModelMapper extends RepresentationModelMapper<Classroom, ClassroomModel> {

    ClassroomModelMapper INSTANCE = Mappers.getMapper(ClassroomModelMapper.class);

    @AfterMapping
    default void addLinks(@MappingTarget ClassroomModel model) {
        model.add(linkTo(DictionaryResource.class).slash("classrooms").withRel("classrooms"));
    }
}
