package org.vdragun.tms.ui.rest.resource.v1;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.hasEntry;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.common.util.Translator;
import org.vdragun.tms.ui.rest.api.v1.model.CourseModel;
import org.vdragun.tms.ui.rest.api.v1.model.ModelConverter;
import org.vdragun.tms.ui.rest.api.v1.model.StudentModel;
import org.vdragun.tms.ui.rest.api.v1.model.TeacherModel;
import org.vdragun.tms.ui.rest.api.v1.model.TimetableModel;

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

    public void verifyErrorMessage(ResultActions actions, String msgCode, Object... msgArgs) throws Exception {
        String expectedMessage = translator.getLocalizedMessage(msgCode, msgArgs);
        actions.andExpect(jsonPath("$.apierror.message", equalTo(expectedMessage)));
    }

    public void verifyValidationError(
            ResultActions actions,
            String propertyName,
            String msgCode,
            Object... msgArgs) throws Exception {
        String expectedMessage = translator.getLocalizedMessage(msgCode, msgArgs);
        actions.andExpect(
                jsonPath("$.apierror.subErrors",
                        hasItem(
                                allOf(
                                        hasEntry("field", propertyName),
                                        hasEntry("message", expectedMessage)))));
    }

    public void verifyTimetableJson(ResultActions actions, List<Timetable> timetables) throws Exception {
        List<TimetableModel> expected = modelConverter.convertList(timetables, Timetable.class, TimetableModel.class);
        for (int i = 0; i < expected.size(); i++) {
            TimetableModel expectedStudent = expected.get(i);
            actions
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].id", i),
                            equalTo(expectedStudent.getId())))
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].startTime", i),
                            equalTo(expectedStudent.getStartTime())))
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].duration", i),
                            equalTo(expectedStudent.getDuration())))
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].classroomId", i),
                            equalTo(expectedStudent.getClassroomId())))
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].classroomCapacity", i),
                            equalTo(expectedStudent.getClassroomCapacity())));

            CourseModel expectedCourse = expectedStudent.getCourse();
            actions
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].course.id", i),
                            equalTo(expectedCourse.getId())))
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].course.name", i),
                            equalTo(expectedCourse.getName())))
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].course.description", i),
                            equalTo(expectedCourse.getDescription())))
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].course.categoryCode", i),
                            equalTo(expectedCourse.getCategoryCode())))
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].course.teacherId", i),
                            equalTo(expectedCourse.getTeacherId())))
                    .andExpect(jsonPath(format("$._embedded.timetables[%d].course.teacherFullName", i),
                            equalTo(expectedCourse.getTeacherFullName())));
        }
    }

    public void verifyTimetableJson(ResultActions actions, Timetable timetable) throws Exception {
        TimetableModel expected = modelConverter.convert(timetable, TimetableModel.class);
        actions
                .andExpect(jsonPath("$.id", equalTo(expected.getId())))
                .andExpect(jsonPath("$.startTime", equalTo(expected.getStartTime())))
                .andExpect(jsonPath("$.duration", equalTo(expected.getDuration())))
                .andExpect(jsonPath("$.classroomId", equalTo(expected.getClassroomId())))
                .andExpect(jsonPath("$.classroomCapacity", equalTo(expected.getClassroomCapacity())));

        CourseModel expectedCourse = expected.getCourse();
        actions
                .andExpect(jsonPath("$.course.id", equalTo(expectedCourse.getId())))
                .andExpect(jsonPath("$.course.name", equalTo(expectedCourse.getName())))
                .andExpect(jsonPath("$.course.description", equalTo(expectedCourse.getDescription())))
                .andExpect(jsonPath("$.course.categoryCode", equalTo(expectedCourse.getCategoryCode())))
                .andExpect(jsonPath("$.course.teacherId", equalTo(expectedCourse.getTeacherId())))
                .andExpect(jsonPath("$.course.teacherFullName", equalTo(expectedCourse.getTeacherFullName())));
    }

    public void verifyCourseJson(ResultActions actions, List<Course> courses) throws Exception {
        List<CourseModel> expected = modelConverter.convertList(courses, Course.class, CourseModel.class);
        for (int i = 0; i < expected.size(); i++) {
            CourseModel model = expected.get(i);
            actions
                    .andExpect(jsonPath(format("$._embedded.courses[%d].id", i),
                            equalTo(model.getId())))
                    .andExpect(jsonPath(format("$._embedded.courses[%d].name", i),
                            equalTo(model.getName())))
                    .andExpect(jsonPath(format("$._embedded.courses[%d].description", i),
                            equalTo(model.getDescription())))
                    .andExpect(jsonPath(format("$._embedded.courses[%d].categoryCode", i),
                            equalTo(model.getCategoryCode())))
                    .andExpect(jsonPath(format("$._embedded.courses[%d].teacherId", i),
                            equalTo(model.getTeacherId())))
                    .andExpect(jsonPath(format("$._embedded.courses[%d].teacherFullName", i),
                            equalTo(model.getTeacherFullName())));
        }
    }

    public void verifyCourseJson(ResultActions actions, Course course) throws Exception {
        CourseModel expected = modelConverter.convert(course, CourseModel.class);
        actions
                .andExpect(jsonPath("$.id", equalTo(expected.getId())))
                .andExpect(jsonPath("$.name", equalTo(expected.getName())))
                .andExpect(jsonPath("$.description", equalTo(expected.getDescription())))
                .andExpect(jsonPath("$.categoryCode", equalTo(expected.getCategoryCode())))
                .andExpect(jsonPath("$.teacherId", equalTo(expected.getTeacherId())))
                .andExpect(jsonPath("$.teacherFullName", equalTo(expected.getTeacherFullName())));
    }

    public void verifyStudentJson(ResultActions actions, List<Student> students) throws Exception {
        List<StudentModel> expected = modelConverter.convertList(students, Student.class, StudentModel.class);
        for (int i = 0; i < expected.size(); i++) {
            StudentModel expectedStudent = expected.get(i);
            actions
                    .andExpect(jsonPath(format("$._embedded.students[%d].id", i),
                            equalTo(expectedStudent.getId())))
                    .andExpect(jsonPath(format("$._embedded.students[%d].firstName", i),
                            equalTo(expectedStudent.getFirstName())))
                    .andExpect(jsonPath(format("$._embedded.students[%d].lastName", i),
                            equalTo(expectedStudent.getLastName())))
                    .andExpect(jsonPath(format("$._embedded.students[%d].group", i),
                            equalTo(expectedStudent.getGroup())))
                    .andExpect(jsonPath(format("$._embedded.students[%d].enrollmentDate", i),
                            equalTo(expectedStudent.getEnrollmentDate())));
            for (int j = 0; j < expectedStudent.getCourses().size(); j++) {
                CourseModel expectedCourse = expectedStudent.getCourses().get(j);
                actions
                        .andExpect(jsonPath(format("$._embedded.students[%d].courses[%d].id", i, j),
                                equalTo(expectedCourse.getId())))
                        .andExpect(jsonPath(format("$._embedded.students[%d].courses[%d].name", i, j),
                                equalTo(expectedCourse.getName())))
                        .andExpect(jsonPath(format("$._embedded.students[%d].courses[%d].description", i, j),
                                equalTo(expectedCourse.getDescription())))
                        .andExpect(jsonPath(format("$._embedded.students[%d].courses[%d].categoryCode", i, j),
                                equalTo(expectedCourse.getCategoryCode())))
                        .andExpect(jsonPath(format("$._embedded.students[%d].courses[%d].teacherId", i, j),
                                equalTo(expectedCourse.getTeacherId())))
                        .andExpect(jsonPath(format("$._embedded.students[%d].courses[%d].teacherFullName", i, j),
                                equalTo(expectedCourse.getTeacherFullName())));
            }
        }
    }

    public void verifyStudentJson(ResultActions actions, Student student) throws Exception {
        StudentModel expectedStudent = modelConverter.convert(student, StudentModel.class);
        actions
                .andExpect(jsonPath("$.id", equalTo(expectedStudent.getId())))
                .andExpect(jsonPath("$.firstName", equalTo(expectedStudent.getFirstName())))
                .andExpect(jsonPath("$.lastName", equalTo(expectedStudent.getLastName())))
                .andExpect(jsonPath("$.group", equalTo(expectedStudent.getGroup())))
                .andExpect(jsonPath("$.enrollmentDate", equalTo(expectedStudent.getEnrollmentDate())));

        for (int j = 0; j < expectedStudent.getCourses().size(); j++) {
            CourseModel expectedCourse = expectedStudent.getCourses().get(j);
            actions
                    .andExpect(jsonPath(format("$.courses[%d].id", j),
                            equalTo(expectedCourse.getId())))
                    .andExpect(jsonPath(format("$.courses[%d].name", j),
                            equalTo(expectedCourse.getName())))
                    .andExpect(jsonPath(format("$.courses[%d].description", j),
                            equalTo(expectedCourse.getDescription())))
                    .andExpect(jsonPath(format("$.courses[%d].categoryCode", j),
                            equalTo(expectedCourse.getCategoryCode())))
                    .andExpect(jsonPath(format("$.courses[%d].teacherId", j),
                            equalTo(expectedCourse.getTeacherId())))
                    .andExpect(jsonPath(format("$.courses[%d].teacherFullName", j),
                            equalTo(expectedCourse.getTeacherFullName())));
        }
    }

    public void verifyTeacherJson(ResultActions actions, List<Teacher> teachers) throws Exception {
        List<TeacherModel> expected = modelConverter.convertList(teachers, Teacher.class, TeacherModel.class);
        for (int i = 0; i < expected.size(); i++) {
            TeacherModel expectedTeacher = expected.get(i);
            actions
                    .andExpect(jsonPath(format("$._embedded.teachers[%d].id", i),
                            equalTo(expectedTeacher.getId())))
                    .andExpect(jsonPath(format("$._embedded.teachers[%d].firstName", i),
                            equalTo(expectedTeacher.getFirstName())))
                    .andExpect(jsonPath(format("$._embedded.teachers[%d].lastName", i),
                            equalTo(expectedTeacher.getLastName())))
                    .andExpect(jsonPath(format("$._embedded.teachers[%d].title", i),
                            equalTo(expectedTeacher.getTitle())))
                    .andExpect(jsonPath(format("$._embedded.teachers[%d].dateHired", i),
                            equalTo(expectedTeacher.getDateHired())));
            for (int j = 0; j < expectedTeacher.getCourses().size(); j++) {
                CourseModel expectedCourse = expectedTeacher.getCourses().get(j);
                actions
                        .andExpect(jsonPath(format("$._embedded.teachers[%d].courses[%d].id", i, j),
                                equalTo(expectedCourse.getId())))
                        .andExpect(jsonPath(format("$._embedded.teachers[%d].courses[%d].name", i, j),
                                equalTo(expectedCourse.getName())))
                        .andExpect(jsonPath(format("$._embedded.teachers[%d].courses[%d].description", i, j),
                                equalTo(expectedCourse.getDescription())))
                        .andExpect(jsonPath(format("$._embedded.teachers[%d].courses[%d].categoryCode", i, j),
                                equalTo(expectedCourse.getCategoryCode())))
                        .andExpect(jsonPath(format("$._embedded.teachers[%d].courses[%d].teacherId", i, j),
                                equalTo(expectedCourse.getTeacherId())))
                        .andExpect(jsonPath(format("$._embedded.teachers[%d].courses[%d].teacherFullName", i, j),
                                equalTo(expectedCourse.getTeacherFullName())));
            }
        }
    }

    public void verifyTeacherJson(ResultActions actions, Teacher teacher) throws Exception {
        TeacherModel expectedTeacher = modelConverter.convert(teacher, TeacherModel.class);
        actions
                .andExpect(jsonPath("$.id", equalTo(expectedTeacher.getId())))
                .andExpect(jsonPath("$.firstName", equalTo(expectedTeacher.getFirstName())))
                .andExpect(jsonPath("$.lastName", equalTo(expectedTeacher.getLastName())))
                .andExpect(jsonPath("$.title", equalTo(expectedTeacher.getTitle())))
                .andExpect(jsonPath("$.dateHired", equalTo(expectedTeacher.getDateHired())));
        for (int j = 0; j < expectedTeacher.getCourses().size(); j++) {
            CourseModel expectedCourse = expectedTeacher.getCourses().get(j);
            actions
                    .andExpect(jsonPath(format("$.courses[%d].id", j),
                            equalTo(expectedCourse.getId())))
                    .andExpect(jsonPath(format("$.courses[%d].name", j),
                            equalTo(expectedCourse.getName())))
                    .andExpect(jsonPath(format("$.courses[%d].description", j),
                            equalTo(expectedCourse.getDescription())))
                    .andExpect(jsonPath(format("$.courses[%d].categoryCode", j),
                            equalTo(expectedCourse.getCategoryCode())))
                    .andExpect(jsonPath(format("$.courses[%d].teacherId", j),
                            equalTo(expectedCourse.getTeacherId())))
                    .andExpect(jsonPath(format("$.courses[%d].teacherFullName", j),
                            equalTo(expectedCourse.getTeacherFullName())));
        }
    }
}
