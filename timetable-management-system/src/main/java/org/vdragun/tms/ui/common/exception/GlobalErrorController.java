package org.vdragun.tms.ui.common.exception;

import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vdragun.tms.ui.common.util.Translator;
import org.vdragun.tms.util.Constants.Attribute;
import org.vdragun.tms.util.Constants.Message;
import org.vdragun.tms.util.Constants.Page;

/**
 * Application error controller
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping("/error")
public class GlobalErrorController extends AbstractErrorController {
    private static final Logger log = LoggerFactory.getLogger(GlobalErrorController.class);

    @Autowired
    private Translator translator;

    public GlobalErrorController(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String showErrorPage(HttpServletRequest request, Model model) {
        HttpStatus status = getStatus(request);
        return showErrorPage(request, model, status);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> provideResponse(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        log.warn("Handling '{}' request for URI:{} with status code:{}",
                status.getReasonPhrase(), getRequestUrl(request), status.value());
        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity<>(status);
        }
        Map<String, Object> body = getErrorAttributes(request, false);
        return new ResponseEntity<>(body, status);
    }

    private String showErrorPage(HttpServletRequest request, Model model, HttpStatus status) {
        if (status == HttpStatus.NOT_FOUND) {
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
        return translator.getLocalizedMessage(code, args);
    }

}
