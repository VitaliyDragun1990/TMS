package org.vdragun.tms.ui.rest.api.v1.model;

import java.util.List;

import org.vdragun.tms.core.domain.Student;

/**
 * Data transfer object with essential information for particular
 * {@link Student} domain entity.
 * 
 * @author Vitaliy Dragun
 *
 */
public class StudentDTO {

    private Integer id;
    private String firstName;
    private String lastName;
    private String group;
    private String enrollmentDate;
    private List<CourseDTO> courses;

    public StudentDTO() {
    }

    public StudentDTO(
            Integer id,
            String firstName,
            String lastName,
            String group,
            String enrollmentDate,
            List<CourseDTO> courses) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.group = group;
        this.enrollmentDate = enrollmentDate;
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public List<CourseDTO> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseDTO> courses) {
        this.courses = courses;
    }

    @Override
    public String toString() {
        return "StudentDTO [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", group=" + group
                + ", enrollmentDate=" + enrollmentDate + ", courses=" + courses + "]";
    }

}
