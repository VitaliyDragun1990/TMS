package org.vdragun.tms.security.rest.validation;

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

/**
 * @author Vitaliy Dragun
 *
 */
@Target({ METHOD, FIELD, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { ValidRoleConstraintValidator.class })
public @interface ValidRole {

    String message() default "{org.vdragun.tms.security.rest.validation.ValidRole.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
