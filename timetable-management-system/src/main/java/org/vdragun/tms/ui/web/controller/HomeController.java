package org.vdragun.tms.ui.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping({ "/home", "/" })
public class HomeController {

    @GetMapping
    public String showIndexPage() {
        return "index";
    }
}
