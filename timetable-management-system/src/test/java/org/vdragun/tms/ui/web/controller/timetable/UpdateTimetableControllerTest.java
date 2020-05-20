package org.vdragun.tms.ui.web.controller.timetable;

import static java.lang.String.format;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.config.WebConfig;
import org.vdragun.tms.config.WebMvcConfig;
import org.vdragun.tms.core.application.exception.ResourceNotFoundException;
import org.vdragun.tms.core.application.service.classroom.ClassroomService;
import org.vdragun.tms.core.application.service.timetable.TimetableService;
import org.vdragun.tms.core.application.service.timetable.UpdateTimetableData;
import org.vdragun.tms.core.domain.Classroom;
import org.vdragun.tms.core.domain.Timetable;
import org.vdragun.tms.ui.common.util.Constants.Attribute;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.common.util.Constants.Page;
import org.vdragun.tms.ui.web.controller.EntityGenerator;
import org.vdragun.tms.ui.web.controller.MessageProvider;

/**
 * @author Vitaliy Dragun
 *
 */
@WebMvcTest(controllers = UpdateTimetableController.class)
@Import({ WebConfig.class, WebMvcConfig.class, MessageProvider.class })
@DisplayName("Update Timetable Controller")
public class UpdateTimetableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageProvider messageProvider;

    @MockBean
    private TimetableService timetableServiceMock;

    @MockBean
    private ClassroomService classroomServiceMock;

    @Captor
    private ArgumentCaptor<UpdateTimetableData> captor;

    private EntityGenerator generator = new EntityGenerator();

    private String dateTimeFormat;

    @BeforeEach
    void setUp() {
        dateTimeFormat = getMessage(Message.DATE_TIME_FORMAT);
    }

    private String getMessage(String msgCode, Object... args) {
        return messageProvider.getMessage(msgCode, args);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern(dateTimeFormat));
    }

    @Test
    void shouldShowTimetableUpdateForm() throws Exception {
        List<Classroom> classrooms = generator.generateClassrooms(10);
        Timetable timetableToUpdate = generator.generateTimetable();
        when(classroomServiceMock.findAllClassrooms()).thenReturn(classrooms);
        when(timetableServiceMock.findTimetableById(timetableToUpdate.getId())).thenReturn(timetableToUpdate);

        mockMvc.perform(get("/timetables/{timetableId}/update", timetableToUpdate.getId()).locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.CLASSROOMS, Attribute.TIMETABLE))
                .andExpect(model().attribute(Attribute.CLASSROOMS, equalTo(classrooms)))
                .andExpect(model().attribute(Attribute.TIMETABLE,
                        samePropertyValuesAs(updateDataFrom(timetableToUpdate))))
                .andExpect(view().name(Page.TIMETABLE_UPDATE_FORM));
    }

    @Test
    void shouldShowBadRequestPageIfTimetableIdentifierIsNotNumber() throws Exception {
        String timetableId = "id";

        mockMvc.perform(get("/timetables/{timetableId}/update", timetableId).locale(Locale.US))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(Attribute.ERROR, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.ERROR, containsString(format("\"%s\"", timetableId))))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/timetables/" + timetableId + "/update"))))
                .andExpect(view().name(Page.BAD_REQUEST));
    }

    @Test
    void shouldShowNotFoundPageIfNoTimetableToUpdateWithGivenIdentifier() throws Exception {
        Integer timetableId = 1;
        when(timetableServiceMock.findTimetableById(any(Integer.class)))
                .thenThrow(new ResourceNotFoundException("Timetable with id=%d not found", timetableId));

        mockMvc.perform(get("/timetables/{timetableId}/update", timetableId).locale(Locale.US))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists(Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/timetables/" + timetableId + "/update"))))
                .andExpect(view().name(Page.NOT_FOUND));
    }

    @Test
    void shouldUpdateTimetableIfNoErrors() throws Exception {
        Integer timetableId = 1;
        LocalDateTime startTime = LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.MINUTES);
        Integer duration = 60;
        Integer classroomId = 1;

        mockMvc.perform(post("/timetables/{timetableId}", timetableId).locale(Locale.US)
                .param("timetableId", timetableId.toString())
                .param("startTime", formatDateTime(startTime))
                .param("durationInMinutes", duration.toString())
                .param("classroomId", classroomId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(Attribute.INFO_MESSAGE,
                        equalTo(getMessage(Message.TIMETABLE_UPDATE_SUCCESS))))
                .andExpect(redirectedUrlTemplate("/timetables/{timetableId}", timetableId));

        UpdateTimetableData expected = new UpdateTimetableData(timetableId, startTime, duration, classroomId);
        verify(timetableServiceMock, times(1)).updateExistingTimetable(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(expected));
    }

    @Test
    void shouldShowTimetableUpdateFormIfValidationErrors() throws Exception {
        Integer timetableId = 1;
        LocalDateTime pastStartTime = LocalDateTime.now().minusDays(3).truncatedTo(ChronoUnit.MINUTES);
        Integer invalidDuration = 10;
        Integer invalidClassroomId = -1;

        mockMvc.perform(post("/timetables/{timetableId}", timetableId).locale(Locale.US)
                .param("timetableId", timetableId.toString())
                .param("startTime", formatDateTime(pastStartTime))
                .param("durationInMinutes", invalidDuration.toString())
                .param("classroomId", invalidClassroomId.toString()))
                .andExpect(status().isOk())
                .andExpect(model().errorCount(3))
                .andExpect(model().attributeHasFieldErrors("timetable", "startTime", "durationInMinutes",
                        "classroomId"))
                .andExpect(model().attribute(Attribute.VALIDATED, equalTo(true)))
                .andExpect(view().name(Page.TIMETABLE_UPDATE_FORM));

        verify(timetableServiceMock, never()).updateExistingTimetable(any(UpdateTimetableData.class));
    }
    
    @Test
    void shouldShowPageNotFoundIfTryToUpdateNonExistedTimetable() throws Exception {
        Integer timetableId = 1;
        LocalDateTime startTime = LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.MINUTES);
        Integer duration = 60;
        Integer classroomId = 1;
        doThrow(new ResourceNotFoundException("Timetable with id=%d not found", timetableId))
                .when(timetableServiceMock).updateExistingTimetable(any(UpdateTimetableData.class));

        mockMvc.perform(post("/timetables/{timetableId}", timetableId).locale(Locale.US)
                .param("timetableId", timetableId.toString())
                .param("startTime", formatDateTime(startTime))
                .param("durationInMinutes", duration.toString())
                .param("classroomId", classroomId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists(Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/timetables/" + timetableId))))
                .andExpect(view().name(Page.NOT_FOUND));

        UpdateTimetableData expected = new UpdateTimetableData(timetableId, startTime, duration, classroomId);
        verify(timetableServiceMock, times(1)).updateExistingTimetable(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(expected));
    }

    @Test
    void shouldShowPageBadRequestIfTryToUpdateTimetableWithNonNumberIdentifier() throws Exception {
        String invalidTimetableId = "id";
        LocalDateTime startTime = LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.MINUTES);
        Integer duration = 60;
        Integer classroomId = 1;

        mockMvc.perform(post("/timetables/{timetableId}", invalidTimetableId).locale(Locale.US)
                .param("timetableId", invalidTimetableId.toString())
                .param("startTime", formatDateTime(startTime))
                .param("durationInMinutes", duration.toString())
                .param("classroomId", classroomId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(Attribute.ERROR, Attribute.MESSAGE))
                .andExpect(model().attribute(Attribute.ERROR, containsString(format("\"%s\"", invalidTimetableId))))
                .andExpect(model().attribute(Attribute.MESSAGE,
                        equalTo(getMessage(Message.REQUESTED_RESOURCE, "/timetables/" + invalidTimetableId))))
                .andExpect(view().name(Page.BAD_REQUEST));
    }

    private UpdateTimetableData updateDataFrom(Timetable timetable) {
        return new UpdateTimetableData(
                timetable.getId(),
                timetable.getStartTime(),
                timetable.getDurationInMinutes(),
                timetable.getClassroom().getId());
    }

}
