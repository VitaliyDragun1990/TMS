package org.vdragun.tms.config;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

import org.hamcrest.Matcher;
import org.springframework.test.util.JsonPathExpectationsHelper;
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.rest.api.v1.mapper.CourseModelMapper;
import org.vdragun.tms.ui.rest.api.v1.mapper.StudentModelMapper;
import org.vdragun.tms.ui.rest.api.v1.mapper.TeacherModelMapper;
import org.vdragun.tms.ui.rest.api.v1.mapper.TimetableModelMapper;
import org.vdragun.tms.ui.rest.api.v1.model.CourseModel;
import org.vdragun.tms.ui.rest.api.v1.model.StudentModel;
import org.vdragun.tms.ui.rest.api.v1.model.TeacherModel;
import org.vdragun.tms.ui.rest.api.v1.model.TimetableModel;
import org.vdragun.tms.util.localizer.MessageLocalizer;

/**
 * Contains convenient methods to facilitate testing JSON structure in tests
 * conducted with {@link MockMvc}
 * 
 * @author Vitaliy Dragun
 *
 */
public class JsonVerifier {

    private MessageLocalizer messageLocalizer;

    public JsonVerifier(MessageLocalizer messageLocalizer) {
        this.messageLocalizer = messageLocalizer;
    }

    public void verifyErrorMessage(String json, String msgCode, Object... msgArgs) throws Exception {
        String expectedMessage = messageLocalizer.getLocalizedMessage(msgCode, msgArgs);
        jsonPath("$.apierror.message").assertValue(json, equalTo(expectedMessage));
    }

    public void verifyValidationError(
            String json,
            String propertyName,
            String msgCode,
            Object... msgArgs) throws Exception {
        String expectedMessage = messageLocalizer.getLocalizedMessage(msgCode, msgArgs);
        jsonPath("$.apierror.subErrors").assertValue(json, hasItem(
                allOf(
                        hasEntry("field", propertyName),
                        hasEntry("message", expectedMessage))));
    }

    public void verifyValidationErrorsCount(
            String json,
            int expectedCount) throws Exception {
        jsonPath("$.apierror.subErrors").assertValue(json, hasSize(expectedCount));
    }

    public void verifyTimetableJson(String json, List<Timetable> expectedContent) throws Exception {
        List<TimetableModel> expected = TimetableModelMapper.INSTANCE.map(expectedContent);
        for (int i = 0; i < expected.size(); i++) {
            TimetableModel expectedStudent = expected.get(i);

            jsonPath(format("$._embedded.timetables[%d].id", i))
                    .assertValue(json, equalTo(expectedStudent.getId()));
            jsonPath(format("$._embedded.timetables[%d].startTime", i))
                    .assertValue(json, equalTo(expectedStudent.getStartTime()));
            jsonPath(format("$._embedded.timetables[%d].duration", i))
                    .assertValue(json, equalTo(expectedStudent.getDuration()));
            jsonPath(format("$._embedded.timetables[%d].classroomId", i))
                    .assertValue(json, equalTo(expectedStudent.getClassroomId()));
            jsonPath(format("$._embedded.timetables[%d].classroomCapacity", i))
                    .assertValue(json, equalTo(expectedStudent.getClassroomCapacity()));

            CourseModel expectedCourse = expectedStudent.getCourse();

            jsonPath(format("$._embedded.timetables[%d].course.id", i))
                    .assertValue(json, equalTo(expectedCourse.getId()));
            jsonPath(format("$._embedded.timetables[%d].course.name", i))
                    .assertValue(json, equalTo(expectedCourse.getName()));
            jsonPath(format("$._embedded.timetables[%d].course.description", i))
                    .assertValue(json, equalTo(expectedCourse.getDescription()));
            jsonPath(format("$._embedded.timetables[%d].course.categoryCode", i))
                    .assertValue(json, equalTo(expectedCourse.getCategoryCode()));
            jsonPath(format("$._embedded.timetables[%d].course.teacherId", i))
                    .assertValue(json, equalTo(expectedCourse.getTeacherId()));
            jsonPath(format("$._embedded.timetables[%d].course.teacherFullName", i))
                    .assertValue(json, equalTo(expectedCourse.getTeacherFullName()));
        }
    }

