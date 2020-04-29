package org.vdragun.tms.core.application.service;

/**
 * Contains necessary data to create new course
 * 
 * @author Vitaliy Dragun
 *
 */
public class CourseData {

    private String name;
    private String description;
    private Integer categoryId;
    private Integer teacherId;

    public CourseData() {
    }

    public CourseData(String name, String description, Integer categoryId, Integer teacherId) {
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.teacherId = teacherId;
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

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return "CourseData [name=" + name + ", description=" + description + ", categoryId=" + categoryId
                + ", teacherId=" + teacherId + "]";
    }

}
