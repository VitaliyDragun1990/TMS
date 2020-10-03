package org.vdragun.tms.core.application.service.student;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.vdragun.tms.core.application.validation.PersonName;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contains data necessary to update existing student
 * 
 * @author Vitaliy Dragun
 *
 */
@Schema(description = "DTO (input model) containing information necessary to update existing student")
public class UpdateStudentData {

    /**
     * Identifier of the student to update
     */
    @NotNull
    @Positive
    @Schema(
            description = "Unique student identifier",
            example = "1",
            required = true,
            minimum = "1")
    private Integer studentId;

    @Positive
    @Schema(
            description = "Unique group identifier student assigned to",
            example = "1",
            required = false,
            minimum = "1")
    private Integer groupId;

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

    @ArraySchema(
            schema = @Schema(
                    description = "Unique ciurse identifier",
                    example = "1",
                    minimum = "1",
                    required = false))
    private List<@NotNull @Positive Integer> courseIds;

    public UpdateStudentData() {
        courseIds = new ArrayList<>();
    }

    public UpdateStudentData(Integer studentId, Integer groupId, String firstName, String lastName,
            List<Integer> courseIds) {
        this.studentId = studentId;
        this.groupId = groupId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.courseIds = courseIds;
    }
    
    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
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

    public List<Integer> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<Integer> courseIds) {
        this.courseIds = courseIds;
    }

    @Override
    public String toString() {
        return "UpdateStudentData [studentId=" + studentId + ", groupId=" + groupId + ", firstName=" + firstName
                + ", lastName=" + lastName + ", courseIds=" + courseIds + "]";
    }

}
