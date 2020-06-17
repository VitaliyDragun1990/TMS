package org.vdragun.tms.security.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vdragun.tms.security.dao.RoleDao;
import org.vdragun.tms.security.web.service.SigninForm;
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
    private RoleDao roleDao;

    public AuthenticationController(WebAuthenticationService authService, RoleDao roleDao) {
        this.authService = authService;
        this.roleDao = roleDao;
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute(Attribute.USER, new SignupForm());
        model.addAttribute(Attribute.ROLES, roleDao.findAll());

        return Page.SIGN_UP_FORM;
    }

    @PostMapping("/signup")
    public String signupNewUser(@ModelAttribute(Attribute.USER) SignupForm form, Model model) {
        LOG.info("SignupForm: {}", form);

        return redirectTo("/home");
    }

    @GetMapping("/signin")
    public String showSigninForm(Model model) {
        model.addAttribute(Attribute.USER, new SigninForm());

        return Page.SIGN_IN_FORM;
    }

    @PostMapping("/signin")
    public String signInUser(SigninForm form) {
        LOG.info("SigninForm: {}", form);

        return redirectTo("/home");
    }

    protected String redirectTo(String targetURI) {
        return "redirect:" + targetURI;
    }
}
