package org.vdragun.tms.core.application.validation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import org.vdragun.tms.core.application.service.group.GroupData;

@SpringJUnitConfig(classes = ValidationConfig.class)
public class GroupDataValidationTest extends AbstractValidationTest {

    @Autowired
    private Validator validator;

    @Override
    protected Validator getValidator() {
        return validator;
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ps-25",
            "lk-00",
            "sw-60"
    })
    void shouldPassValidationIfGroupNameIsValid(String validNameValue) {
        GroupData data = new GroupData(validNameValue);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertFalse(result.hasErrors());
    }

    @Test
    void shouldRaiseValidationErrorIfGroupNameIsNotPresent() {
        GroupData data = new GroupData(null);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "name", "NotNull");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "PS-25",
            "25-ps",
            "pp-255",
            "ps--2",
            "Ps-25",
            "ps255",
            "пф-25"
    })
    void shouldRaiseValidationErrorIfGroupNameHasInvalidFormat(String invalidNameValue) {
        GroupData data = new GroupData(invalidNameValue);

        DataBinder dataBinder = createDataBinderFor(data);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "name", "GroupName");
    }

}
