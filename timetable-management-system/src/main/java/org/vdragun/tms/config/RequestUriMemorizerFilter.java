package org.vdragun.tms.config;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.vdragun.tms.util.Constants;
import org.vdragun.tms.util.Constants.Attribute;

/**
 * Saves request URI with query string if any as request attribute {@link Constants.Attribute#REQUEST_URI}
 * 
 * @author Vitaliy Dragun
 *
 */
public class RequestUriMemorizerFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestUriMemorizerFilter.class);

    private static final List<String> STATIC_RESOURCES = Arrays.asList("/static", "/css", "/js", "/webjars");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestUri = getRequestUri(request);

        if (notStaticResourceRequest(requestUri)) {
            request.setAttribute(Attribute.REQUEST_URI, requestUri);
            LOG.trace("Request URI: {}", requestUri);
        }
        
        filterChain.doFilter(request, response);
    }

    private boolean notStaticResourceRequest(String requestUri) {
        return STATIC_RESOURCES.stream()
                .noneMatch(requestUri::startsWith);
    }

    private String getRequestUri(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();

        if (queryString != null) {
            return requestURI + "?" + decode(queryString);
        } else {
            return requestURI;
        }
    }

    private String decode(String str) {
        try {
            return URLDecoder.decode(str, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new ConfigurationException("Error decoding string: " + str, e);
        }
    }

}
