package org.vdragun.tms.ui.web.controller;

import java.time.LocalDate;
import java.time.Month;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    protected String redirectTo(String targetURI) {
        return "redirect:" + targetURI;
    }

}
