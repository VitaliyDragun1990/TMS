package org.vdragun.tms.core.application.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * Contains data necessary to update existing student
 * 
 * @author Vitaliy Dragun
 *
 */
public class UpdateStudentData {

    @NotNull
    @Positive
    private Integer studentId;

    @Positive
    private Integer groupId;

    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
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
        return "UpdateStudentData [studentId=" + studentId + ", groupId=" + groupId + ", courseIds=" + courseIds + "]";
    }

}