    public void verifyTimetableJson(String json, Timetable expectedContent) throws Exception {
        TimetableModel expected = TimetableModelMapper.INSTANCE.map(expectedContent);
        jsonPath("$.id").assertValue(json, equalTo(expected.getId()));
        jsonPath("$.startTime").assertValue(json, equalTo(expected.getStartTime()));
        jsonPath("$.duration").assertValue(json, equalTo(expected.getDuration()));
        jsonPath("$.classroomId").assertValue(json, equalTo(expected.getClassroomId()));
        jsonPath("$.classroomCapacity").assertValue(json, equalTo(expected.getClassroomCapacity()));

        CourseModel expectedCourse = expected.getCourse();

        jsonPath("$.course.id").assertValue(json, equalTo(expectedCourse.getId()));
        jsonPath("$.course.name").assertValue(json, equalTo(expectedCourse.getName()));
        jsonPath("$.course.description").assertValue(json, equalTo(expectedCourse.getDescription()));
        jsonPath("$.course.categoryCode").assertValue(json, equalTo(expectedCourse.getCategoryCode()));
        jsonPath("$.course.teacherId").assertValue(json, equalTo(expectedCourse.getTeacherId()));
        jsonPath("$.course.teacherFullName").assertValue(json, equalTo(expectedCourse.getTeacherFullName()));
    }

    public void verifyCourseJson(String json, List<Course> expectedContent) {
        List<CourseModel> expected = CourseModelMapper.INSTANCE.map(expectedContent);
        for (int i = 0; i < expected.size(); i++) {
            CourseModel model = expected.get(i);
            jsonPath(format("$._embedded.courses[%d].id", i))
                    .assertValue(json, equalTo(model.getId()));
            jsonPath(format("$._embedded.courses[%d].name", i))
                    .assertValue(json, equalTo(model.getName()));
            jsonPath(format("$._embedded.courses[%d].description", i))
                    .assertValue(json, equalTo(model.getDescription()));
            jsonPath(format("$._embedded.courses[%d].categoryCode", i))
                    .assertValue(json, equalTo(model.getCategoryCode()));
            jsonPath(format("$._embedded.courses[%d].teacherId", i))
                    .assertValue(json, equalTo(model.getTeacherId()));
            jsonPath(format("$._embedded.courses[%d].teacherFullName", i))
                    .assertValue(json, equalTo(model.getTeacherFullName()));
        }
    }

    public void verifyCourseJson(String json, Course expectedContent) throws Exception {
        CourseModel expected = CourseModelMapper.INSTANCE.map(expectedContent);
        jsonPath("$.id").assertValue(json, equalTo(expected.getId()));
        jsonPath("$.name").assertValue(json, equalTo(expected.getName()));
        jsonPath("$.description").assertValue(json, equalTo(expected.getDescription()));
        jsonPath("$.categoryCode").assertValue(json, equalTo(expected.getCategoryCode()));
        jsonPath("$.teacherId").assertValue(json, equalTo(expected.getTeacherId()));
        jsonPath("$.teacherFullName").assertValue(json, equalTo(expected.getTeacherFullName()));
    }

