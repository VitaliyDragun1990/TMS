package org.vdragun.tms.ui.common.exception;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.vdragun.tms.ui.common.util.Constants.Attribute;
import org.vdragun.tms.ui.common.util.Constants.Message;
import org.vdragun.tms.ui.common.util.Constants.Page;

/**
 * Application error controller
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
public class GlobalErrorController implements ErrorController {

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorController.class);

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object errorStatus = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (errorStatus != null) {
            return showErrorPage(request, model, errorStatus);
        } else {
            // If no error - redirect to home page
            return "redirect:/home";
        }
    }

    private String showErrorPage(HttpServletRequest request, Model model, Object errorStatus) {
        Integer statusCode = Integer.valueOf(errorStatus.toString());

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            log.warn("Handling not found resource, URI:{}", getRequestUrl(request));
            model.addAttribute(Attribute.MESSAGE, getMessage(Message.REQUESTED_RESOURCE, getRequestUrl(request)));

            return Page.NOT_FOUND;
        } else {
            log.error("Handling internal application error, URI:{}", getRequestUrl(request));
            model.addAttribute(Attribute.MESSAGE, getMessage(Message.REQUESTED_RESOURCE, getRequestUrl(request)));

            return Page.SERVER_ERROR;
        }
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    private Object getRequestUrl(HttpServletRequest req) {
        return req.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
    }

    private String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

}
