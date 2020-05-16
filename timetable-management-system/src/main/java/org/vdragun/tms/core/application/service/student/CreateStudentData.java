package org.vdragun.tms.core.application.service.student;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

import org.vdragun.tms.core.application.validation.LatinCharacters;

/**
 * Contains necessary data to create new student
 * 
 * @author Vitaliy Dragun
 *
 */
public class CreateStudentData {

    @NotBlank
    @LatinCharacters
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank
    @LatinCharacters
    @Size(min = 2, max = 50)
    private String lastName;

    @NotNull
    @PastOrPresent
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
