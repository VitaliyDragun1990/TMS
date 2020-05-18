package org.vdragun.tms.core.application.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
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
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Pattern(regexp = "^[A-Z]{1}[a-z]+$")
@Size(min = 2, max = 50)
@ReportAsSingleViolation
@Documented
@Constraint(validatedBy = {})
public @interface PersonName {

    String message() default "{org.vdragun.tms.core.application.validation.PersonName.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
