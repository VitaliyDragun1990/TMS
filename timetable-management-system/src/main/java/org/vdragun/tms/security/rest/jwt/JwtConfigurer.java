package org.vdragun.tms.security.rest.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.vdragun.tms.security.rest.filter.JwtAuthenticationTokenFilter;

/**
 * JWT configuration for application that adds {@link JwtAuthenticationTokenFilter} to
 * security chain.
 * 
 * @author Vitaliy Dragun
 *
 */
public class JwtConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private JwtAuthenticationTokenFilter jwtTokenFilter;

    public JwtConfigurer(JwtAuthenticationTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
