package org.vdragun.tms.core.application.validation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import org.vdragun.tms.core.application.service.timetable.UpdateTimetableData;

@SpringJUnitConfig(classes = ValidationConfig.class)
public class UpdateTimetableDataValidationTest extends AbstractValidationTest {

    private static final Integer VALID_TIMETABLE_ID = 1;

    private static final LocalDateTime VALID_START_TIME = LocalDateTime.now().withHour(9).plusDays(5);

    private static final Integer VALID_DURATION = 60;

    private static final Integer VALID_CLASSROOM_ID = 3;

    @Autowired
    private Validator validator;

    @Override
    protected Validator getValidator() {
        return validator;
    }

    @Test
    void shouldPassValidationIfNoValidationErrors() {
        UpdateTimetableData data = new UpdateTimetableData(
                VALID_TIMETABLE_ID,
                VALID_START_TIME,
                VALID_DURATION,
                VALID_CLASSROOM_ID);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertFalse(result.hasErrors());
    }

    @Test
    void shouldRaiseValidationErrorIfStartTimeIsNotPresent() {
        UpdateTimetableData data = new UpdateTimetableData(
                VALID_TIMETABLE_ID,
                null,
                VALID_DURATION,
                VALID_CLASSROOM_ID);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "startTime", "NotNull");
    }

    @Test
    void shouldRaiseValidationErrorIfStartTimeIsNotInTheFuture() {
        LocalDateTime pastTime = LocalDateTime.now().withHour(9).minusDays(1);

        UpdateTimetableData data = new UpdateTimetableData(
                VALID_TIMETABLE_ID,
                pastTime,
                VALID_DURATION,
                VALID_CLASSROOM_ID);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "startTime", "Future");
    }

    @Test
    void shouldRaiseValidationErrorIfDurationIsNotPresent() {
        UpdateTimetableData data = new UpdateTimetableData(
                VALID_TIMETABLE_ID,
                VALID_START_TIME,
                null,
                VALID_CLASSROOM_ID);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "duration", "NotNull");
    }

    @ParameterizedTest
    @ValueSource(ints = {
            30 - 10,
            90 + 10
    })
    void shouldRaiseValidationErrorIfDurationIsNotWithinValidRange(int invalidDurationValue) {
        UpdateTimetableData data = new UpdateTimetableData(
                VALID_TIMETABLE_ID,
                VALID_START_TIME,
                invalidDurationValue,
                VALID_CLASSROOM_ID);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "duration", "TimetableDuration");
    }

    @Test
    void shouldRaiseValidationErrorsIfRequiredIdentifiersNotPositiveNumbers() {
        UpdateTimetableData data = new UpdateTimetableData(
                0,
                VALID_START_TIME,
                VALID_DURATION,
                -1);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(2));
        assertFieldErrors(result, "timetableId", "Positive");
        assertFieldErrors(result, "classroomId", "Positive");
    }

    @Test
    void shouldRaiseValidationErrorsIfRequiredIdentifiersAreNotPresent() {
        UpdateTimetableData data = new UpdateTimetableData(
                null,
                VALID_START_TIME,
                VALID_DURATION,
                null);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(2));
        assertFieldErrors(result, "timetableId", "NotNull");
        assertFieldErrors(result, "classroomId", "NotNull");
    }

}
