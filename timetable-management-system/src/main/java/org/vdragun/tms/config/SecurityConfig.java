package org.vdragun.tms.config;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.vdragun.tms.security.rest.exception.AccessDeniedExceptionHandler;
import org.vdragun.tms.security.rest.exception.RestAuthenticationEntryPoint;
import org.vdragun.tms.security.rest.jwt.JwtAuthenticationTokenFilter;
import org.vdragun.tms.security.rest.jwt.JwtConfigurer;
import org.vdragun.tms.security.rest.jwt.JwtTokenProvider;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Configuration
    @Order(1)
    @Profile("!test")
    public static class RestWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

        private static final String LOGIN_ENDPOINT = "/api/v1/auth/signin";
        private static final String REGISTER_ENDPOINT = "/api/v1/auth/signup";

        @Autowired
        private JwtTokenProvider jwtTokenProvider;

        @Autowired
        private AccessDeniedExceptionHandler accessDeniedHandler;
        
        @Autowired
        private RestAuthenticationEntryPoint authEntryPoint;

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
                    .antMatchers(REGISTER_ENDPOINT, LOGIN_ENDPOINT).permitAll()
                    // courses
                    .antMatchers(GET, "/api/v1/courses", "/api/v1/courses/**").authenticated()
                    .antMatchers(POST, "/api/v1/courses").hasRole("ADMIN")
                    // students
                    .antMatchers(GET, "/api/v1/students", "/api/v1/students/**").hasAnyRole("ADMIN", "TEACHER")
                    .antMatchers(POST, "/api/v1/students").hasRole("ADMIN")
                    .antMatchers(PUT, "/api/v1/students/**").hasRole("ADMIN")
                    .antMatchers(DELETE, "/api/v1/students/**").hasRole("ADMIN")
                    // teachers
                    .antMatchers(GET, "/api/v1/teachers", "/api/v1/teachers/**").hasAnyRole("ADMIN", "TEACHER")
                    .antMatchers(POST, "/api/v1/teachers").hasRole("ADMIN")
                    // timetables
                    .antMatchers(GET, "/api/v1/timetables", "/api/v1/timetables/**").authenticated()
                    .antMatchers(POST, "/api/v1/timetables").hasRole("ADMIN")
                    .antMatchers(PUT, "/api/v1/timetables/**").hasRole("ADMIN")
                    .antMatchers(DELETE, "/api/v1/timetables/**").hasRole("ADMIN")
                     // other
                    .anyRequest().authenticated()
                    .and().exceptionHandling()
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authEntryPoint)
                    .and().sessionManagement().sessionCreationPolicy(STATELESS)
                    .and().apply(new JwtConfigurer(new JwtAuthenticationTokenFilter(jwtTokenProvider, authEntryPoint)));
        }
    }

    @Configuration
    @Order(2)
    public static class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/**")
                    .csrf()
                    .disable()
                    .authorizeRequests()
                    .anyRequest()
                    .permitAll();
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web
                    .ignoring()
                    .antMatchers(GET, "/css/**", "/js/**", "/webjars/**");
        }
    }
}
