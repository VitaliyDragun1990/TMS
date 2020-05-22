package org.vdragun.tms.ui.rest.resource.v1.student;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.vdragun.tms.config.WebConfig;
import org.vdragun.tms.config.WebRestConfig;
import org.vdragun.tms.core.application.service.student.StudentService;
import org.vdragun.tms.core.application.service.student.UpdateStudentData;
import org.vdragun.tms.core.domain.Student;
import org.vdragun.tms.ui.rest.resource.v1.JsonVerifier;
import org.vdragun.tms.ui.web.controller.EntityGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = UpdateStudentResource.class)
@Import({ WebConfig.class, WebRestConfig.class, JsonVerifier.class })
@DisplayName("Update Student Resource")
public class UpdateStudentResourceTest {

    private static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";
    private static final int GROUP_ID = 2;
    private static final int STUDENT_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JsonVerifier jsonVerifier;

    @MockBean
    private StudentService studentServiceMock;

    @Captor
    private ArgumentCaptor<UpdateStudentData> captor;

    private EntityGenerator generator = new EntityGenerator();

    @Test
    void shouldUpdateExistingStudent() throws Exception {
        UpdateStudentData updateData = new UpdateStudentData(STUDENT_ID, GROUP_ID, "Jack", "Smith", asList(3, 4));
        Student expectedStudent = generator.generateStudent();
        when(studentServiceMock.updateExistingStudent(any(UpdateStudentData.class))).thenReturn(expectedStudent);
        
        ResultActions resultActions = mockMvc.perform(put("/api/v1/students/{studentId}", STUDENT_ID)
                .locale(Locale.US)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE_HAL_JSON));
        
        verify(studentServiceMock, times(1)).updateExistingStudent(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(updateData));
        jsonVerifier.verifyStudentJson(resultActions, expectedStudent);
    }

}
