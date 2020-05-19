package org.vdragun.tms.ui.web.controller.timetable;

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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.config.WebMvcConfig;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.ui.common.util.Constants.Attribute;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.common.util.Constants.Page;
import org.vdragun.tms.ui.web.controller.MessageProvider;

/**
 * @author Vitaliy Dragun
 *
 */
@WebMvcTest(controllers = DeleteTimetableController.class)
@Import({ WebMvcConfig.class, MessageProvider.class })
@DisplayName("Delete Timetable Controller")
public class DeleteTimetableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageProvider messageProvider;

    @MockBean
    private TimetableService timetableServiceMock;

    private String getMessage(String msgCode, Object... args) {
        return messageProvider.getMessage(msgCode, args);
    }

    @Test
    void shouldDeleteTimetableByGivenIdentifier() throws Exception {
        Integer timetableId = 1;
        
        mockMvc.perform(post("/timetables/delete").locale(Locale.US)
                .param("id", timetableId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(Attribute.INFO_MESSAGE,
                        equalTo(getMessage(Message.TIMETABLE_DELETE_SUCCESS, timetableId))))
                .andExpect(redirectedUrl("/timetables"));

        verify(timetableServiceMock, times(1)).deleteTimetableById(timetableId);
    }

    @Test
    void shouldShowNotFoundPageInNoTimetableWithProvidedIdentifier() throws Exception {
        Integer timetableId = 1;
        doThrow(new ResourceNotFoundException("Timetable with id=%d not found", timetableId))
                .when(timetableServiceMock).deleteTimetableById(timetableId);

        mockMvc.perform(post("/timetables/delete").locale(Locale.US)
                .param("id", timetableId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists(Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/timetables/delete"))))
                .andExpect(view().name(Page.NOT_FOUND));

        verify(timetableServiceMock, times(1)).deleteTimetableById(timetableId);
    }

    @Test
    void shouldShowBadrequestPageIfProvidedTimetableIdentifierIsNotNumber() throws Exception {
        String invalidTimetableId = "id";

        mockMvc.perform(post("/timetables/delete").locale(Locale.US)
                .param("id", invalidTimetableId))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(Attribute.ERROR, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.ERROR, containsString(format("\"%s\"", invalidTimetableId))))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/timetables/delete"))))
                .andExpect(view().name(Page.BAD_REQUEST));

        verify(timetableServiceMock, never()).deleteTimetableById(any(Integer.class));
    }

}
