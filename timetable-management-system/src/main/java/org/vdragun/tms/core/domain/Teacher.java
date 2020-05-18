package org.vdragun.tms.core.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
public class Teacher extends Person {

    private LocalDate dateHired;
    private Title title;
    private List<Course> courses;

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
        super(id, firstName, lastName);
        this.dateHired = dateHired;
        this.title = title;
        courses = new ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "teacherGen")
    @SequenceGenerator(name = "teacherGen", sequenceName = "teachers_teacher_id_seq", allocationSize = 1)
    @Column(name = "teacher_id")
    @Override
    public Integer getId() {
        return super.getId();
    }

    @Column(name = "t_first_name")
    @Override
    public String getFirstName() {
        return super.getFirstName();
    }

    @Column(name = "t_last_name")
    @Override
    public String getLastName() {
        return super.getLastName();
    }

    @Column(name = "date_hired")
    public LocalDate getDateHired() {
        return dateHired;
    }

    public void setDateHired(LocalDate dateHired) {
        this.dateHired = dateHired;
    }

    @Column(name = "title")
    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    @OneToMany(mappedBy = "teacher", cascade = { CascadeType.PERSIST })
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
    public String toString() {
        return "Teacher [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName +
                ", dateHired=" + dateHired + ", title=" + title + "]";
    }

}
