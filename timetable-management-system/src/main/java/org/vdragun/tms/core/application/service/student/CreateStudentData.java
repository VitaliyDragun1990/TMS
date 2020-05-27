package org.vdragun.tms.core.application.service.student;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import org.vdragun.tms.core.application.validation.PersonName;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Contains necessary data to create new student
 * 
 * @author Vitaliy Dragun
 *
 */
public class CreateStudentData {

    @NotNull
    @PersonName
    private String firstName;

    @NotNull
    @PersonName
    private String lastName;

    @NotNull
    @PastOrPresent
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate enrollmentDate;

    public CreateStudentData() {
    }

    public CreateStudentData(String firstName, String lastName, LocalDate enrollmentDate) {
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
