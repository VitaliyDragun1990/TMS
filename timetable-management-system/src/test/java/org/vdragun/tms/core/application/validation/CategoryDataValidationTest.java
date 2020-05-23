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
import org.vdragun.tms.core.application.service.category.CategoryData;


@SpringJUnitConfig(classes = ValidationConfig.class)
public class CategoryDataValidationTest extends AbstractValidationTest {

    private static final String VALID_CODE = "ENG";

    @Autowired
    private Validator validator;

    @Override
    protected Validator getValidator() {
        return validator;
    }

    @Test
    void shouldRaiseValidationErrorIfCodeIsNotPresent() {
        CategoryData categoryData = new CategoryData();
        categoryData.setCode(null);

        DataBinder dataBinder = createDataBinderFor(categoryData);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "code", "NotNull");
    }

    @ParameterizedTest
    @ValueSource(strings = { "abd", "ABc", "1AB", "   ", "", "A1B", "ФЙЦ" })
    void shouldRiseValidationErrorIfCodeDoesNotConformToRequiredStandard(String invaliedCodeValue) {
        CategoryData categoryData = new CategoryData();
        categoryData.setCode(invaliedCodeValue);

        DataBinder dataBinder = createDataBinderFor(categoryData);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "code", "CategoryCode");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Hello=World",
            "(Hello World)",
            "",
            "Это предложение не пройдет валидацию."
    })
    void shouldRiseValidationErrorIfDescriptionIsNotLatinSentence(String invalidDescriptionValue) {
        CategoryData categoryData = new CategoryData();
        categoryData.setCode(VALID_CODE);
        categoryData.setDescription(invalidDescriptionValue);

        DataBinder dataBinder = createDataBinderFor(categoryData);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertThat(result.getAllErrors().size(), equalTo(1));
        assertFieldErrors(result, "description", "LatinSentence");
    }

    @Test
    void shouldPassValidationIfCodeIsValid() {
        CategoryData categoryData = new CategoryData();
        categoryData.setCode(VALID_CODE);

        DataBinder dataBinder = createDataBinderFor(categoryData);
        dataBinder.validate();

        BindingResult result = dataBinder.getBindingResult();
        assertFalse(result.hasErrors());
    }

}
