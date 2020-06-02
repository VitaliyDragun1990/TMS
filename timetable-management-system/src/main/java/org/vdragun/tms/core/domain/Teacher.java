package org.vdragun.tms.core.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Represents teacher instance in the application
 * 
 * @author Vitaliy Dragun
 *
 */
@Entity
@Table(name = "teachers")
@AttributeOverride(
        name = "firstName",
        column = @Column(name = "t_first_name"))
@AttributeOverride(
        name = "lastName",
        column = @Column(name = "t_last_name"))
public class Teacher extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "teacherGen")
    @SequenceGenerator(name = "teacherGen", sequenceName = "teachers_teacher_id_seq", allocationSize = 1)
    @Column(name = "teacher_id")
    private Integer id;

    @Column(name = "date_hired")
    private LocalDate dateHired;

    @Column(name = "title")
    private Title title;

    @OneToMany(mappedBy = "teacher", cascade = { CascadeType.PERSIST })
    private List<Course> courses = new ArrayList<>();

    protected Teacher() {
        this(null, null, null);
    }

    public Teacher(String firstName, String lastName, Title title) {
        this(null, firstName, lastName, title);
    }

    public Teacher(String firstName, String lastName, Title title, LocalDate dateHired) {
        this(null, firstName, lastName, title, dateHired);
    }

    public Teacher(Integer id, String firstName, String lastName, Title title) {
        this(id, firstName, lastName, title, LocalDate.now());
    }

    public Teacher(Integer id, String firstName, String lastName, Title title, LocalDate dateHired) {
        super(firstName, lastName);
        this.id = id;
        this.dateHired = dateHired;
        this.title = title;
        courses = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public LocalDate getDateHired() {
        return dateHired;
    }

    public void setDateHired(LocalDate dateHired) {
        this.dateHired = dateHired;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public void addCourse(Course course) {
        courses.add(course);
        course.setTeacher(this);
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
        Teacher other = (Teacher) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public String toString() {
        return "Teacher [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName +
                ", dateHired=" + dateHired + ", title=" + title + "]";
    }

}
