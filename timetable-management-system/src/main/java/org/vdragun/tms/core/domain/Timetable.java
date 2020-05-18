package org.vdragun.tms.core.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Represents university timetable instance
 * 
 * @author Vitaliy Dragun
 *
 */
@Entity
@Table(name = "timetables")
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "timetableGen")
    @SequenceGenerator(name = "timetableGen", sequenceName = "timetables_timetable_id_seq", allocationSize = 1)
    @Column(name = "timetable_id")
    private Integer id;

    @Column(name = "start_date_time")
    private LocalDateTime startTime;

    @Column(name = "duration")
    private int durationInMinutes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    protected Timetable() {
        this(null, 0, null, null, null);
    }

    public Timetable(LocalDateTime startTime, int durationInMinutes, Course course, Classroom classRoom,
            Teacher teacher) {
        this(null, startTime, durationInMinutes, course, classRoom, teacher);
    }

    public Timetable(Integer id, LocalDateTime startTime, int durationInMinutes, Course course, Classroom classroom,
            Teacher teacher) {
        this.id = id;
        this.startTime = startTime;
        this.durationInMinutes = durationInMinutes;
        this.course = course;
        this.classroom = classroom;
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

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public int hashCode() {
        return 2021;
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
        return id != null && id.equals(other.getId());
    }

    @Override
    public String toString() {
        return "TimeTable [id=" + id + ", startTime=" + startTime + ", durationInMinutes=" + durationInMinutes
                + ", course=" + course + ", classRoom=" + classroom
                + ", teacher=" + teacher.getFirstName() + " " + teacher.getLastName()
                + "]";
    }

}
