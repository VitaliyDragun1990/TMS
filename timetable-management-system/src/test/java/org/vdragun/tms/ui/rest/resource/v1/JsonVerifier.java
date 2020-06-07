package org.vdragun.tms.ui.rest.resource.v1;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.hasEntry;

import java.util.List;

import org.hamcrest.Matcher;
import org.springframework.test.util.JsonPathExpectationsHelper;
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.rest.api.v1.model.CourseModel;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.rest.api.v1.model.StudentModel;
import org.vdragun.tms.ui.rest.api.v1.model.TeacherModel;
import org.vdragun.tms.ui.rest.api.v1.model.TimetableModel;
import org.vdragun.tms.util.translator.Translator;

/**
 * Contains convenient methods to facilitate testing JSON structure in tests
 * conducted with {@link MockMvc}
 * 
 * @author Vitaliy Dragun
 *
 */
public class JsonVerifier {

    private ModelConverter modelConverter;
    private Translator translator;

    public JsonVerifier(ModelConverter modelConverter, Translator translator) {
        this.modelConverter = modelConverter;
        this.translator = translator;
    }

    public void verifyErrorMessage(String json, String msgCode, Object... msgArgs) throws Exception {
        String expectedMessage = translator.getLocalizedMessage(msgCode, msgArgs);
        jPath("$.apierror.message").assertValue(json, equalTo(expectedMessage));
    }

    public void verifyValidationError(
            String json,
            String propertyName,
            String msgCode,
            Object... msgArgs) throws Exception {
        String expectedMessage = translator.getLocalizedMessage(msgCode, msgArgs);
        jPath("$.apierror.subErrors").assertValue(json, hasItem(
                allOf(
                        hasEntry("field", propertyName),
                        hasEntry("message", expectedMessage))));
    }

    public void verifyTimetableJson(String json, List<Timetable> expectedContent) throws Exception {
        List<TimetableModel> expected = modelConverter.convertList(expectedContent, Timetable.class,
                TimetableModel.class);
        for (int i = 0; i < expected.size(); i++) {
            TimetableModel expectedStudent = expected.get(i);

            jPath(format("$._embedded.timetables[%d].id", i))
                    .assertValue(json, equalTo(expectedStudent.getId()));
            jPath(format("$._embedded.timetables[%d].startTime", i))
                    .assertValue(json, equalTo(expectedStudent.getStartTime()));
            jPath(format("$._embedded.timetables[%d].duration", i))
                    .assertValue(json, equalTo(expectedStudent.getDuration()));
            jPath(format("$._embedded.timetables[%d].classroomId", i))
                    .assertValue(json, equalTo(expectedStudent.getClassroomId()));
            jPath(format("$._embedded.timetables[%d].classroomCapacity", i))
                    .assertValue(json, equalTo(expectedStudent.getClassroomCapacity()));

            CourseModel expectedCourse = expectedStudent.getCourse();

            jPath(format("$._embedded.timetables[%d].course.id", i))
                    .assertValue(json, equalTo(expectedCourse.getId()));
            jPath(format("$._embedded.timetables[%d].course.name", i))
                    .assertValue(json, equalTo(expectedCourse.getName()));
            jPath(format("$._embedded.timetables[%d].course.description", i))
                    .assertValue(json, equalTo(expectedCourse.getDescription()));
            jPath(format("$._embedded.timetables[%d].course.categoryCode", i))
                    .assertValue(json, equalTo(expectedCourse.getCategoryCode()));
            jPath(format("$._embedded.timetables[%d].course.teacherId", i))
                    .assertValue(json, equalTo(expectedCourse.getTeacherId()));
            jPath(format("$._embedded.timetables[%d].course.teacherFullName", i))
                    .assertValue(json, equalTo(expectedCourse.getTeacherFullName()));
        }
    }

    public void verifyTimetableJson(String json, Timetable expectedContent) throws Exception {
        TimetableModel expected = modelConverter.convert(expectedContent, TimetableModel.class);
        jPath("$.id").assertValue(json, equalTo(expected.getId()));
        jPath("$.startTime").assertValue(json, equalTo(expected.getStartTime()));
        jPath("$.duration").assertValue(json, equalTo(expected.getDuration()));
        jPath("$.classroomId").assertValue(json, equalTo(expected.getClassroomId()));
        jPath("$.classroomCapacity").assertValue(json, equalTo(expected.getClassroomCapacity()));

        CourseModel expectedCourse = expected.getCourse();

        jPath("$.course.id").assertValue(json, equalTo(expectedCourse.getId()));
        jPath("$.course.name").assertValue(json, equalTo(expectedCourse.getName()));
        jPath("$.course.description").assertValue(json, equalTo(expectedCourse.getDescription()));
        jPath("$.course.categoryCode").assertValue(json, equalTo(expectedCourse.getCategoryCode()));
        jPath("$.course.teacherId").assertValue(json, equalTo(expectedCourse.getTeacherId()));
        jPath("$.course.teacherFullName").assertValue(json, equalTo(expectedCourse.getTeacherFullName()));
    }

