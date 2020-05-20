package org.vdragun.tms.ui.web.controller;

import java.time.LocalDate;
import java.time.Month;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.vdragun.tms.ui.common.util.Translator;

/**
 * Parent class with common functionality for all application controllers.
 * 
 * @author Vitaliy Dragun
 *
 */
public abstract class AbstractController {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private Translator translator;

    protected String getMessage(String code, Object... args) {
        return translator.getLocalizedMessage(code, args);
    }

    protected String formatDate(LocalDate date) {
        return translator.formatDate(date);
    }

    protected String formatMonth(Month month) {
        return translator.formatMonth(month);
    }

    protected String getRequestUri() {
        ServletUriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequest();
        return uriBuilder.toUriString();
    }

    protected String redirectTo(String targetURI) {
        return "redirect:" + targetURI;
    }

}
