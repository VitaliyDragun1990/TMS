package org.vdragun.tms.ui.rest.api.v1.model;

import java.util.List;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.vdragun.tms.core.domain.Student;

/**
 * Data transfer object with essential information for particular
 * {@link Student} domain entity.
 * 
 * @author Vitaliy Dragun
 *
 */
@Relation(collectionRelation = "students", itemRelation = "student")
public class StudentModel extends RepresentationModel<StudentModel> {

    private Integer id;
    private String firstName;
    private String lastName;
    private String group;
    private String enrollmentDate;
    private List<CourseModel> courses;

    public StudentModel() {
    }

    public StudentModel(
            Integer id,
            String firstName,
            String lastName,
            String group,
            String enrollmentDate,
            List<CourseModel> courses) {
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

    public List<CourseModel> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseModel> courses) {
        this.courses = courses;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(id);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        StudentModel other = (StudentModel) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "StudentDTO [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", group=" + group
                + ", enrollmentDate=" + enrollmentDate + ", courses=" + courses + "]";
    }

}
