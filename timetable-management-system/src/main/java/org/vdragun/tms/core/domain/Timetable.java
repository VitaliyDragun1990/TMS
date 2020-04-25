package org.vdragun.tms.core.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents university timetable instance
 * 
 * @author Vitaliy Dragun
 *
 */
public class Timetable {

    private Integer id;
    private LocalDateTime startTime;
    private int durationInMinutes;
    private Course course;
    private ClassRoom classRoom;
    private Teacher teacher;

    public Timetable() {
        this(null, 0, null, null, null);
    }

    public Timetable(LocalDateTime startTime, int durationInMinutes, Course course, ClassRoom classRoom,
            Teacher teacher) {
        this(null, startTime, durationInMinutes, course, classRoom, teacher);
    }

    public Timetable(Integer id, LocalDateTime startTime, int durationInMinutes, Course course, ClassRoom classRoom,
            Teacher teacher) {
        this.id = id;
        this.startTime = startTime;
        this.durationInMinutes = durationInMinutes;
        this.course = course;
        this.classRoom = classRoom;
        this.teacher = teacher;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public ClassRoom getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(ClassRoom classRoom) {
        this.classRoom = classRoom;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Timetable other = (Timetable) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "TimeTable [id=" + id + ", startTime=" + startTime + ", durationInMinutes=" + durationInMinutes
                + ", course=" + course + ", classRoom=" + classRoom
                + ", teacher=" + teacher.getFirstName() + " " + teacher.getLastName()
                + "]";
    }

}
