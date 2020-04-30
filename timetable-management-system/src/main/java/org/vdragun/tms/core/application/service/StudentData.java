package org.vdragun.tms.core.application.service;

import java.time.LocalDate;

/**
 * Contains necessary data to create new student
 * 
 * @author Vitaliy Dragun
 *
 */
public class StudentData {

    private String firstName;
    private String lastName;
    private LocalDate enrollmentDate;

    public StudentData() {
    }

    public StudentData(String firstName, String lastName, LocalDate enrollmentDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.enrollmentDate = enrollmentDate;
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

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    @Override
    public String toString() {
        return "StudentData [firstName=" + firstName + ", lastName=" + lastName + ", enrollmentDate=" + enrollmentDate
                + "]";
    }

}
