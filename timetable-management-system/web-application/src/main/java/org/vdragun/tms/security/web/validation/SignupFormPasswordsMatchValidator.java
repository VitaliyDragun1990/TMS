package org.vdragun.tms.security.web.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.vdragun.tms.security.web.service.SignupForm;

/**
 * Custom validator that checks that specified password values match
 * 
 * @author Vitaliy Dragun
 *
 */
@Component
public class SignupFormPasswordsMatchValidator implements Validator {

    private static final String ERROR_CODE = "PasswordsMatch";
    private static final String FIELD_CONFIRM_PASSWORD = "confirmPassword";
    private static final String FIELD_PASSWORD = "password";

    @Override
    public boolean supports(Class<?> clazz) {
        return SignupForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignupForm form = (SignupForm) target;

        String passwordValue = form.getPassword();
        String confirmPasswordValue = form.getConfirmPassword();

        if (passwordsDontMatch(passwordValue, confirmPasswordValue)) {
            errors.rejectValue(FIELD_PASSWORD, ERROR_CODE);
            errors.rejectValue(FIELD_CONFIRM_PASSWORD, ERROR_CODE);
        }
    }

    private boolean passwordsDontMatch(String password, String confirmPassword) {
        return (password != null && !password.equals(confirmPassword)) ||
                (confirmPassword != null && !confirmPassword.equals(password));
    }

}
