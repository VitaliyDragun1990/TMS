package org.vdragun.tms.core.application.validation;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import org.vdragun.tms.core.application.service.student.UpdateStudentData;

@SpringJUnitConfig(classes = ValidationConfig.class)
class UpdateStudentDataValidationTest extends AbstractValidationTest {

    private static final Integer VALID_STUDENT_ID = 1;

    private static final Integer VALID_GROUP_ID = 1;

    private static final List<Integer> VALID_COURSE_IDS = Arrays.asList(1, 2);

    private static final String VALID_FIRST_NAME = "Jack";

    private static final String VALID_LAST_NAME = "Smith";

    @Autowired
    private Validator validator;

    @Override
    protected Validator getValidator() {
        return validator;
    }

    @Test
    void shouldPassValidationIfNoValidationErrors() {
        UpdateStudentData data = new UpdateStudentData(
                VALID_STUDENT_ID,
                VALID_GROUP_ID,
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_COURSE_IDS);
        
        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertFalse(result.hasErrors());
    }

    @Test
    void shouldRaiseValidationErrorIfStudentIdIsNotPresent() {
        UpdateStudentData data = new UpdateStudentData(
                null,
                VALID_GROUP_ID,
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_COURSE_IDS);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "studentId", "NotNull");
    }

    @Test
    void shouldRaiseValidationErrorIfStudentIdIsNotPositiveNumber() {
        UpdateStudentData data = new UpdateStudentData(
                -1,
                VALID_GROUP_ID,
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_COURSE_IDS);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "studentId", "Positive");
    }

    @Test
    void shouldRaiseValidationErrorIfGroupIdIsNotPositiveNumber() {
        UpdateStudentData data = new UpdateStudentData(
                VALID_STUDENT_ID,
                -1,
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_COURSE_IDS);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "groupId", "Positive");
    }

    @Test
    void shouldRaiseValidationErrorIfFirstNameIsNotPresent() {
        UpdateStudentData data = new UpdateStudentData(
                VALID_STUDENT_ID,
                VALID_GROUP_ID,
                null,
                VALID_LAST_NAME,
                VALID_COURSE_IDS);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "firstName", "NotNull");
    }

    @Test
    void shouldRaiseValidationErrorIfLastNameIsNotPresent() {
        UpdateStudentData data = new UpdateStudentData(
                VALID_STUDENT_ID,
                VALID_GROUP_ID,
                VALID_FIRST_NAME,
                null,
                VALID_COURSE_IDS);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "lastName", "NotNull");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "J",
            "Джек",
            "Jack17",
            "17Jack",
            "jack",
            "Anna-Maria",
            "",
            "   "
    })
    void shouldRaiseValidationErrorIfFirstNameIsInvalid(String invalidFirstNameValue) {
        UpdateStudentData data = new UpdateStudentData(
                VALID_STUDENT_ID,
                VALID_GROUP_ID,
                invalidFirstNameValue,
                VALID_LAST_NAME,
                VALID_COURSE_IDS);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "firstName", "PersonName");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "J",
            "Смит",
            "Smith35",
            "17Smith",
            "smith",
            "White-River",
            "",
            "   "
    })
    void shouldRaiseValidationErrorIfLastNameIsInvalid(String invalidLastNameValue) {
        UpdateStudentData data = new UpdateStudentData(
                VALID_STUDENT_ID,
                VALID_GROUP_ID,
                VALID_FIRST_NAME,
                invalidLastNameValue,
                VALID_COURSE_IDS);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "lastName", "PersonName");
    }

    @Test
    void shouldRaiseValidationErrorsIfCourseIdsContainsNullOrNegativeValues() {
        UpdateStudentData data = new UpdateStudentData(
                VALID_STUDENT_ID,
                VALID_GROUP_ID,
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                asList(null, -1));

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(2));
        assertFieldErrors(result, "courseIds[0]", "NotNull");
        assertFieldErrors(result, "courseIds[1]", "Positive");
    }

}
