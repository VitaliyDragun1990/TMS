package org.vdragun.tms.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Represents classroom where university course is held in
 * 
 * @author Vitaliy Dragun
 *
 */
@Entity
@Table(name = "classrooms")
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "classroomGen")
    @SequenceGenerator(name = "classroomGen", sequenceName = "classrooms_classroom_id_seq", allocationSize = 1)
    @Column(name = "classroom_id")
    private Integer id;

    @Column(name = "capacity")
    private int capacity;

    protected Classroom() {
        this(0);
    }

    public Classroom(int capacity) {
        this(null, capacity);
    }

    public Classroom(Integer id, int capacity) {
        this.id = id;
        this.capacity = capacity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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
        Classroom other = (Classroom) obj;
        return id != null && id.equals(other.getId());
    }

    @Override
    public String toString() {
        return "Classroom [id=" + id + ", capacity=" + capacity + "]";
    }

}
