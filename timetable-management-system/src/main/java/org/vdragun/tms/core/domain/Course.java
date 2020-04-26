package org.vdragun.tms.core.domain;

import java.util.Objects;

/**
 * Represents university course
 * 
 * @author Vitaliy Dragun
 *
 */
public class Course {

    private Integer id;
    private String name;
    private String description;
    private Category category;
    private Teacher teacher;

    public Course() {
    }

    public Course(String name, Category category, Teacher teacher) {
        this(null, name, category, teacher);
    }

    public Course(Integer id, String name, Category category, Teacher teacher) {
        this(id, name, category, null, teacher);
    }

    public Course(Integer id, String name, Category category, String description, Teacher teacher) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.teacher = teacher;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
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
        Course other = (Course) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "Course [id=" + id + ", name=" + name + ", description=" + description + ", category=" + category + "]";
    }

}
