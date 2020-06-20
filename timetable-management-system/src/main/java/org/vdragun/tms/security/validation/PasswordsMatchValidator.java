package org.vdragun.tms.security.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

/**
 * Custom {@link ConstraintValidator} to validate {@link PasswordsMatch}
 * constraint annotation
 * 
 * @author Vitaliy Dragun
 *
 */
public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, Object> {

    private String passwordField;
    private String passwordMatchField;

    @Override
    public void initialize(PasswordsMatch annotation) {
        passwordField = annotation.passwordField();
        passwordMatchField = annotation.passwordMatchField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object passwordValue = new BeanWrapperImpl(value).getPropertyValue(passwordField);
        Object passwordMatchValue = new BeanWrapperImpl(value).getPropertyValue(passwordMatchField);

        if (passwordValue != null) {
            return passwordField.equals(passwordMatchValue);
        } else {
            return passwordMatchValue == null;
        }
    }

}
