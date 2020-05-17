package org.vdragun.tms.ui.web.controller.student;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.config.WebMvcConfig;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.ui.web.controller.MessageProvider;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Message;
import org.vdragun.tms.ui.web.util.Constants.Page;

/**
 * @author Vitaliy Dragun
 *
 */
@WebMvcTest(controllers = DeleteStudentController.class)
@Import({ WebMvcConfig.class, MessageProvider.class })
public class DeleteStudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageProvider messageProvider;

    @MockBean
    private StudentService studentsServiceMock;

    private String getMessage(String msgCode, Object... args) {
        return messageProvider.getMessage(msgCode, args);
    }

    @Test
    void shouldDeleteStudentByGivenIdentifier() throws Exception {
        Integer studentId = 1;

        mockMvc.perform(post("/students/delete").locale(Locale.US)
                .param("id", studentId + ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(Attribute.INFO_MESSAGE,
                        equalTo(getMessage(Message.STUDENT_DELETE_SUCCESS, studentId))))
                .andExpect(redirectedUrl("/students"));

        verify(studentsServiceMock, times(1)).deleteStudentById(studentId);
    }

    @Test
    void shouldShowNotFoundPageWithStatusNotFoundIfNoStudentWithGivenIdentifier() throws Exception {
        Integer studentId = 1;
        doThrow(new ResourceNotFoundException("Student with id=%d not found", studentId))
                .when(studentsServiceMock)
                .deleteStudentById(studentId);

        mockMvc.perform(post("/students/delete").locale(Locale.US)
                .param("id", studentId + ""))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists(Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/students/delete"))))
                .andExpect(view().name(Page.NOT_FOUND));

        verify(studentsServiceMock, times(1)).deleteStudentById(studentId);
    }
    
    @Test
    void shouldShowBadRequestPageWithStatusBadRequestIfStudentIdentifierIsNotNumber() throws Exception {
        String studentId = "id";

        mockMvc.perform(post("/students/delete").locale(Locale.US)
                .param("id", studentId + ""))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(Attribute.ERROR, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.ERROR, containsString(format("\"%s\"", studentId))))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/students/delete"))))
                .andExpect(view().name(Page.BAD_REQUEST));

        verify(studentsServiceMock, never()).deleteStudentById(any(Integer.class));
    }

}
