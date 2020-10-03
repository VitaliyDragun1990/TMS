package org.vdragun.tms.core.domain;

import javax.persistence.MappedSuperclass;

/**
 * Abstract class with common properties that represents person
 * 
 * @author Vitaliy Dragun
 *
 */
@MappedSuperclass
abstract class Person {

    protected String firstName;
    protected String lastName;

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    protected Person() {
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
    
}
