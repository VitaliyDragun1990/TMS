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
        public static final String DATE_TIME_FORMAT = "format.datetime";
        public static final String REQUESTED_RESOURCE = "msg.requestedResource";
        public static final String TIMETABLE_DELETE_SUCCESS = "msg.timetableDeleteSuccess";
        public static final String STUDENT_DELETE_SUCCESS = "msg.studentDeleteSuccess";
        public static final String STUDENT_UPDATE_SUCCESS = "msg.studentUpdateSuccess";
        public static final String STUDENT_REGISTER_SUCCESS = "msg.studentRegisterSuccess";
        public static final String TEACHER_REGISTER_SUCCESS = "msg.teacherRegisterSuccess";
        public static final String COURSE_REGISTER_SUCCESS = "msg.courseRegisterSuccess";
        public static final String TIMETABLE_UPDATE_SUCCESS = "msg.timetableUpdateSuccess";
        public static final String TIMETABLE_REGISTER_SUCCESS = "msg.timetableRegisterSuccess";

        private Message() {
        }
    }

    public static final class Page {

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
        public static final String SERVER_ERROR = "500";

        private Page() {
        }
    }

    public static final class Attribute {

        public static final String MESSAGE = "msg";
        public static final String INFO_MESSAGE = "infoMsg";
        public static final String COURSES = "courses";
        public static final String COURSE = "course";
        public static final String STUDENTS = "students";
        public static final String STUDENT = "student";
        public static final String STUDENT_ID = "studentId";
        public static final String TEACHERS = "teachers";
        public static final String TEACHER = "teacher";
        public static final String TIMETABLE = "timetable";
        public static final String TIMETABLES = "timetables";
        public static final String CATEGORIES = "categories";
        public static final String CLASSROOMS = "classrooms";
        public static final String GROUPS = "groups";
        public static final String ERROR = "error";
        public static final String VALIDATED = "validated";

        private Attribute() {
        }
    }
}
