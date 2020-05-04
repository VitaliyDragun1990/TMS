package org.vdragun.tms.ui.web.util;

/**
 * Contains constant values used across web layer
 * 
 * @author Vitaliy Dragun
 *
 */
public final class Constants {

    private Constants() {
    }

    public static final class Message {

        public static final String ALL_TIMETABLES = "msg.timetablesAll";
        public static final String ALL_TEACHERS = "msg.teachersAll";
        public static final String ALL_STUDENTS = "msg.studentsAll";
        public static final String ALL_COURSES = "msg.coursesAll";
        public static final String TIMETABLES_FOR_TEACHER = "msg.timetablesForTeacher";
        public static final String TIMETABLES_FOR_STUDENT = "msg.timetablesForStudent";
        public static final String DATE_FORMAT = "format.date";
        public static final String REQUESTED_RESOURCE = "msg.requestedResource";

        private Message() {
        }
    }
}
