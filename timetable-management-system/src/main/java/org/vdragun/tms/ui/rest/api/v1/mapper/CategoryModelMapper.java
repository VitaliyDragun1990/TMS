package org.vdragun.tms.ui.rest.api.v1.mapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.ui.rest.api.v1.model.CategoryModel;
import org.vdragun.tms.ui.rest.resource.v1.dictionary.DictionaryResource;

/**
 * @author Vitaliy Dragun
 *
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryModelMapper extends RepresentationModelMapper<Category, CategoryModel> {

    CategoryModelMapper INSTANCE = Mappers.getMapper(CategoryModelMapper.class);

    @AfterMapping
    default void addLinks(@MappingTarget CategoryModel model) {
        model.add(linkTo(DictionaryResource.class).slash("categories").withRel("categories"));
    }
}
