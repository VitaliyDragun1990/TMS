package org.vdragun.tms.core.application.service.timetable;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.vdragun.tms.core.application.validation.TimetableDuration;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contains necessary data to create new timetable
 * 
 * @author Vitaliy Dragun
 *
 */
@Schema(description = "DTO (input model) containing information necessary to register new timetable")
public class CreateTimetableData {

    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm[:ss]")
    @Schema(
            implementation = String.class,
            description = "Date and time for which particular timetable was registered (future only)",
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
            description = "Unique identifier of the course timetable registered for",
            minimum = "1",
            example = "1",
            required = true)
    private Integer courseId;

    @NotNull
    @Positive
    @Schema(
            description = "Unique identifier of the classroom timetable registered at",
            minimum = "1",
            example = "1",
            required = true)
    private Integer classroomId;

    @NotNull
    @Positive
    @Schema(
            description = "Unique identifier of the teacher assigned to the course",
            minimum = "1",
            example = "1",
            required = true)
    private Integer teacherId;

    public CreateTimetableData() {
    }

    public CreateTimetableData(LocalDateTime startTime, Integer durationInMinutes, Integer courseId,
            Integer classroomId, Integer teacherId) {
        this.startTime = startTime;
        this.duration = durationInMinutes;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer durationInMinutes) {
        this.duration = durationInMinutes;
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
        return "CreateTimetableData [startTime=" + startTime + ", durationInMinutes=" + duration + ", courseId="
                + courseId + ", classroomId=" + classroomId + ", teacherId=" + teacherId + "]";
    }

}
