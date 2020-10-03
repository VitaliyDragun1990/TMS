package org.vdragun.tms.config;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.vdragun.tms.security.service.AuthenticatedUser;

/**
 * Custom annotation that uses {@link WithSecurityContext} to create
 * {@link SecurityContext} already populated with {@link AuthenticatedUser}
 * 
 * @author Vitaliy Dragun
 *
 */
@Retention(RUNTIME)
@WithSecurityContext(factory = WithMockAuthenticatedUserSecurityContextFactory.class)
public @interface WithMockAuthenticatedUser {

    String username() default "john";

    String firstName() default "John";

    String lastName() default "Smith";

    String password() default "password";

    String email() default "john@gmail.com";

    String[] roles() default { "ROLE_ADMIN" };
}
