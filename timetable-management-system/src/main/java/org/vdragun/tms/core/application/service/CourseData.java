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

    public CourseData() {
    }

    public CourseData(String name, String description) {
        this.name = name;
        this.description = description;
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

    @Override
    public String toString() {
        return "CourseData [name=" + name + ", description=" + description + "]";
    }

}
