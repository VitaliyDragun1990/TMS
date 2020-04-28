package org.vdragun.tms.core.domain;

import java.util.Objects;

/**
 * Represents classroom where university course is held in
 * 
 * @author Vitaliy Dragun
 *
 */
public class Classroom {

    private Integer id;
    private int capacity;

    public Classroom() {
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
        Classroom other = (Classroom) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "ClassRoom [id=" + id + ", capacity=" + capacity + "]";
    }

}
