package org.vdragun.tms.core.application.validation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import org.vdragun.tms.core.application.service.student.CreateStudentData;

@SpringJUnitConfig(classes = ValidationConfig.class)
public class CreateStudentDataValidationTest extends AbstractValidationTest {

    private static final String VALID_FIRST_NAME = "Jack";
    private static final String VALID_LAST_NAME = "Smith";
    private static final LocalDate VALID_ENROLLMENT_DATE = LocalDate.now().minusDays(5);

    @Autowired
    private Validator validator;

    @Override
    protected Validator getValidator() {
        return validator;
    }

    @Test
    void shouldPassValidationIfNoValidationErrors() {
        CreateStudentData data = new CreateStudentData(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_ENROLLMENT_DATE);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertFalse(result.hasErrors());
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
        CreateStudentData data = new CreateStudentData(invalidFirstNameValue, VALID_LAST_NAME, VALID_ENROLLMENT_DATE);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "firstName", "PersonName");
    }

    @Test
    void shouldRaiseValidationErrorIfFirstNameIsNotPresent() {
        CreateStudentData data = new CreateStudentData(null, VALID_LAST_NAME, VALID_ENROLLMENT_DATE);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "firstName", "NotNull");
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
        CreateStudentData data = new CreateStudentData(VALID_FIRST_NAME, invalidLastNameValue, VALID_ENROLLMENT_DATE);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "lastName", "PersonName");
    }

    @Test
    void shouldRiseValidationErrorIfLastNameIsNotPresent() {
        CreateStudentData data = new CreateStudentData(VALID_FIRST_NAME, null, VALID_ENROLLMENT_DATE);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "lastName", "NotNull");
    }

    @Test
    void shouldRaiseValidationErrorIfEnrollmentDateIsNotPresent() {
        CreateStudentData data = new CreateStudentData(VALID_FIRST_NAME, VALID_LAST_NAME, null);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "enrollmentDate", "NotNull");
    }

    @Test
    void shouldRaiseValidationErrorIfEnrollmentDateIsInTheFuture() {
        LocalDate futureEnrollmentDate = LocalDate.now().plusDays(5);
        CreateStudentData data = new CreateStudentData(VALID_FIRST_NAME, VALID_LAST_NAME, futureEnrollmentDate);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "enrollmentDate", "PastOrPresent");
    }

}
