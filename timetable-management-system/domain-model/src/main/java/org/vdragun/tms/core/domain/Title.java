package org.vdragun.tms.core.domain;

/**
 * Represents title {@link Teacher} can have.
 * 
 * @author Vitaliy Dragun
 *
 */
public enum Title {
    PROFESSOR,
    INSTRUCTOR,
    ASSOCIATE_PROFESSOR;

    public String asString() {
        switch (this) {
        case PROFESSOR:
            return "Professor";
        case INSTRUCTOR:
            return "Instructor";
        case ASSOCIATE_PROFESSOR:
            return "Associate Professor";
        default:
            throw new IllegalStateException();
        }
    }

    public static Title fromString(String s) {
        if (PROFESSOR.asString().equalsIgnoreCase(s)) {
            return PROFESSOR;
        } else if (INSTRUCTOR.asString().equalsIgnoreCase(s)) {
            return INSTRUCTOR;
        } else if (ASSOCIATE_PROFESSOR.asString().equalsIgnoreCase(s)) {
            return ASSOCIATE_PROFESSOR;
        } else {
            throw new IllegalArgumentException("Invalid title value: " + s);
        }
    }
}
