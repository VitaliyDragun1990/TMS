package org.vdragun.tms.ui.rest.resource.v1.student;

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
import org.vdragun.tms.core.application.service.student.StudentService;

@WebMvcTest(controllers = DeleteStudentResource.class)
@Import({ WebConfig.class, WebRestConfig.class })
@DisplayName("Delete Student Resource")
public class DeleteStudentResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentServiceMock;

    @Test
    void shouldDeleteStudentByGivenIdentifier() throws Exception {
        Integer studentId = 1;

        mockMvc.perform(delete("/api/v1/students/{studentId}", studentId)
                .locale(Locale.US))
                .andExpect(status().isOk());

        verify(studentServiceMock, times(1)).deleteStudentById(studentId);
    }

}
