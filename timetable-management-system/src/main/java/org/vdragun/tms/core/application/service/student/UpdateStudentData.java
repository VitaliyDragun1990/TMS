package org.vdragun.tms.core.application.service.student;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.vdragun.tms.core.application.validation.PersonName;

/**
 * Contains data necessary to update existing student
 * 
 * @author Vitaliy Dragun
 *
 */
public class UpdateStudentData {

    /**
     * Identifier of the student to update
     */
    @NotNull
    @Positive
    private Integer studentId;

    @Positive
    private Integer groupId;

    @NotNull
    @PersonName
    private String firstName;

    @NotNull
    @PersonName
    private String lastName;

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