package org.vdragun.tms.security.web.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.vdragun.tms.security.model.Role;
import org.vdragun.tms.security.validation.SignupFormPasswordsMatchValidator;
import org.vdragun.tms.security.web.service.SignupForm;
import org.vdragun.tms.security.web.service.WebAuthenticationService;
import org.vdragun.tms.util.Constants.Attribute;
import org.vdragun.tms.util.Constants.Page;

/**
 * Processes student registration requests
 * 
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping(AuthenticationController.BASE_URL)
public class AuthenticationController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

    public static final String BASE_URL = "/auth";

    private WebAuthenticationService authService;
    private SignupFormPasswordsMatchValidator formValidator;

    public AuthenticationController(
            WebAuthenticationService authService,
            SignupFormPasswordsMatchValidator formValidator) {
        this.authService = authService;
        this.formValidator = formValidator;
    }

    @InitBinder
    void sharedInitBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @InitBinder(Attribute.SIGN_UP_FORM)
    void signupInitBinder(WebDataBinder binder) {
        binder.addValidators(formValidator);
    }

    @ModelAttribute(Attribute.ROLES)
    public List<Role> allRoles() {
        return authService.findRols();
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        LOG.trace("Received GET request to show user sign up form, URI: {}", getRequestUri());

        model.addAttribute(Attribute.SIGN_UP_FORM, new SignupForm());

        return Page.SIGN_UP_FORM;
    }

    @PostMapping("/signup")
    public String signupNewUser(
            @Valid @ModelAttribute(Attribute.SIGN_UP_FORM) SignupForm form,
            BindingResult bindingResult,
            Model model) {
        LOG.trace("Received POST request to register new user, data: {}, URI: {}", form, getRequestUri());

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> LOG.trace("Validation error: {}", error));

            model.addAttribute(Attribute.VALIDATED, true);

            return Page.SIGN_UP_FORM;
        }

        authService.processSignUp(form);

        return redirectTo("/auth/signin");
    }

    protected String getRequestUri() {
        ServletUriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequest();
        return uriBuilder.toUriString();
    }

    protected String redirectTo(String targetURI) {
        return "redirect:" + targetURI;
    }
}
