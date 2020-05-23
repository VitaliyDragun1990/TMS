package org.vdragun.tms.core.application.validation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import org.vdragun.tms.core.application.service.classroom.ClassroomData;

@SpringJUnitConfig(classes = ValidationConfig.class)
public class ClassroomDataValidationTest extends AbstractValidationTest {

    private static final int CAPACITY_INNER_BOUND = 30;
    private static final int CAPACITY_UPPER_BOUND = 60;

    @Autowired
    private Validator validator;

    @Override
    protected Validator getValidator() {
        return validator;
    }

    @Test
    void shouldRaiseValidationErrorIfCapacityIsNotPresent() {
        ClassroomData data = new ClassroomData(null);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "capacity", "NotNull");
    }

    @ParameterizedTest
    @ValueSource(ints = {
            CAPACITY_INNER_BOUND - 1,
            CAPACITY_UPPER_BOUND + 1
    })
    void shouldRaiseValidationErrorIfCapacityNotWithinRequiredRange(int invalidCapacityValue) {
        ClassroomData data = new ClassroomData(invalidCapacityValue);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "capacity", "ClassroomCapacity");
    }

}
