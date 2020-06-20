package org.vdragun.tms.security.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author Vitaliy Dragun
 *
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { PasswordsMatchValidator.class })
public @interface PasswordsMatch {

    String message() default "{org.vdragun.tms.security.validation.PasswordsMatch.message}";

    String passwordField() default "password";

    String passwordMatchField() default "confirmPassword";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {

    }
}
