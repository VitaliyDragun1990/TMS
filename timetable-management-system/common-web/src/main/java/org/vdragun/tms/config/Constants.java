package org.vdragun.tms.config;

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

        public static final String DATE_FORMAT_DEFAULT = "format.date.default";

        public static final String DATE_TIME_FORMAT = "format.datetime";

        public static final String DATE_TIME_FORMAT_DEFAULT = "format.datetime.default";

        public static final String TIMESTAMP_FORMAT = "format.timestamp";

        public static final String TIMESTAMP_FORMAT_DEFAULT = "format.timestamp.default";

        public static final String REQUESTED_RESOURCE = "msg.requestedResource";

        public static final String TIMETABLE_DELETE_SUCCESS = "msg.timetableDeleteSuccess";

        public static final String STUDENT_DELETE_SUCCESS = "msg.studentDeleteSuccess";

        public static final String STUDENT_UPDATE_SUCCESS = "msg.studentUpdateSuccess";

        public static final String STUDENT_REGISTER_SUCCESS = "msg.studentRegisterSuccess";

        public static final String TEACHER_REGISTER_SUCCESS = "msg.teacherRegisterSuccess";

        public static final String COURSE_REGISTER_SUCCESS = "msg.courseRegisterSuccess";

        public static final String TIMETABLE_UPDATE_SUCCESS = "msg.timetableUpdateSuccess";

        public static final String TIMETABLE_REGISTER_SUCCESS = "msg.timetableRegisterSuccess";

        public static final String REQUIRED_REQUEST_PARAMETER = "msg.requiredRequestParameter";

        public static final String MISSING_REQUIRED_PARAMETER = "msg.missingParameter";

        public static final String UNSUPPORTED_MEDIA_TYPE = "msg.unsuportedMediaType";

        public static final String VALIDATION_ERROR = "msg.validationError";

        public static final String RESOURCE_NOT_FOUND = "msg.resourceNotFound";

        public static final String MALFORMED_JSON_REQUEST = "msg.malformedJsonRequest";

        public static final String MALFORMED_JSON_RESPONSE = "msg.malformedJsonResponse";

        public static final String NO_HANDLER_FOUND = "msg.noHandlerFound";

        public static final String DB_ERROR = "msg.dbError";

        public static final String INTERNAL_SERVER_ERROR = "msg.internalServerError";

        public static final String ARGUMENT_TYPE_MISSMATCH = "msg.argumentTypeMissmatch";

        public static final String POSITIVE_ID = "Positive.id";

        public static final String BAD_CREDENTIALS = "msg.badCredentials";

        public static final String ACCESS_DENIED = "msg.accessDenied";

        public static final String AUTHENTICATION_REQUIRED = "msg.authenticationRequired";

        public static final String INVALID_JWT_TOKEN = "msg.invalidJwtToken";

        private Message() {
        }
    }

    public static final class View {

        public static final String NOT_FOUND = "404";

        public static final String SERVER_ERROR = "500";

        private View() {
        }
    }

    public static final class Attribute {

        public static final String MESSAGE = "msg";

        private Attribute() {
        }
    }

    public static final class Role {

        public static final String ADMIN = "ROLE_ADMIN";

        public static final String STUDENT = "ROLE_STUDENT";

        public static final String TEACHER = "ROLE_TEACHER";
        
        private Role() {
        }
    }
}
