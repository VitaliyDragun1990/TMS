package org.vdragun.tms.core.application.validation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import org.vdragun.tms.core.application.service.course.CourseData;

@SpringJUnitConfig(classes = ValidationConfig.class)
public class CourseDataValidationTest extends AbstractValidationTest {

    private static final String VALID_NAME = "Adcanced English";
    private static final String VALID_DESCRIPTION = "New English course";
    private static final Integer VALID_CATEGORY_ID = 1;
    private static final Integer VALID_TEACHER_ID = 1;

    @Autowired
    private Validator validator;

    @Override
    protected Validator getValidator() {
        return validator;
    }

    @Test
    void shouldRaiseValidationErrorIfNameIsNotPresent() {
        CourseData data = new CourseData(null, VALID_DESCRIPTION, VALID_CATEGORY_ID, VALID_TEACHER_ID);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "name", "NotNull");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "english",
            "ENGLISH",
            "englisH",
            "    ",
            "English 123",
            "Adcvanced-English",
            "Adcvanced English.",
    })
    void shouldRaiseValidationErrorIfNameValueIsNotValid(String invalidNameValue) {
        CourseData data = new CourseData(invalidNameValue, VALID_DESCRIPTION, VALID_CATEGORY_ID, VALID_TEACHER_ID);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "name", "CourseName");
    }

    @Test
    void shouldNotRaiseValidationErrorsIfAllValuesAreValid() {
        CourseData data = new CourseData(VALID_NAME, VALID_DESCRIPTION, VALID_CATEGORY_ID, VALID_TEACHER_ID);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertFalse(result.hasErrors());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Hello=World",
            "(Hello World)",
            "",
            "Это предложение не пройдет валидацию."
    })
    void shouldRaiseValidationErrorIfDescriptionIsNotValidLatinSentence(String invalidDescriptionValue) {
        CourseData data = new CourseData(VALID_NAME, invalidDescriptionValue, VALID_CATEGORY_ID, VALID_TEACHER_ID);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "description", "LatinSentence");
    }

    @Test
    void shouldRaiseValidationErrorIfCategoryIdIsNotPresent() {
        CourseData data = new CourseData(VALID_NAME, VALID_DESCRIPTION, null, VALID_TEACHER_ID);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "categoryId", "NotNull");
    }

    @Test
    void shouldRaiseValidationErrorIfCategoryIsNotPositiveNumber() {
        CourseData data = new CourseData(VALID_NAME, VALID_DESCRIPTION, -1, VALID_TEACHER_ID);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "categoryId", "Positive");
    }

    @Test
    void shouldRaiseValidationErrorIfTeacherIdIsNotPresent() {
        CourseData data = new CourseData(VALID_NAME, VALID_DESCRIPTION, VALID_CATEGORY_ID, null);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "teacherId", "NotNull");
    }

    @Test
    void shouldRaiseValidationErrorIfTeacherIdIsNotPositiveNumber() {
        CourseData data = new CourseData(VALID_NAME, VALID_DESCRIPTION, VALID_CATEGORY_ID, 0);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "teacherId", "Positive");
    }

}
