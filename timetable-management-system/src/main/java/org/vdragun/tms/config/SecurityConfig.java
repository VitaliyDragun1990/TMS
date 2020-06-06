package org.vdragun.tms.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.vdragun.tms.security.rest.exception.AccessDeniedExceptionHandler;
import org.vdragun.tms.security.rest.exception.RestAuthenticationEntryPoint;
import org.vdragun.tms.security.rest.jwt.JwtConfigurer;
import org.vdragun.tms.security.rest.jwt.JwtAuthenticationTokenFilter;
import org.vdragun.tms.security.rest.jwt.JwtTokenProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Configuration
    @Order(1)
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
                    .antMatchers(HttpMethod.GET).permitAll()
                    .antMatchers(HttpMethod.POST).hasAnyRole("USER", "ADMIN")
                    .antMatchers(HttpMethod.PUT).hasAnyRole("USER", "ADMIN")
                    .antMatchers(HttpMethod.DELETE).hasRole("ADMIN")
                    .anyRequest().authenticated()
                    .and().exceptionHandling()
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authEntryPoint)
                    .and().sessionManagement().sessionCreationPolicy(STATELESS)
                    .and().apply(new JwtConfigurer(new JwtAuthenticationTokenFilter(jwtTokenProvider, authEntryPoint)));
        }
    }

    @Configuration
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
                    .antMatchers(HttpMethod.GET, "/css/**", "/js/**", "/webjars/**");
        }
    }
}