    public void verifyStudentJson(String json, List<Student> expectedContent) throws Exception {
        List<StudentModel> expected = StudentModelMapper.INSTANCE.map(expectedContent);
        for (int i = 0; i < expected.size(); i++) {
            StudentModel expectedStudent = expected.get(i);

            jsonPath(format("$._embedded.students[%d].id", i))
                    .assertValue(json, equalTo(expectedStudent.getId()));
            jsonPath(format("$._embedded.students[%d].firstName", i))
                    .assertValue(json, equalTo(expectedStudent.getFirstName()));
            jsonPath(format("$._embedded.students[%d].lastName", i))
                    .assertValue(json, equalTo(expectedStudent.getLastName()));
            if (expectedStudent.getGroup() != null) {
                jsonPath(format("$._embedded.students[%d].group", i))
                        .assertValue(json, equalTo(expectedStudent.getGroup()));
            }
            jsonPath(format("$._embedded.students[%d].enrollmentDate", i))
                    .assertValue(json, equalTo(expectedStudent.getEnrollmentDate()));

            for (int j = 0; j < expectedStudent.getCourses().size(); j++) {
                CourseModel expectedCourse = expectedStudent.getCourses().get(j);

                jsonPath(format("$._embedded.students[%d].courses[%d].id", i, j))
                        .assertValue(json, equalTo(expectedCourse.getId()));
                jsonPath(format("$._embedded.students[%d].courses[%d].name", i, j))
                        .assertValue(json, equalTo(expectedCourse.getName()));
                jsonPath(format("$._embedded.students[%d].courses[%d].description", i, j))
                        .assertValue(json, equalTo(expectedCourse.getDescription()));
                jsonPath(format("$._embedded.students[%d].courses[%d].categoryCode", i, j))
                        .assertValue(json, equalTo(expectedCourse.getCategoryCode()));
                jsonPath(format("$._embedded.students[%d].courses[%d].teacherId", i, j))
                        .assertValue(json, equalTo(expectedCourse.getTeacherId()));
                jsonPath(format("$._embedded.students[%d].courses[%d].teacherFullName", i, j))
                        .assertValue(json, equalTo(expectedCourse.getTeacherFullName()));
            }
        }
    }

    public void verifyStudentJson(String json, Student expected) throws Exception {
        StudentModel expectedStudent = StudentModelMapper.INSTANCE.map(expected);

        jsonPath("$.id").assertValue(json, equalTo(expectedStudent.getId()));
        jsonPath("$.firstName").assertValue(json, equalTo(expectedStudent.getFirstName()));
        jsonPath("$.lastName").assertValue(json, equalTo(expectedStudent.getLastName()));
        if (expected.getGroup() != null) {
            jsonPath("$.group").assertValue(json, equalTo(expectedStudent.getGroup()));
        }
        jsonPath("$.enrollmentDate").assertValue(json, equalTo(expectedStudent.getEnrollmentDate()));

        for (int j = 0; j < expectedStudent.getCourses().size(); j++) {
            CourseModel expectedCourse = expectedStudent.getCourses().get(j);

            jsonPath(format("$.courses[%d].id", j)).assertValue(json, equalTo(expectedCourse.getId()));
            jsonPath(format("$.courses[%d].name", j)).assertValue(json, equalTo(expectedCourse.getName()));
            jsonPath(format("$.courses[%d].description", j))
                    .assertValue(json, equalTo(expectedCourse.getDescription()));
            jsonPath(format("$.courses[%d].categoryCode", j))
                    .assertValue(json, equalTo(expectedCourse.getCategoryCode()));
            jsonPath(format("$.courses[%d].teacherId", j))
                    .assertValue(json, equalTo(expectedCourse.getTeacherId()));
            jsonPath(format("$.courses[%d].teacherFullName", j))
                    .assertValue(json, equalTo(expectedCourse.getTeacherFullName()));
        }
    }
    
