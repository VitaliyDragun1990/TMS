package org.vdragun.tms.ui.rest.resource.v1.dictionary;

import static org.springframework.http.HttpStatus.OK;
import static org.vdragun.tms.util.WebUtil.getFullRequestUri;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vdragun.tms.core.application.service.category.CategoryService;
import org.vdragun.tms.core.application.service.group.GroupService;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.ui.rest.api.v1.mapper.CategoryModelMapper;
import org.vdragun.tms.ui.rest.api.v1.mapper.GroupModelMapper;
import org.vdragun.tms.ui.rest.api.v1.model.CategoryModel;
import org.vdragun.tms.ui.rest.api.v1.model.GroupModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller that processes dictionary resources related requests
 * 
 * @author Vitaliy Dragun
 *
 */
@RestController
@RequestMapping(
        path = DictionaryResource.BASE_URL,
        produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
@Tag(name = "dictionary", description = "the Dictionary API")
public class DictionaryResource {

    private static final Logger LOG = LoggerFactory.getLogger(DictionaryResource.class);

    public static final String BASE_URL = "/api/v1/dictionary";

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private GroupService groupService;

    @SecurityRequirements
    @Operation(
            summary = "Find all categories available",
            tags = { "dictionary" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CategoryModel.class))),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CategoryModel.class)))
            })
    @GetMapping("/categories")
    @ResponseStatus(OK)
    public CollectionModel<CategoryModel> getAllCategories() {
        LOG.trace("Received GET request to retrieve all categories, URI={}", getFullRequestUri());

        List<Category> categories = categoryService.findAllCategories();
        Link selfLink = new Link(getFullRequestUri());

        return new CollectionModel<>(CategoryModelMapper.INSTANCE.map(categories), selfLink);
    }

    @SecurityRequirements
    @Operation(
            summary = "Find all groups available",
            tags = { "dictionary" })
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = {
                    @Content(
                            mediaType = MediaTypes.HAL_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = GroupModel.class))),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = GroupModel.class)))
            })
    @GetMapping("/groups")
    @ResponseStatus(OK)
    public CollectionModel<GroupModel> getAllGroups() {
        LOG.trace("Received GET request to retrieve all groups, URI={}", getFullRequestUri());

        List<Group> groups = groupService.findAllGroups();
        Link selfLink = new Link(getFullRequestUri());

        return new CollectionModel<>(GroupModelMapper.INSTANCE.map(groups), selfLink);
    }

}
