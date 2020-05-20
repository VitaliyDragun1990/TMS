package org.vdragun.tms.ui.rest.api.v1.model;

import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.vdragun.tms.core.domain.Course;

/**
 * Data transfer object with essential information for particular {@link Course}
 * domain entity.
 * 
 * @author Vitaliy Dragun
 *
 */
@Relation(collectionRelation = "courses", itemRelation = "course")
public class CourseModel extends RepresentationModel<CourseModel> {

    private Integer id;
    private String name;
    private String description;
    private String categoryCode;
    private Integer teacherId;
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

    public String getTeacherFullName() {
        return teacherFullName;
    }

    public void setTeacherFullName(String teacherFullName) {
        this.teacherFullName = teacherFullName;
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
        CourseModel other = (CourseModel) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "CourseDTO [id=" + id + ", name=" + name + ", description=" + description + ", categoryCode="
                + categoryCode + ", teacherId=" + teacherId + ", teacherFullName=" + teacherFullName + "]";
    }

}
