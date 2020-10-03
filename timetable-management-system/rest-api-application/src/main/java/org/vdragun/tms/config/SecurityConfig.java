package org.vdragun.tms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.vdragun.tms.security.dao.UserDao;
import org.vdragun.tms.security.rest.exception.RestAccessDeniedExceptionHandler;
import org.vdragun.tms.security.rest.exception.RestAuthenticationEntryPoint;
import org.vdragun.tms.security.rest.filter.JwtAuthenticationTokenFilter;
import org.vdragun.tms.security.rest.jwt.JwtConfigurer;
import org.vdragun.tms.security.rest.jwt.JwtTokenProvider;
import org.vdragun.tms.security.service.AuthenticatedUserDetailsService;
import org.vdragun.tms.security.service.UserService;
import org.vdragun.tms.security.service.UserServiceImpl;
import org.vdragun.tms.util.localizer.MessageLocalizer;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.vdragun.tms.config.Constants.Role.ADMIN;
import static org.vdragun.tms.config.Constants.Role.TEACHER;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticatedUserDetailsService userDetailsService(UserService userService) {
        return new AuthenticatedUserDetailsService(userService);
    }

    @Bean
    public UserServiceImpl userService(UserDao userDao) {
        return new UserServiceImpl(userDao);
    }

    @Configuration
    @Order(1)
    @ConditionalOnProperty(
            name = "secured.rest",
            havingValue = "true",
            matchIfMissing = true)
    public static class RestWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

        private static final String LOGIN_ENDPOINT = "/api/v1/auth/signin";
        private static final String REGISTER_ENDPOINT = "/api/v1/auth/signup";
        private static final String CHECK_ENDPOINTS = "/api/v1/auth/check/*";

        @Autowired
        private JwtTokenProvider jwtTokenProvider;

        @Autowired
        private RestAccessDeniedExceptionHandler accessDeniedHandler;
        
        @Autowired
        private RestAuthenticationEntryPoint authEntryPoint;

        @Bean
        public RestAccessDeniedExceptionHandler restAccessDeniedExceptionHandler(
                ObjectMapper mapper,
                MessageLocalizer messageLocalizer) {
            return new RestAccessDeniedExceptionHandler(mapper, messageLocalizer);
        }

        @Bean
        public RestAuthenticationEntryPoint restAuthenticationEntryPoint(
                ObjectMapper mapper,
                MessageLocalizer messageLocalizer) {
            return new RestAuthenticationEntryPoint(mapper, messageLocalizer);
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/api/**")
                    .httpBasic().disable()
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers(REGISTER_ENDPOINT, LOGIN_ENDPOINT, CHECK_ENDPOINTS).permitAll()
                    // courses
                    .antMatchers(GET, "/api/v1/courses", "/api/v1/courses/**")
                    .permitAll()
                    // dictionary resources
                    .antMatchers(GET, "/api/v1/dictionary/*").permitAll()
                    .antMatchers(POST, "/api/v1/courses").hasAuthority(ADMIN)
                    // students
                    .antMatchers(GET, "/api/v1/students", "/api/v1/students/**")
                    .authenticated()
                    .antMatchers(POST, "/api/v1/students").hasAuthority(ADMIN)
                    .antMatchers(PUT, "/api/v1/students/**").hasAuthority(ADMIN)
                    .antMatchers(DELETE, "/api/v1/students/**").hasAuthority(ADMIN)
                    // teachers
                    .antMatchers(GET, "/api/v1/teachers", "/api/v1/teachers/**").hasAnyAuthority(ADMIN, TEACHER)
                    .antMatchers(POST, "/api/v1/teachers").hasAuthority(ADMIN)
                    // timetables
                    .antMatchers(GET, "/api/v1/timetables/teacher/**").hasAnyAuthority(ADMIN, TEACHER)
                    .antMatchers(
                            GET,
                            "/api/v1/timetables",
                            "/api/v1/timetables/*",
                            "/api/v1/timetables/student/**").authenticated()
                    .antMatchers(POST, "/api/v1/timetables").hasAuthority(ADMIN)
                    .antMatchers(PUT, "/api/v1/timetables/**").hasAuthority(ADMIN)
                    .antMatchers(DELETE, "/api/v1/timetables/**").hasAuthority(ADMIN)
                     // other
                    .anyRequest().authenticated()
                    .and()
                        .exceptionHandling()
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authEntryPoint)
                    .and()
                        .sessionManagement().sessionCreationPolicy(STATELESS)
                    .and()
                        .cors()
                    .and()
                        .apply(new JwtConfigurer(new JwtAuthenticationTokenFilter(jwtTokenProvider, authEntryPoint)));
        }
    }

}
