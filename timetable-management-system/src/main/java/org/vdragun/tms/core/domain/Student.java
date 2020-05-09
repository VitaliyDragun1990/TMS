package org.vdragun.tms.core.domain;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
public class Student extends Person {

    private LocalDate enrollmentDate;
    private Group group;
    private Set<Course> courses;

    protected Student() {
        this(null, null);
    }

    public Student(String firstName, String lastName) {
        this(null, firstName, lastName);
    }

    public Student(String firstName, String lastName, LocalDate enrollmentDate) {
        this(null, firstName, lastName, enrollmentDate);
    }

    public Student(Integer id, String firstName, String lastName) {
        this(id, firstName, lastName, LocalDate.now());
    }

    public Student(Integer id, String firstName, String lastName, LocalDate enrollmentDate) {
        super(id, firstName, lastName);
        this.enrollmentDate = enrollmentDate;
        courses = new HashSet<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "studentGen")
    @SequenceGenerator(name = "studentGen", sequenceName = "students_student_id_seq", allocationSize = 1)
    @Column(name = "student_id")
    @Override
    public Integer getId() {
        return super.getId();
    }

    @Column(name = "s_first_name")
    @Override
    public String getFirstName() {
        return super.getFirstName();
    }

    @Column(name = "s_last_name")
    @Override
    public String getLastName() {
        return super.getLastName();
    }

    @Column(name = "enrollment_date")
    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "student_courses",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    public Set<Course> getCourses() {
        return Collections.unmodifiableSet(courses);
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
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
    public String toString() {
        return "Student [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName +
                ", enrollmentDate=" + enrollmentDate + ", group=" + group + ", courses=" + courses + "]";
    }

}
