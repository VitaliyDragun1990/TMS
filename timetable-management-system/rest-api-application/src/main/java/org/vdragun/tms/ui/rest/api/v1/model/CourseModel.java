package org.vdragun.tms.ui.rest.api.v1.model;

import java.util.Objects;

import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.vdragun.tms.core.domain.Course;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data transfer object with essential information for particular {@link Course}
 * domain entity.
 * 
 * @author Vitaliy Dragun
 *
 */
@Relation(collectionRelation = "courses", itemRelation = "course")
@Schema(description = "DTO containing essential information about particular course")
public class CourseModel extends RepresentationModel<CourseModel> {

    @Schema(description = "Unique identifier of the course", example = "1")
    private Integer id;

    @Schema(description = "Name of the course", example = "Advanced English")
    private String name;

    @Schema(description = "Description of the course", example = "Updated Advanced English course")
    private String description;

    @Schema(description = "Code of the category to which course belongs", example = "ENG")
    private String categoryCode;

    @Schema(description = "Unique identfier of the teacher who asigned to the course", example = "1")
    private Integer teacherId;

    @Schema(description = "Full name of the teacher who assigned to the course", example = "Anna Smith")
    private String teacherFullName;

    public CourseModel() {
    }

    public CourseModel(
            Integer id,
            String name,
            String description,
            String categoryCode,
            Integer teacherId,
            String teacherFullName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.categoryCode = categoryCode;
        this.teacherId = teacherId;
        this.teacherFullName = teacherFullName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherFullName() {
        return teacherFullName;
    }

    public void setTeacherFullName(String teacherFullName) {
        this.teacherFullName = teacherFullName;
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

        CourseModel other = (CourseModel) obj;

        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "CourseModel [id=" + id + ", name=" + name + ", description=" + description + ", categoryCode="
                + categoryCode + ", teacherId=" + teacherId + ", teacherFullName=" + teacherFullName + "]";
    }

}
