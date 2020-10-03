package org.vdragun.tms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vdragun.tms.security.dao.UserDao;
import org.vdragun.tms.security.service.AuthenticatedUserDetailsService;
import org.vdragun.tms.security.service.UserService;
import org.vdragun.tms.security.service.UserServiceImpl;
import org.vdragun.tms.security.web.exception.WebAccessDeniedExceptionHandler;
import org.vdragun.tms.util.localizer.MessageLocalizer;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.vdragun.tms.util.Constants.Role.ADMIN;
import static org.vdragun.tms.util.Constants.Role.TEACHER;

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
    @ConditionalOnProperty(
            name = "secured.web",
            havingValue = "true",
            matchIfMissing = true)
    @Order(1)
    public static class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private PasswordEncoder passwordEncoder;
        
        @Autowired
        private WebAccessDeniedExceptionHandler accessDeniedHandler;

        @Bean
        public WebAccessDeniedExceptionHandler webAccessDeniedExceptionHandler(MessageLocalizer messageLocalizer) {
            return new WebAccessDeniedExceptionHandler(messageLocalizer);
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth
                    .userDetailsService(userDetailsService)
                    .passwordEncoder(passwordEncoder);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    // courses
                    .antMatchers(GET, "/courses/register").hasAuthority(ADMIN)
                    .antMatchers(GET, "/courses", "/courses/**").permitAll()
                    .antMatchers(POST, "/courses").hasAuthority(ADMIN)
                    // students
                    .antMatchers(
                            GET,
                            "/students/register",
                            "/students/*/update"
                            )
                    .hasAuthority(ADMIN)
                    .antMatchers(
                            GET,
                            "/students",
                            "/students/**")
                    .authenticated()
                    .antMatchers(
                            POST,
                            "/students/delete",
                            "/students",
                            "/students/**")
                    .hasAuthority(ADMIN)
                    // teachers
                    .antMatchers(GET, "/teachers/register").hasAnyAuthority(ADMIN)
                    .antMatchers(
                            GET,
                            "/teachers",
                            "/teachers/**")
                    .hasAnyAuthority(ADMIN, TEACHER)
                    .antMatchers(POST, "/teachers").hasAuthority(ADMIN)
                    // timetables
                    .antMatchers(
                            GET,
                            "/timetables/register",
                            "/timetables/*/update"
                            )
                    .hasAuthority(ADMIN)
                    .antMatchers(
                            GET,
                            "/timetables/teacher/**"
                            )
                    .hasAnyAuthority(ADMIN, TEACHER)
                    .antMatchers(
                            GET,
                            "/timetables",
                            "/timetables/*",
                            "/timetables/student/**"
                            )
                    .authenticated()
                    .antMatchers(
                            POST,
                            "/timetables",
                            "/timetables/*",
                            "/timetables/delete")
                    .hasAuthority(ADMIN)
                    .antMatchers("/", "/**", "/auth/signup", "/auth/signin").permitAll()
                    .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                    .and()
                        .formLogin()
                            .loginPage("/auth/signin")
                            .loginProcessingUrl("/auth/signin")
                    .defaultSuccessUrl("/home")
                    .and()
                        .logout()
                            .logoutUrl("/auth/signout")
                    .and()
                        .exceptionHandling().accessDeniedHandler(accessDeniedHandler);
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web
                    .ignoring()
                    .antMatchers(GET, "/css/**", "/js/**", "/webjars/**");
        }
    }
}
