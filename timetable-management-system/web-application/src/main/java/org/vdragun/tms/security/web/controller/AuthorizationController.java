package org.vdragun.tms.security.web.controller;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.vdragun.tms.config.WebConstants.Attribute;
import org.vdragun.tms.config.WebConstants.View;

@Controller
public class AuthorizationController {

    @GetMapping("/403")
    public String showAccessDeniedPage(Model model, HttpSession session, HttpServletResponse resp) {
        Object accessDeniedMsg = session.getAttribute(Attribute.ACCESS_DENIED_MSG);

        // if message present - show 403 page
        if (accessDeniedMsg != null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            model.addAttribute(Attribute.MESSAGE, accessDeniedMsg);
            session.removeAttribute(Attribute.ACCESS_DENIED_MSG);

            return View.ACCESS_DENIED;
        }

        // else redirect to home page
        return "redirect:/";

    }
}
