package org.vdragun.tms.core.application.service;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * Contains data necessary to update existing timetable
 * 
 * @author Vitaliy Dragun
 *
 */
public class UpdateTimetableData {

    @NotNull
    @Positive
    private Integer timetableId;

    @NotNull
    @Future
    private LocalDateTime startTime;

    @NotNull
    @Min(value = 30)
    @Max(value = 90)
    private int durationInMinutes;

    @NotNull
    @Positive
    private Integer classroomId;

    public UpdateTimetableData() {
    }

    public UpdateTimetableData(Integer timetableId, LocalDateTime startTime, int durationInMinutes,
            Integer classroomId) {
        this.timetableId = timetableId;
        this.startTime = startTime;
        this.durationInMinutes = durationInMinutes;
        this.classroomId = classroomId;
    }

    public Integer getTimetableId() {
        return timetableId;
    }

    public void setTimetableId(Integer timetableId) {
        this.timetableId = timetableId;
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

    public Integer getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Integer classroomId) {
        this.classroomId = classroomId;
    }

    @Override
    public String toString() {
        return "UpdateTimetableData [timetableId=" + timetableId + ", startTime=" + startTime + ", durationInMinutes="
                + durationInMinutes + ", classroomId=" + classroomId + "]";
    }

}
