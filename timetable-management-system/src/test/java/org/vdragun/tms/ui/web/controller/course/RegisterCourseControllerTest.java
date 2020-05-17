package org.vdragun.tms.ui.web.controller.course;

import static io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils.postForm;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.vdragun.tms.config.WebMvcConfig;
import org.vdragun.tms.core.application.service.category.CategoryService;
import org.vdragun.tms.core.application.service.course.CourseData;
import org.vdragun.tms.core.application.service.course.CourseService;
import org.vdragun.tms.core.application.service.teacher.TeacherService;
import org.vdragun.tms.core.domain.Category;
import org.vdragun.tms.core.domain.Course;
import org.vdragun.tms.core.domain.Teacher;
import org.vdragun.tms.ui.web.controller.EntityGenerator;
import org.vdragun.tms.ui.web.controller.MessageProvider;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Message;
import org.vdragun.tms.ui.web.util.Constants.Page;

/**
 * @author Vitaliy Dragun
 *
 */
@WebMvcTest(controllers = RegisterCourseController.class)
@Import({ WebMvcConfig.class, MessageProvider.class })
public class RegisterCourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageProvider messageProvider;

    @MockBean
    private CourseService courseServiceMock;

    @MockBean
    private TeacherService teacherServiceMock;

    @MockBean
    private CategoryService categoryServiceMock;

    private EntityGenerator generator = new EntityGenerator();


    @Test
    void shouldShowCourseRegistrationForm() throws Exception {
        List<Teacher> teachers = generator.generateTeachers(10);
        List<Category> categories = generator.generateCategories(10);
        when(teacherServiceMock.findAllTeachers()).thenReturn(teachers);
        when(categoryServiceMock.findAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/courses/register").locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Attribute.TEACHERS, Attribute.CATEGORIES, Attribute.COURSE))
                .andExpect(model().attribute(Attribute.TEACHERS, equalTo(teachers)))
                .andExpect(model().attribute(Attribute.CATEGORIES, equalTo(categories)))
                .andExpect(model().attribute(Attribute.COURSE, samePropertyValuesAs(new CourseData())))
                .andExpect(view().name(equalTo(Page.COURSE_FORM)));
    }

    @Test
    void shouldRegisterNewCourseIfNoValidationErrors() throws Exception {
        CourseData courseData = new CourseData("English", "Course description", 1, 1);
        Course course = generator.generateCourse();
        when(courseServiceMock.registerNewCourse(ArgumentMatchers.any(CourseData.class))).thenReturn(course);

        mockMvc.perform(postForm("/courses", courseData).locale(Locale.US))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(Attribute.INFO_MESSAGE,
                        equalTo(getMessage(Message.COURSE_REGISTER_SUCCESS))))
                .andExpect(redirectedUrlTemplate("/courses/{courseId}", course.getId()));

        ArgumentCaptor<CourseData> captor = ArgumentCaptor.forClass(CourseData.class);
        verify(courseServiceMock, times(1)).registerNewCourse(captor.capture());
        assertThat(captor.getValue(), samePropertyValuesAs(courseData));
    }

    @Test
    void shouldShowCourseRegistrationFormIfValidationErrors() throws Exception {
        String toShortName = "E";
        String forbiddenCharDescription = "Course=description*";
        int illelagIdentity = -1;
        CourseData courseData = new CourseData(toShortName, forbiddenCharDescription, illelagIdentity, illelagIdentity);

        mockMvc.perform(postForm("/courses", courseData).locale(Locale.US))
                .andExpect(status().isOk())
                .andExpect(model().errorCount(4))
                .andExpect(model().attributeHasFieldErrors("course", "name", "description", "categoryId", "teacherId"))
                .andExpect(model().attribute(Attribute.VALIDATED, equalTo(true)))
                .andExpect(view().name(Page.COURSE_FORM));

        verify(courseServiceMock, never()).registerNewCourse(any(CourseData.class));
    }

    private String getMessage(String msgCode, Object... args) {
        return messageProvider.getMessage(msgCode, args);
    }

}
