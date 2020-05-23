package org.vdragun.tms.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Represents university course
 * 
 * @author Vitaliy Dragun
 *
 */
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "courseGen")
    @SequenceGenerator(name = "courseGen", sequenceName = "courses_course_id_seq", allocationSize = 1)
    @Column(name = "course_id")
    private Integer id;

    @Column(name = "course_name")
    private String name;

    @Column(name = "course_description")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    protected Course() {
    }

    public Course(Integer id) {
        this.id = id;
    }

    public Course(String name, Category category, Teacher teacher) {
        this(name, null, category, teacher);
    }

    public Course(Integer id, String name, Category category, Teacher teacher) {
        this(id, name, category, null, teacher);
    }

    public Course(String name, String description, Category category, Teacher teacher) {
        this(null, name, category, description, teacher);
    }

    public Course(Integer id, String name, Category category, String description, Teacher teacher) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.teacher = teacher;
        teacher.addCourse(this);
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
        return 2021;
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
        return id != null && id.equals(other.getId());
    }

    @Override
    public String toString() {
        return "Course [id=" + id + ", name=" + name + ", description=" + description + ", category=" + category + "]";
    }

}
