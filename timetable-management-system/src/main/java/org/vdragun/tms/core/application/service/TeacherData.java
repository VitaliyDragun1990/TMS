package org.vdragun.tms.core.application.service;

import java.time.LocalDate;

import org.vdragun.tms.core.domain.Title;

/**
 * Contains necessary data to create new teacher
 * 
 * @author Vitaliy Dragun
 *
 */
public class TeacherData {

    private String firstName;
    private String lastName;
    private LocalDate dateHired;
    private Title title;

    public TeacherData() {
    }

    public TeacherData(String firstName, String lastName, LocalDate dateHired, Title title) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateHired = dateHired;
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateHired() {
        return dateHired;
    }

    public void setDateHired(LocalDate dateHired) {
        this.dateHired = dateHired;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "TeacherData [firstName=" + firstName + ", lastName=" + lastName + ", dateHired=" + dateHired
                + ", title=" + title + "]";
    }

}
