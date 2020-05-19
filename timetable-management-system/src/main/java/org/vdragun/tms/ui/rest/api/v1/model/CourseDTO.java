package org.vdragun.tms.ui.rest.api.v1.model;

import org.vdragun.tms.core.domain.Course;

/**
 * Data transfer object with essential information for particular {@link Course}
 * domain entity.
 * 
 * @author Vitaliy Dragun
 *
 */
public class CourseDTO {

    private Integer id;
    private String name;
    private String description;
    private String categoryCode;
    private Integer teacherId;
    private String teacherFullName;

    public CourseDTO() {
    }

    public CourseDTO(
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
    public String toString() {
        return "CourseDTO [id=" + id + ", name=" + name + ", description=" + description + ", categoryCode="
                + categoryCode + ", teacherId=" + teacherId + ", teacherFullName=" + teacherFullName + "]";
    }

}
