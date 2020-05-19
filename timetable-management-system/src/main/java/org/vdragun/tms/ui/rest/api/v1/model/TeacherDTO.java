package org.vdragun.tms.ui.rest.api.v1.model;

import java.util.List;

import org.vdragun.tms.core.domain.Teacher;

/**
 * Data transfer object with essential information for particular
 * {@link Teacher} domain entity.
 * 
 * @author Vitaliy Dragun
 *
 */
public class TeacherDTO {

    private Integer id;
    private String firstName;
    private String lastName;
    private String title;
    private String dateHired;
    private List<CourseDTO> courses;

    public TeacherDTO() {
    }

    public TeacherDTO(
            Integer id,
            String firstName,
            String lastName,
            String title,
            String dateHired,
            List<CourseDTO> courses) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.dateHired = dateHired;
        this.courses = courses;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateHired() {
        return dateHired;
    }

    public void setDateHired(String dateHired) {
        this.dateHired = dateHired;
    }

    public List<CourseDTO> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseDTO> courses) {
        this.courses = courses;
    }

    @Override
    public String toString() {
        return "TeacherDTO [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", title=" + title
                + ", dateHired=" + dateHired + ", courses=" + courses + "]";
    }

}
