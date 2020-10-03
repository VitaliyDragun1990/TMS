package org.vdragun.tms.config;

/**
 * Contains constant values used across web layer
 * 
 * @author Vitaliy Dragun
 *
 */
public final class WebConstants {

    private WebConstants() {
    }

    public static final class View {

        public static final String COURSES = "courses";
        public static final String COURSE_INFO = "course";
        public static final String COURSE_FORM = "course-form";
        public static final String HOME = "index";
        public static final String STUDENTS = "students";
        public static final String STUDENT_INFO = "student";
        public static final String STUDENT_REG_FORM = "student-req-form";
        public static final String STUDENT_UPDATE_FORM = "student-update-form";
        public static final String TEACHERS = "teachers";
        public static final String TEACHER_INFO = "teacher";
        public static final String TEACHER_FORM = "teacher-form";
        public static final String TIMETABLE_INFO = "timetable";
        public static final String TIMETABLES = "timetables";
        public static final String TIMETABLE_REG_FORM = "timetable-req-form";
        public static final String TIMETABLE_UPDATE_FORM = "timetable-update-form";
        public static final String NOT_FOUND = "404";
        public static final String BAD_REQUEST = "400";
        public static final String ACCESS_DENIED = "403";
        public static final String SERVER_ERROR = "500";
        public static final String SIGN_IN_FORM = "sign-in-form";
        public static final String SIGN_UP_FORM = "sign-up-form";

        private View() {
        }
    }

    public static final class Attribute {

        public static final String MESSAGE = "msg";
        public static final String INFO_MESSAGE = "infoMsg";
        public static final String COURSES = "courses";
        public static final String COURSE = "course";
        public static final String STUDENTS = "students";
        public static final String STUDENT = "student";
        public static final String TEACHERS = "teachers";
        public static final String TEACHER = "teacher";
        public static final String TIMETABLE = "timetable";
        public static final String TIMETABLES = "timetables";
        public static final String CATEGORIES = "categories";
        public static final String CLASSROOMS = "classrooms";
        public static final String GROUPS = "groups";
        public static final String ERROR = "error";
        public static final String VALIDATED = "validated";
        public static final String TITLES = "titles";
        public static final String SIGN_UP_FORM = "signupForm";
        public static final String ROLES = "roles";
        public static final String ACCESS_DENIED_MSG = "accessDeniedMsg";

        private Attribute() {
        }
    }

}
