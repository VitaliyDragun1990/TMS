package org.vdragun.tms.ui.web.controller.student;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.config.WebMvcConfig;
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.application.service.group.GroupService;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.application.service.student.UpdateStudentData;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Group;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.web.controller.EntityGenerator;
import org.vdragun.tms.ui.web.controller.MessageProvider;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Message;
import org.vdragun.tms.ui.web.util.Constants.Page;

import io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils;

/**
 * @author Vitaliy Dragun
 *
 */
@WebMvcTest(controllers = UpdateStudentController.class)
@Import({ WebMvcConfig.class, MessageProvider.class })
public class UpdateStudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageProvider messageProvider;

    @MockBean
    private StudentService studentsServiceMock;

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
        when(studentsServiceMock.findStudentById(student.getId())).thenReturn(student);
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
    void shouldUpdateStudentIfNoValdiationErrors() throws Exception {
        Integer studentId = 1;
        Integer groupId = 1;
        List<Integer> courseIds = asList(1);
        UpdateStudentData updateData = new UpdateStudentData(studentId, groupId, "Jack", "Smith", courseIds);

        mockMvc.perform(MockMvcRequestBuilderUtils.postForm("/students/" + 1, updateData).locale(Locale.US))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(Attribute.INFO_MESSAGE,
                        equalTo(getMessage(Message.STUDENT_UPDATE_SUCCESS))))
                .andExpect(redirectedUrlTemplate("/students/{studentId}", studentId));

        verify(studentsServiceMock, times(1)).updateExistingStudent(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(updateData));
    }

    @Test
    void shouldShowStudentUpdateFormIfValidationErrors() throws Exception {
        Integer studentId = 1;
        Integer invalidGroupId = -1;
        List<Integer> invalidCourseIds = asList(-0);
        String toShortFirstName = "J";
        UpdateStudentData updateData =
                new UpdateStudentData(studentId, invalidGroupId, toShortFirstName, "Smith", invalidCourseIds);

        mockMvc.perform(MockMvcRequestBuilderUtils.postForm("/students/" + 1, updateData).locale(Locale.US))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().errorCount(3))
                .andExpect(model().attributeHasFieldErrors("student", "firstName", "groupId", "courseIds[0]"))
                .andExpect(model().attribute(Attribute.VALIDATED, equalTo(true)))
                .andExpect(view().name(Page.STUDENT_UPDATE_FORM));

        verify(studentsServiceMock, never()).updateExistingStudent(any(UpdateStudentData.class));
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
