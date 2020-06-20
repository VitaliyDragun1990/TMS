package org.vdragun.tms.ui.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.vdragun.tms.config.ConfigurationException;
import org.vdragun.tms.util.localizer.MessageLocalizer;
import org.vdragun.tms.util.localizer.TemporalLocalizer;

/**
 * Parent class with common functionality for all application controllers.
 * 
 * @author Vitaliy Dragun
 *
 */
public abstract class AbstractController {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageLocalizer messageLocalizer;

    @Autowired
    private TemporalLocalizer temporalLocalizer;

    protected String getMessage(String code, Object... args) {
        return messageLocalizer.getLocalizedMessage(code, args);
    }

    protected String formatDate(LocalDate date) {
        return temporalLocalizer.localizeDate(date);
    }

    protected String formatMonth(Month month) {
        return temporalLocalizer.localizeMonth(month);
    }

    protected String getRequestUri() {
        ServletUriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequest();
        return uriBuilder.toUriString();
    }

    protected String redirectTo(String targetURI) {
        return "redirect:" + targetURI;
    }

    protected String buildPageTemplateUrl(String baseUrl, QueryParam... params) {
        String result = baseUrl + "?";

        for (QueryParam param : params) {
            result += param.keyValue() + "&";
        }

        return result;
    }

    public static class QueryParam {
        public final String name;
        public final String value;

        public QueryParam(String name, String value) {
            this.name = name;
            try {
                this.value = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                throw new ConfigurationException("Error creating query param", e);
            }
        }

        public static QueryParam from(String name, String value) {
            return new QueryParam(name, value);
        }

        public String keyValue() {
            return name + "=" + value;
        }

    }

}
