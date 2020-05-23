package org.vdragun.tms.core.application.service.timetable;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.vdragun.tms.core.application.validation.TimetableDuration;

/**
 * Contains necessary data to create new timetable
 * 
 * @author Vitaliy Dragun
 *
 */
public class CreateTimetableData {

    @NotNull
    @Future
    private LocalDateTime startTime;

    @NotNull
    @TimetableDuration
    private Integer durationInMinutes;

    @NotNull
    @Positive
    private Integer courseId;

    @NotNull
    @Positive
    private Integer classroomId;

    @NotNull
    @Positive
    private Integer teacherId;

    public CreateTimetableData() {
    }

    public CreateTimetableData(LocalDateTime startTime, Integer durationInMinutes, Integer courseId,
            Integer classroomId, Integer teacherId) {
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

    public Integer getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(Integer durationInMinutes) {
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
