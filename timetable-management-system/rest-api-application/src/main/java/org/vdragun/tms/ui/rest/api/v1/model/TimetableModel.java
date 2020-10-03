package org.vdragun.tms.ui.rest.api.v1.model;

import java.util.Objects;

import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.vdragun.tms.core.domain.Timetable;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data transfer object with essential information for particular
 * {@link Timetable} domain entity.
 * 
 * @author Vitaliy Dragun
 *
 */
@Relation(collectionRelation = "timetables", itemRelation = "timetable")
@Schema(description = "DTO containing essential information about particular timetable")
public class TimetableModel extends RepresentationModel<TimetableModel> {

    @Schema(description = "Unique identifier of the timetable", example = "1")
    private Integer id;

    @Schema(
            description = "Date and time for which particular timetable was registered",
            pattern = "yyyy-MM-dd HH:mm",
            example = "2020-05-22 14:30")
    private String startTime;

    @Schema(description = "Duration of the timetable (in minutes)", example = "60")
    private Integer duration;

    @Schema(description = "Course timetable registered for", implementation = CourseModel.class)
    private CourseModel course;

    @Schema(description = "Unique identifier of the classroom timetable registered at", example = "1")
    private Integer classroomId;

    @Schema(description = "Capacity of the classroom for which timetable registered", example = "30")
    private Integer classroomCapacity;

    public TimetableModel() {
    }

    public TimetableModel(
            Integer id,
            String startTime,
            Integer duration,
            CourseModel course,
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

    public CourseModel getCourse() {
        return course;
    }

    public void setCourse(CourseModel course) {
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

    @Schema(hidden = true)
    @Override
    public Links getLinks() {
        return super.getLinks();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(id);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        TimetableModel other = (TimetableModel) obj;

        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "TimetableModel [id=" + id + ", startTime=" + startTime + ", duration=" + duration + ","
                + " course=" + course + ", classroomId=" + classroomId + ", classroomCapacity="
                + classroomCapacity + "]";
    }

}
