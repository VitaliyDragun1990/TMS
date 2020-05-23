package org.vdragun.tms.core.application.service.group;

import javax.validation.constraints.NotNull;

import org.vdragun.tms.core.application.validation.GroupName;

/**
 * Contains necessary data to create new group
 * 
 * @author Vitaliy Dragun
 *
 */
public class GroupData {

    @NotNull
    @GroupName
    private String name;

    public GroupData() {
    }

    public GroupData(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "GroupData [name=" + name + "]";
    }

}
