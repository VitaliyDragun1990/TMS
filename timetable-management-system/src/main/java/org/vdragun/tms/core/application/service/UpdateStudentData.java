package org.vdragun.tms.core.application.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains data necessary to update existing student
 * 
 * @author Vitaliy Dragun
 *
 */
public class UpdateStudentData {

    private Integer studentId;
    private Integer groupId;
    private List<Integer> courseIds;

    public UpdateStudentData() {
        courseIds = new ArrayList<>();
    }

    public UpdateStudentData(Integer studentId, Integer groupId, List<Integer> courseIds) {
        this.studentId = studentId;
        this.groupId = groupId;
        this.courseIds = courseIds;
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
