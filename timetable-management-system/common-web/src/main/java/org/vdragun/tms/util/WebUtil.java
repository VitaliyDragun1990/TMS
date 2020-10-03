package org.vdragun.tms.util;

import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.vdragun.tms.core.application.exception.ConfigurationException;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Contains web-related helper methods
 * 
 * @author Vitaliy Dragun
 *
 */
public final class WebUtil {

    private WebUtil() {
    }

    public static String getFullRequestUri() {
        ServletUriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequest();
        return decode(uriBuilder.toUriString());
    }

    public static String getRequestUri() {
        HttpServletRequest request = getCurrentRequest();
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();

        if (queryString != null) {
            return requestURI + "?" + decode(queryString);
        } else {
            return requestURI;
        }
    }

    private static HttpServletRequest getCurrentRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        Assert.state(attrs instanceof ServletRequestAttributes, "No current ServletRequestAttributes");
        return ((ServletRequestAttributes) attrs).getRequest();
    }

    private static String decode(String str) {
        try {
            return URLDecoder.decode(str, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new ConfigurationException("Error decoding string: " + str, e);
        }
    }
}
