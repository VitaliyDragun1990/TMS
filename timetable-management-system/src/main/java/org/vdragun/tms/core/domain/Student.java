package org.vdragun.tms.core.domain;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Represents student instance in the application
 * 
 * @author Vitaliy Dragun
 *
 */
@Entity
@Table(name = "students")
@AttributeOverride(
        name = "firstName",
        column = @Column(name = "s_first_name"))
@AttributeOverride(
        name = "lastName",
        column = @Column(name = "s_last_name"))
public class Student extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "studentGen")
    @SequenceGenerator(name = "studentGen", sequenceName = "students_student_id_seq", allocationSize = 1)
    @Column(name = "student_id")
    private Integer id;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToMany
    @JoinTable(
            name = "student_courses",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<Course> courses = new HashSet<>();

    protected Student() {
    }

    public Student(String firstName, String lastName, LocalDate enrollmentDate) {
        this(null, firstName, lastName, enrollmentDate);
    }

    public Student(Integer id, String firstName, String lastName, LocalDate enrollmentDate) {
        super(firstName, lastName);
        this.id = id;
        this.enrollmentDate = enrollmentDate;
    }

    public Integer getId() {
        return id;
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

    public Set<Course> getCourses() {
        return Collections.unmodifiableSet(courses);
    }

    public void setCourses(Set<Course> courses) {
        courses.forEach(this::addCourse);
    }

    public void addCourse(Course course) {
        this.courses.add(course);
    }

    public void removeCourse(Course course) {
        this.courses.remove(course);
    }

    public void removeAllCourses() {
        this.courses.clear();
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
        Student other = (Student) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public String toString() {
        return "Student [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName +
                ", enrollmentDate=" + enrollmentDate + "]";
    }

}
