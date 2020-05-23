package org.vdragun.tms.core.application.validation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

/**
 * @author Vitaliy Dragun
 *
 */
abstract class AbstractValidationTest {

    protected abstract Validator getValidator();

    protected DataBinder createDataBinderFor(Object target) {
        DataBinder dataBinder = new DataBinder(target);
        dataBinder.addValidators(getValidator());
        return dataBinder;
    }

    protected void assertFieldErrors(BindingResult result, String fieldName, String... errorCodes) {
        assertThat(result.getFieldErrorCount(fieldName), equalTo(errorCodes.length));
        List<FieldError> fieldErrors = result.getFieldErrors(fieldName);
        for (String errorCode : errorCodes) {
            assertThat(fieldErrors, hasItem(hasProperty("code", equalTo(errorCode))));
        }
    }
}
