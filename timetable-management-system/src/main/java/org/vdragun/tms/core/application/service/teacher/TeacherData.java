package org.vdragun.tms.core.application.service.teacher;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import org.vdragun.tms.core.application.validation.PersonName;
import org.vdragun.tms.core.domain.Title;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contains necessary data to create new teacher
 * 
 * @author Vitaliy Dragun
 *
 */
@Schema(description = "DTO (input model) containing necessary information to register new teacher")
public class TeacherData {

    @NotNull
    @PersonName
    @Schema(
            description = "Teacher first name",
            pattern = "^[A-Z]{1}[a-z]+$",
            minLength = 2,
            maxLength = 50,
            example = "John",
            required = true)
    private String firstName;

    @NotNull
    @PersonName
    @Schema(
            description = "Teacher last name",
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
            description = "Date when particular teacher was hired (past or present only)",
            pattern = "yyyy-MM-dd",
            example = "2020-05-22",
            required = true)
    private LocalDate dateHired;

    @NotNull
    @Schema(
            description = "Teacher title",
            example = "INSTRUCTOR",
            allowableValues = { "PROFESSOR", "ASSOCIATE_PROFESSOR", "INSTRUCTOR" },
            required = true)
    private Title title;

    public TeacherData() {
    }

    public TeacherData(
            String firstName,
            String lastName,
            LocalDate dateHired,
            Title title) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateHired = dateHired;
        this.title = title;
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

    public LocalDate getDateHired() {
        return dateHired;
    }

    public void setDateHired(LocalDate dateHired) {
        this.dateHired = dateHired;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "TeacherData [firstName=" + firstName + ", lastName=" + lastName + ", dateHired=" + dateHired
                + ", title=" + title + "]";
    }

}
