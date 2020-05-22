package org.vdragun.tms.ui.rest.resource.v1.timetable;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.config.WebConfig;
import org.vdragun.tms.config.WebRestConfig;
import org.vdragun.tms.core.application.service.timetable.TimetableService;

@WebMvcTest(controllers = DeleteTimetableResource.class)
@Import({ WebConfig.class, WebRestConfig.class })
@DisplayName("Delete Timetable Resource")
public class DeleteTimetableResourceTest {

    private static final Integer TIMETABLE_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TimetableService timetableServiceMock;

    @Test
    void shouldDeleteTimetableById() throws Exception {
        mockMvc.perform(delete("/api/v1/timetables/{timetableId}", TIMETABLE_ID)
                .locale(Locale.US))
                .andExpect(status().isOk());

        verify(timetableServiceMock, times(1)).deleteTimetableById(TIMETABLE_ID);
    }

}
