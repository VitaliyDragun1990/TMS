package org.vdragun.tms.ui.rest.api.v1.model;

import java.util.Objects;

import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.vdragun.tms.core.domain.Category;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data transfer object with essential information about particular
 * {@link Category} domain entity
 * 
 * @author Vitaliy Dragun
 *
 */
@Relation(collectionRelation = "categories", itemRelation = "category")
@Schema(description = "DTO containing essential information about particular category")
public class CategoryModel extends RepresentationModel<CategoryModel> {

    @Schema(description = "Unique identifier of the category", example = "1")
    private Integer id;

    @Schema(description = "Unique category code", example = "ENG")
    private String code;

    @Schema(description = "Optional category description", nullable = true)
    private String description;

    public CategoryModel() {
    }

    public CategoryModel(Integer id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Schema(hidden = true)
    @Override
    public Links getLinks() {
        return super.getLinks();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(id);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        CategoryModel other = (CategoryModel) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "CategoryModel [id=" + id + ", code=" + code + ", description=" + description + "]";
    }

}
