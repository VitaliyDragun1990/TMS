package org.vdragun.tms.ui.rest.api.v1.model;

import org.vdragun.tms.core.domain.Timetable;

/**
 * Data transfer object with essential information for particular
 * {@link Timetable} domain entity.
 * 
 * @author Vitaliy Dragun
 *
 */
public class TimetableDTO {

    private Integer id;
    private String startTime;
    private Integer duration;
    private CourseDTO course;
    private Integer classroomId;
    private Integer classroomCapacity;

    public TimetableDTO() {
    }

    public TimetableDTO(
            Integer id,
            String startTime,
            Integer duration,
            CourseDTO course,
            Integer classroomId,
            Integer classroomCapacity) {
        this.id = id;
        this.startTime = startTime;
        this.duration = duration;
        this.course = course;
        this.classroomId = classroomId;
        this.classroomCapacity = classroomCapacity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public Integer getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Integer classroomId) {
        this.classroomId = classroomId;
    }

    public Integer getClassroomCapacity() {
        return classroomCapacity;
    }

    public void setClassroomCapacity(Integer classroomCapacity) {
        this.classroomCapacity = classroomCapacity;
    }

    @Override
    public String toString() {
        return "TimetableDTO [id=" + id + ", startTime=" + startTime + ", duration=" + duration + ", course=" + course
                + ", classroomId=" + classroomId + ", classroomCapacity=" + classroomCapacity + "]";
    }

}