    public void verifyCourseJson(String json, List<Course> expectedContent) {
        List<CourseModel> expected = modelConverter.convertList(expectedContent, Course.class, CourseModel.class);
        for (int i = 0; i < expected.size(); i++) {
            CourseModel model = expected.get(i);
            jPath(format("$._embedded.courses[%d].id", i))
                    .assertValue(json, equalTo(model.getId()));
            jPath(format("$._embedded.courses[%d].name", i))
                    .assertValue(json, equalTo(model.getName()));
            jPath(format("$._embedded.courses[%d].description", i))
                    .assertValue(json, equalTo(model.getDescription()));
            jPath(format("$._embedded.courses[%d].categoryCode", i))
                    .assertValue(json, equalTo(model.getCategoryCode()));
            jPath(format("$._embedded.courses[%d].teacherId", i))
                    .assertValue(json, equalTo(model.getTeacherId()));
            jPath(format("$._embedded.courses[%d].teacherFullName", i))
                    .assertValue(json, equalTo(model.getTeacherFullName()));
        }
    }

    public void verifyCourseJson(String json, Course expectedContent) throws Exception {
        CourseModel expected = modelConverter.convert(expectedContent, CourseModel.class);
        jPath("$.id").assertValue(json, equalTo(expected.getId()));
        jPath("$.name").assertValue(json, equalTo(expected.getName()));
        jPath("$.description").assertValue(json, equalTo(expected.getDescription()));
        jPath("$.categoryCode").assertValue(json, equalTo(expected.getCategoryCode()));
        jPath("$.teacherId").assertValue(json, equalTo(expected.getTeacherId()));
        jPath("$.teacherFullName").assertValue(json, equalTo(expected.getTeacherFullName()));
    }

    public void verifyStudentJson(String json, List<Student> expectedContent) throws Exception {
        List<StudentModel> expected = modelConverter.convertList(expectedContent, Student.class, StudentModel.class);
        for (int i = 0; i < expected.size(); i++) {
            StudentModel expectedStudent = expected.get(i);

            jPath(format("$._embedded.students[%d].id", i))
                    .assertValue(json, equalTo(expectedStudent.getId()));
            jPath(format("$._embedded.students[%d].firstName", i))
                    .assertValue(json, equalTo(expectedStudent.getFirstName()));
            jPath(format("$._embedded.students[%d].lastName", i))
                    .assertValue(json, equalTo(expectedStudent.getLastName()));
            jPath(format("$._embedded.students[%d].group", i))
                    .assertValue(json, equalTo(expectedStudent.getGroup()));
            jPath(format("$._embedded.students[%d].enrollmentDate", i))
                    .assertValue(json, equalTo(expectedStudent.getEnrollmentDate()));

            for (int j = 0; j < expectedStudent.getCourses().size(); j++) {
                CourseModel expectedCourse = expectedStudent.getCourses().get(j);

                jPath(format("$._embedded.students[%d].courses[%d].id", i, j))
                        .assertValue(json, equalTo(expectedCourse.getId()));
                jPath(format("$._embedded.students[%d].courses[%d].name", i, j))
                        .assertValue(json, equalTo(expectedCourse.getName()));
                jPath(format("$._embedded.students[%d].courses[%d].description", i, j))
                        .assertValue(json, equalTo(expectedCourse.getDescription()));
                jPath(format("$._embedded.students[%d].courses[%d].categoryCode", i, j))
                        .assertValue(json, equalTo(expectedCourse.getCategoryCode()));
                jPath(format("$._embedded.students[%d].courses[%d].teacherId", i, j))
                        .assertValue(json, equalTo(expectedCourse.getTeacherId()));
                jPath(format("$._embedded.students[%d].courses[%d].teacherFullName", i, j))
                        .assertValue(json, equalTo(expectedCourse.getTeacherFullName()));
            }
        }
    }

