package org.vdragun.tms.ui.rest.api.v1.model;

import java.util.Objects;

import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.vdragun.tms.core.domain.Classroom;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data transfer object with essential information about particular
 * {@link Classroom} domain entity
 * 
 * @author Vitaliy Dragun
 *
 */
@Relation(collectionRelation = "classrooms", itemRelation = "classroom")
@Schema(description = "DTO containing essential information about particular classroom")
public class ClassroomModel extends RepresentationModel<ClassroomModel> {

    @Schema(description = "Unique identifier of the classroom", example = "1")
    private Integer id;

    @Schema(description = "Capacity of the classroom", example = "60")
    private int capacity;

    public ClassroomModel() {
    }

    public ClassroomModel(Integer id, int capacity) {
        this.id = id;
        this.capacity = capacity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        ClassroomModel other = (ClassroomModel) obj;

        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "ClassroomModel [id=" + id + ", capacity=" + capacity + "]";
    }

}
