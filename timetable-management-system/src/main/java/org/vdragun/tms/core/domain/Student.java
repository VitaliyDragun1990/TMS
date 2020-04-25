package org.vdragun.tms.core.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents student instance in the application
 * 
 * @author Vitaliy Dragun
 *
 */
public class Student extends Person {

    private LocalDate enrollmentDate;
    private Group group;
    private List<Course> courses;

    public Student() {
        this(null, null);
    }

    public Student(String firstName, String lastName) {
        this(null, firstName, lastName);
    }


    public Student(Integer id, String firstName, String lastName) {
        this(id, firstName, lastName, LocalDate.now());
    }

    public Student(Integer id, String firstName, String lastName, LocalDate enrollmentDate) {
        super(id, firstName, lastName);
        this.enrollmentDate = enrollmentDate;
        courses = new ArrayList<>();
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public List<Course> getCourses() {
        return Collections.unmodifiableList(courses);
    }

    public void setCourses(List<Course> courses) {
        this.courses.addAll(courses);
    }

    @Override
    public String toString() {
        return "Student [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName +
                ", enrollmentDate=" + enrollmentDate + ", group=" + group + ", courses=" + courses + "]";
    }

}
