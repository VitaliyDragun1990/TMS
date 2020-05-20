package org.vdragun.tms.ui.rest.api.v1.model;

import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.vdragun.tms.core.domain.Timetable;

/**
 * Data transfer object with essential information for particular
 * {@link Timetable} domain entity.
 * 
 * @author Vitaliy Dragun
 *
 */
@Relation(collectionRelation = "timetables", itemRelation = "timetable")
public class TimetableModel extends RepresentationModel<TimetableModel> {

    private Integer id;
    private String startTime;
    private Integer duration;
    private CourseModel course;
    private Integer classroomId;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(id);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        TimetableModel other = (TimetableModel) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "TimetableDTO [id=" + id + ", startTime=" + startTime + ", duration=" + duration + ", course=" + course
                + ", classroomId=" + classroomId + ", classroomCapacity=" + classroomCapacity + "]";
    }

}
