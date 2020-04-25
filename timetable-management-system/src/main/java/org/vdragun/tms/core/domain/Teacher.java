package org.vdragun.tms.core.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents teacher instance in the application
 * 
 * @author Vitaliy Dragun
 *
 */
public class Teacher extends Person {

    private LocalDate dateHired;
    private Title title;
    private List<Course> courses;

    public Teacher() {
        this(null, null, null);
    }

    public Teacher(String firstName, String lastName, Title title) {
        this(null, firstName, lastName, title);
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
        return Collections.unmodifiableList(courses);
    }

    public void setCourses(List<Course> courses) {
        this.courses.addAll(courses);
    }

    @Override
    public String toString() {
        return "Teacher [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName +
                ", dateHired=" + dateHired + ", title=" + title + ", courses=" + courses + "]";
    }

}
