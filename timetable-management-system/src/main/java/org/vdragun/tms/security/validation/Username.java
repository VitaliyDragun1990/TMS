package org.vdragun.tms.security.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Vitaliy Dragun
 *
 */
@Target({ METHOD, FIELD, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Size(min = 5, max = 25)
@Pattern(regexp = "^[A-Za-z]{1}[A-Za-z0-9]+$")
@ReportAsSingleViolation
@Documented
@Constraint(validatedBy = {})
public @interface Username {

    String message() default "{org.vdragun.tms.security.validation.Username.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
