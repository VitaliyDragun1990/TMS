package org.vdragun.tms.ui.rest.api.v1.model;

import java.util.Objects;

import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.vdragun.tms.core.domain.Group;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data transfer object with essential information about particular
 * {@link Group} domain entity
 * 
 * @author Vitaliy Dragun
 *
 */
@Relation(collectionRelation = "groups", itemRelation = "group")
@Schema(description = "DTO containing essential information about particular group")
public class GroupModel extends RepresentationModel<GroupModel> {

    @Schema(description = "Unique identifier of the group", example = "1")
    private Integer id;

    @Schema(description = "Unique group name", example = "ps-25")
    private String name;

    public GroupModel() {
    }

    public GroupModel(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Schema(hidden = true)
    @Override
    public Links getLinks() {
        return super.getLinks();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        GroupModel other = (GroupModel) obj;

        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "GroupModel [id=" + id + ", name=" + name + "]";
    }

}
