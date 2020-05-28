package org.vdragun.tms.core.application.service.student;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import org.vdragun.tms.core.application.validation.PersonName;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contains necessary data to create new student
 * 
 * @author Vitaliy Dragun
 *
 */
@Schema(description = "DTO (input model) containing information necessary to register new student")
public class CreateStudentData {

    @NotNull
    @PersonName
    @Schema(
            description = "Student first name",
            pattern = "^[A-Z]{1}[a-z]+$",
            minLength = 2,
            maxLength = 50,
            example = "John",
            required = true)
    private String firstName;

    @NotNull
    @PersonName
    @Schema(
            description = "Student last name",
            pattern = "^[A-Z]{1}[a-z]+$",
            minLength = 2,
            maxLength = 50,
            example = "Smith",
            required = true)
    private String lastName;

    @NotNull
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(
            description = "Date when particular teacher was enrolled (past or present only)",
            pattern = "yyyy-MM-dd",
            example = "2020-05-22",
            required = true)
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