    public void verifyStudentJson(String json, Student expectedContent) throws Exception {
        StudentModel expectedStudent = modelConverter.convert(expectedContent, StudentModel.class);

        jPath("$.id").assertValue(json, equalTo(expectedStudent.getId()));
        jPath("$.firstName").assertValue(json, equalTo(expectedStudent.getFirstName()));
        jPath("$.lastName").assertValue(json, equalTo(expectedStudent.getLastName()));
        jPath("$.group").assertValue(json, equalTo(expectedStudent.getGroup()));
        jPath("$.enrollmentDate").assertValue(json, equalTo(expectedStudent.getEnrollmentDate()));

        for (int j = 0; j < expectedStudent.getCourses().size(); j++) {
            CourseModel expectedCourse = expectedStudent.getCourses().get(j);

            jPath(format("$.courses[%d].id", j)).assertValue(json, equalTo(expectedCourse.getId()));
            jPath(format("$.courses[%d].name", j)).assertValue(json, equalTo(expectedCourse.getName()));
            jPath(format("$.courses[%d].description", j))
                    .assertValue(json, equalTo(expectedCourse.getDescription()));
            jPath(format("$.courses[%d].categoryCode", j))
                    .assertValue(json, equalTo(expectedCourse.getCategoryCode()));
            jPath(format("$.courses[%d].teacherId", j))
                    .assertValue(json, equalTo(expectedCourse.getTeacherId()));
            jPath(format("$.courses[%d].teacherFullName", j))
                    .assertValue(json, equalTo(expectedCourse.getTeacherFullName()));
        }
    }
    
    public void verifyTeacherJson(String json, List<Teacher> expectedContent) throws Exception {
        List<TeacherModel> expected = modelConverter.convertList(expectedContent, Teacher.class, TeacherModel.class);
        for (int i = 0; i < expected.size(); i++) {
            TeacherModel expectedTeacher = expected.get(i);

            jPath(format("$._embedded.teachers[%d].id", i))
                    .assertValue(json, equalTo(expectedTeacher.getId()));
            jPath(format("$._embedded.teachers[%d].firstName", i))
                    .assertValue(json, equalTo(expectedTeacher.getFirstName()));
            jPath(format("$._embedded.teachers[%d].lastName", i))
                    .assertValue(json, equalTo(expectedTeacher.getLastName()));
            jPath(format("$._embedded.teachers[%d].title", i))
                    .assertValue(json, equalTo(expectedTeacher.getTitle()));
            jPath(format("$._embedded.teachers[%d].dateHired", i))
                    .assertValue(json, equalTo(expectedTeacher.getDateHired()));

            for (int j = 0; j < expectedTeacher.getCourses().size(); j++) {
                CourseModel expectedCourse = expectedTeacher.getCourses().get(j);

                jPath(format("$._embedded.teachers[%d].courses[%d].id", i, j))
                        .assertValue(json, equalTo(expectedCourse.getId()));
                jPath(format("$._embedded.teachers[%d].courses[%d].name", i, j))
                        .assertValue(json, equalTo(expectedCourse.getName()));
                jPath(format("$._embedded.teachers[%d].courses[%d].description", i, j))
                        .assertValue(json, equalTo(expectedCourse.getDescription()));
                jPath(format("$._embedded.teachers[%d].courses[%d].categoryCode", i, j))
                        .assertValue(json, equalTo(expectedCourse.getCategoryCode()));
                jPath(format("$._embedded.teachers[%d].courses[%d].teacherId", i, j))
                        .assertValue(json, equalTo(expectedCourse.getTeacherId()));
                jPath(format("$._embedded.teachers[%d].courses[%d].teacherFullName", i, j))
                        .assertValue(json, equalTo(expectedCourse.getTeacherFullName()));
            }
        }
    }
    
    public void verifyTeacherJson(String json, Teacher expectedContent) throws Exception {
        TeacherModel expectedTeacher = modelConverter.convert(expectedContent, TeacherModel.class);

        jPath("$.id").assertValue(json, equalTo(expectedTeacher.getId()));
        jPath("$.firstName").assertValue(json, equalTo(expectedTeacher.getFirstName()));
        jPath("$.lastName").assertValue(json, equalTo(expectedTeacher.getLastName()));
        jPath("$.title").assertValue(json, equalTo(expectedTeacher.getTitle()));
        jPath("$.dateHired").assertValue(json, equalTo(expectedTeacher.getDateHired()));

        for (int j = 0; j < expectedTeacher.getCourses().size(); j++) {
            CourseModel expectedCourse = expectedTeacher.getCourses().get(j);

            jPath(format("$.courses[%d].id", j)).assertValue(json, equalTo(expectedCourse.getId()));
            jPath(format("$.courses[%d].name", j)).assertValue(json, equalTo(expectedCourse.getName()));
            jPath(format("$.courses[%d].description", j))
                    .assertValue(json, equalTo(expectedCourse.getDescription()));
            jPath(format("$.courses[%d].categoryCode", j))
                    .assertValue(json, equalTo(expectedCourse.getCategoryCode()));
            jPath(format("$.courses[%d].teacherId", j))
                    .assertValue(json, equalTo(expectedCourse.getTeacherId()));
            jPath(format("$.courses[%d].teacherFullName", j))
                    .assertValue(json, equalTo(expectedCourse.getTeacherFullName()));
        }
    }

    public void verifyJson(String body, String expression, Matcher<?> matcher) {
        jPath(expression).assertValue(body, matcher);
    }

    private JsonPathExpectationsHelper jPath(String expression) {
        return new JsonPathExpectationsHelper(expression);
    }
}
