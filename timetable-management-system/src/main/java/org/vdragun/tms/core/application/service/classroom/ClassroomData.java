package org.vdragun.tms.core.application.service.classroom;

import javax.validation.constraints.NotNull;

import org.vdragun.tms.core.application.validation.ClassroomCapacity;

/**
 * Contains necessary data to create new classroom
 * 
 * @author Vitaliy Dragun
 *
 */
public class ClassroomData {

    @NotNull
    @ClassroomCapacity
    private Integer capacity;

    public ClassroomData() {
    }

    public ClassroomData(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "ClassroomData [capacity=" + capacity + "]";
    }

}
