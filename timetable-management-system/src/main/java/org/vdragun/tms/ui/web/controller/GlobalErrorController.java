package org.vdragun.tms.ui.web.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.vdragun.tms.ui.web.util.Constants.Attribute;
import org.vdragun.tms.ui.web.util.Constants.Message;
import org.vdragun.tms.ui.web.util.Constants.Page;

/**
 * Application error controller
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
public class GlobalErrorController extends AbstractController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object errorStatus = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (errorStatus != null) {
            return showErrorPage(request, model, errorStatus);
        } else {
            // If no error - redirect to home page
            return redirectTo("/home");
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

}
