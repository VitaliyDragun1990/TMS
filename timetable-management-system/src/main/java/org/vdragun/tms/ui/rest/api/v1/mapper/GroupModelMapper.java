package org.vdragun.tms.ui.rest.api.v1.mapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.ui.rest.api.v1.model.GroupModel;
import org.vdragun.tms.ui.rest.resource.v1.dictionary.DictionaryResource;

/**
 * @author Vitaliy Dragun
 *
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GroupModelMapper extends RepresentationModelMapper<Group, GroupModel> {

    GroupModelMapper INSTANCE = Mappers.getMapper(GroupModelMapper.class);

    @AfterMapping
    default void addLinks(@MappingTarget GroupModel model) {
        model.add(linkTo(DictionaryResource.class).slash("groups").withRel("groups"));
    }
}
