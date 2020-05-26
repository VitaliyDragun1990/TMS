package org.vdragun.tms.core.application.service.timetable;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.vdragun.tms.core.application.validation.TimetableDuration;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contains data necessary to update existing timetable
 * 
 * @author Vitaliy Dragun
 *
 */
@Schema(description = "DTO (input model) containing information necessary to update existing timetable")
public class UpdateTimetableData {

    @NotNull
    @Positive
    @Schema(
            description = "Unique identifier of the timetable to update",
            minimum = "1",
            required = true)
    private Integer timetableId;

    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm[:ss]")
    @Schema(
            implementation = String.class,
            description = "Date and time for which particular timetable should be registered (future only)",
            pattern = "yyyy-MM-dd HH:mm",
            example = "2020-05-22 14:30",
            required = true)
    private LocalDateTime startTime;

    @NotNull
    @TimetableDuration
    @Schema(
            description = "Duration of the timetable (in minutes)",
            minimum = "30",
            maximum = "90",
            example = "60",
            required = true)
    private Integer duration;

    @NotNull
    @Positive
    @Schema(
            description = "Unique identifier of the classroom timetable registered at",
            minimum = "1",
            example = "1",
            required = true)
    private Integer classroomId;

    public UpdateTimetableData() {
    }

    public UpdateTimetableData(Integer timetableId, LocalDateTime startTime, Integer durationInMinutes,
            Integer classroomId) {
        this.timetableId = timetableId;
        this.startTime = startTime;
        this.duration = durationInMinutes;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer durationInMinutes) {
        this.duration = durationInMinutes;
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
                + duration + ", classroomId=" + classroomId + "]";
    }

}