    public void verifyTeacherJson(String json, List<Teacher> expectedContent) throws Exception {
        List<TeacherModel> expected = TeacherModelMapper.INSTANCE.map(expectedContent);
        for (int i = 0; i < expected.size(); i++) {
            TeacherModel expectedTeacher = expected.get(i);

            jsonPath(format("$._embedded.teachers[%d].id", i))
                    .assertValue(json, equalTo(expectedTeacher.getId()));
            jsonPath(format("$._embedded.teachers[%d].firstName", i))
                    .assertValue(json, equalTo(expectedTeacher.getFirstName()));
            jsonPath(format("$._embedded.teachers[%d].lastName", i))
                    .assertValue(json, equalTo(expectedTeacher.getLastName()));
            jsonPath(format("$._embedded.teachers[%d].title", i))
                    .assertValue(json, equalTo(expectedTeacher.getTitle()));
            jsonPath(format("$._embedded.teachers[%d].dateHired", i))
                    .assertValue(json, equalTo(expectedTeacher.getDateHired()));

            for (int j = 0; j < expectedTeacher.getCourses().size(); j++) {
                CourseModel expectedCourse = expectedTeacher.getCourses().get(j);

                jsonPath(format("$._embedded.teachers[%d].courses[%d].id", i, j))
                        .assertValue(json, equalTo(expectedCourse.getId()));
                jsonPath(format("$._embedded.teachers[%d].courses[%d].name", i, j))
                        .assertValue(json, equalTo(expectedCourse.getName()));
                jsonPath(format("$._embedded.teachers[%d].courses[%d].description", i, j))
                        .assertValue(json, equalTo(expectedCourse.getDescription()));
                jsonPath(format("$._embedded.teachers[%d].courses[%d].categoryCode", i, j))
                        .assertValue(json, equalTo(expectedCourse.getCategoryCode()));
                jsonPath(format("$._embedded.teachers[%d].courses[%d].teacherId", i, j))
                        .assertValue(json, equalTo(expectedCourse.getTeacherId()));
                jsonPath(format("$._embedded.teachers[%d].courses[%d].teacherFullName", i, j))
                        .assertValue(json, equalTo(expectedCourse.getTeacherFullName()));
            }
        }
    }
    
    public void verifyTeacherJson(String json, Teacher expectedContent) throws Exception {
        TeacherModel expectedTeacher = TeacherModelMapper.INSTANCE.map(expectedContent);

        jsonPath("$.id").assertValue(json, equalTo(expectedTeacher.getId()));
        jsonPath("$.firstName").assertValue(json, equalTo(expectedTeacher.getFirstName()));
        jsonPath("$.lastName").assertValue(json, equalTo(expectedTeacher.getLastName()));
        jsonPath("$.title").assertValue(json, equalTo(expectedTeacher.getTitle()));
        jsonPath("$.dateHired").assertValue(json, equalTo(expectedTeacher.getDateHired()));

        for (int j = 0; j < expectedTeacher.getCourses().size(); j++) {
            CourseModel expectedCourse = expectedTeacher.getCourses().get(j);

            jsonPath(format("$.courses[%d].id", j)).assertValue(json, equalTo(expectedCourse.getId()));
            jsonPath(format("$.courses[%d].name", j)).assertValue(json, equalTo(expectedCourse.getName()));
            jsonPath(format("$.courses[%d].description", j))
                    .assertValue(json, equalTo(expectedCourse.getDescription()));
            jsonPath(format("$.courses[%d].categoryCode", j))
                    .assertValue(json, equalTo(expectedCourse.getCategoryCode()));
            jsonPath(format("$.courses[%d].teacherId", j))
                    .assertValue(json, equalTo(expectedCourse.getTeacherId()));
            jsonPath(format("$.courses[%d].teacherFullName", j))
                    .assertValue(json, equalTo(expectedCourse.getTeacherFullName()));
        }
    }

    public void verifyJson(String body, String expression, Matcher<?> matcher) {
        jsonPath(expression).assertValue(body, matcher);
    }

    public String getValueByExpression(String body, String expression) {
        return String.valueOf(jsonPath(expression).evaluateJsonPath(body));
    }

    private JsonPathExpectationsHelper jsonPath(String expression) {
        return new JsonPathExpectationsHelper(expression);
    }
}
