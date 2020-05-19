package org.vdragun.tms.ui.web.controller.student;

import static io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils.postForm;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.config.WebMvcConfig;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.application.service.group.GroupService;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.application.service.student.UpdateStudentData;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.common.util.Constants.Attribute;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.common.util.Constants.Page;
import org.vdragun.tms.ui.web.controller.EntityGenerator;
import org.vdragun.tms.ui.web.controller.MessageProvider;

import io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils;

/**
 * @author Vitaliy Dragun
 *
 */
@WebMvcTest(controllers = UpdateStudentController.class)
@Import({ WebMvcConfig.class, MessageProvider.class })
@DisplayName("Update Student Controller")
public class UpdateStudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageProvider messageProvider;

    @MockBean
    private StudentService studentServiceMock;

    @MockBean
    private CourseService courseServiceMock;

    @MockBean
    private GroupService groupServiceMock;

    @Captor
    private ArgumentCaptor<UpdateStudentData> captor;

    private EntityGenerator generator = new EntityGenerator();

    private String getMessage(String msgCode, Object... args) {
        return messageProvider.getMessage(msgCode, args);
    }

    @Test
    void shouldShowUpdateStudentForm() throws Exception {
        Student student = generator.generateStudent();
        List<Course> courses = generator.generateCourses(10);
        List<Group> groups = generator.generateGroups(10);
        when(studentServiceMock.findStudentById(student.getId())).thenReturn(student);
        when(courseServiceMock.findAllCourses()).thenReturn(courses);
        when(groupServiceMock.findAllGroups()).thenReturn(groups);
        
        mockMvc.perform(get("/students/{studentId}/update", student.getId()).locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.STUDENT, Attribute.COURSES, Attribute.GROUPS))
                .andExpect(model().attribute(Attribute.COURSES, equalTo(courses)))
                .andExpect(model().attribute(Attribute.GROUPS, equalTo(groups)))
                .andExpect(model().attribute(Attribute.STUDENT, samePropertyValuesAs(updateDataFrom(student))))
                .andExpect(view().name(Page.STUDENT_UPDATE_FORM));
    }
    
    @Test
    void shouldShowPageNotFoundIfTryToGetUpdateFormForNonExistingStudent() throws Exception {
        Integer studentId = 1;
        when(studentServiceMock.findStudentById(studentId))
                .thenThrow(new ResourceNotFoundException("Student with id=%d not found", studentId));

        mockMvc.perform(get("/students/{studentId}/update", studentId).locale(Locale.US))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists(Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/students/" + studentId + "/update"))))
                .andExpect(view().name(Page.NOT_FOUND));
    }

    @Test
    void shouldShowPageBadRequestIfTryToGetUpdateFormForStudentWithInvalidId() throws Exception {
        String invalidStudentId = "id";

        mockMvc.perform(get("/students/{studentId}/update", invalidStudentId).locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(Attribute.ERROR, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.ERROR, containsString(format("\"%s\"", invalidStudentId))))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/students/" + invalidStudentId + "/update"))))
                .andExpect(view().name(Page.BAD_REQUEST));
    }

    @Test
    void shouldUpdateStudentIfNoErrors() throws Exception {
        Integer studentId = 1;
        Integer groupId = 1;
        List<Integer> courseIds = asList(1);
        UpdateStudentData updateData = new UpdateStudentData(studentId, groupId, "Jack", "Smith", courseIds);

        mockMvc.perform(postForm("/students/" + 1, updateData).locale(Locale.US))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(Attribute.INFO_MESSAGE,
                        equalTo(getMessage(Message.STUDENT_UPDATE_SUCCESS))))
                .andExpect(redirectedUrlTemplate("/students/{studentId}", studentId));

        verify(studentServiceMock, times(1)).updateExistingStudent(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(updateData));
    }

    @Test
    void shouldShowStudentUpdateFormIfValidationErrors() throws Exception {
        Integer studentId = 1;
        Integer invalidGroupId = -1;
        List<Integer> invalidCourseIds = asList(-0);
        String toShortFirstName = "J";
        String blankLastName = "  ";
        UpdateStudentData updateData =
                new UpdateStudentData(studentId, invalidGroupId, toShortFirstName, blankLastName, invalidCourseIds);

        mockMvc.perform(MockMvcRequestBuilderUtils.postForm("/students/" + 1, updateData).locale(Locale.US))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().errorCount(4))
                .andExpect(model().attributeHasFieldErrors("student", "firstName", "lastName", "groupId",
                        "courseIds[0]"))
                .andExpect(model().attribute(Attribute.VALIDATED, equalTo(true)))
                .andExpect(view().name(Page.STUDENT_UPDATE_FORM));

        verify(studentServiceMock, never()).updateExistingStudent(any(UpdateStudentData.class));
    }

    @Test
    void shouldShowPageNotFoundIfTryToUpdateNonExistingStudent() throws Exception {
        Integer nonExistingStudentId = 1;
        Integer groupId = 1;
        List<Integer> courseIds = asList(1);
        doThrow(new ResourceNotFoundException("Student with id=%d not found", nonExistingStudentId))
                .when(studentServiceMock).updateExistingStudent(any(UpdateStudentData.class));

        UpdateStudentData updateData = new UpdateStudentData(nonExistingStudentId, groupId, "Jack", "Smith", courseIds);

        mockMvc.perform(postForm("/students/" + 1, updateData).locale(Locale.US))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists(Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/students/" + nonExistingStudentId))))
                .andExpect(view().name(Page.NOT_FOUND));

        verify(studentServiceMock, times(1)).updateExistingStudent(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(updateData));
    }

    @Test
    void shouldShowPageBadRequestIftryToUpdateStudentUsingInvalidIdentifier() throws Exception {
        String invalidStudentId = "id";
        String firstName = "Jack";
        String lastName = "Smith";
        Integer groupId = 1;
        Integer courseId = 1;

        mockMvc.perform(post("/students/{studentId}", invalidStudentId).locale(Locale.US)
                .param("firstName", firstName)
                .param("lastName", lastName)
                .param("groupId", groupId.toString())
                .param("courseIds[0]", courseId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(Attribute.ERROR, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.ERROR, containsString(format("\"%s\"", invalidStudentId))))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/students/" + invalidStudentId))))
                .andExpect(view().name(Page.BAD_REQUEST));

        verify(studentServiceMock, never()).updateExistingStudent(any(UpdateStudentData.class));
    }

    private UpdateStudentData updateDataFrom(Student student) {
        return new UpdateStudentData(
                student.getId(),
                student.getGroup().getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getCourses().stream().map(Course::getId).collect(toList()));
    }

}
