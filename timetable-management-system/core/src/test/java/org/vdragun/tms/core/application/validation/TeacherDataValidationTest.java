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
import org.vdragun.tms.core.application.service.teacher.TeacherData;
import org.vdragun.tms.core.domain.Title;

@SpringJUnitConfig(classes = ValidationConfig.class)
public class TeacherDataValidationTest extends AbstractValidationTest {

    private static final String VALID_FIRST_NAME = "Jack";

    private static final String VALID_LAST_NAME = "Smith";

    private static final LocalDate VALID_DATE_HIRED = LocalDate.now().minusDays(5);

    @Autowired
    private Validator validator;

    @Override
    protected Validator getValidator() {
        return validator;
    }

    @Test
    void shouldPassValidationIfNoValidationErrors() {
        TeacherData data = new TeacherData(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_DATE_HIRED, Title.PROFESSOR);

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
        TeacherData data = new TeacherData(invalidFirstNameValue, VALID_LAST_NAME, VALID_DATE_HIRED, Title.PROFESSOR);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "firstName", "PersonName");
    }

    @Test
    void shouldRaiseValidationErrorIfFirstNameIsNotPresent() {
        TeacherData data = new TeacherData(null, VALID_LAST_NAME, VALID_DATE_HIRED, Title.PROFESSOR);

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
        TeacherData data = new TeacherData(VALID_FIRST_NAME, invalidLastNameValue, VALID_DATE_HIRED, Title.PROFESSOR);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "lastName", "PersonName");
    }

    @Test
    void shouldRiseValidationErrorIfLastNameIsNotPresent() {
        TeacherData data = new TeacherData(VALID_FIRST_NAME, null, VALID_DATE_HIRED, Title.PROFESSOR);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "lastName", "NotNull");
    }

    @Test
    void shouldRaiseValidationErrorIfDateHiredIsNotPresent() {
        TeacherData data = new TeacherData(VALID_FIRST_NAME, VALID_LAST_NAME, null, Title.PROFESSOR);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "dateHired", "NotNull");
    }

    @Test
    void shouldRaiseValidationErrorIfDateHiredIsInTheFuture() {
        LocalDate futureDateHired = LocalDate.now().plusDays(5);
        TeacherData data = new TeacherData(VALID_FIRST_NAME, VALID_LAST_NAME, futureDateHired, Title.PROFESSOR);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "dateHired", "PastOrPresent");
    }

    @Test
    void shouldRaiseValidationErrorIfTitleIsNotPresent() {
        TeacherData data = new TeacherData(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_DATE_HIRED, null);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "title", "NotNull");
    }

}
