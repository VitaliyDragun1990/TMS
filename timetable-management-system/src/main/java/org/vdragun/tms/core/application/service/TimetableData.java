package org.vdragun.tms.core.application.service;

import java.time.LocalDateTime;

/**
 * Contains necessary data to create new timetable
 * 
 * @author Vitaliy Dragun
 *
 */
public class TimetableData {

    private LocalDateTime startTime;
    private int durationInMinutes;
    private Integer courseId;
    private Integer classroomId;
    private Integer teacherId;

    public TimetableData() {
    }

    public TimetableData(LocalDateTime startTime, int durationInMinutes, Integer courseId, Integer classroomId,
            Integer teacherId) {
        this.startTime = startTime;
        this.durationInMinutes = durationInMinutes;
        this.courseId = courseId;
        this.classroomId = classroomId;
        this.teacherId = teacherId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Integer classroomId) {
        this.classroomId = classroomId;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return "TimetableData [startTime=" + startTime + ", durationInMinutes=" + durationInMinutes + ", courseId="
                + courseId + ", classroomId=" + classroomId + ", teacherId=" + teacherId + "]";
    }

}
