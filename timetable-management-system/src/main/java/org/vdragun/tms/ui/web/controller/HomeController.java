package org.vdragun.tms.ui.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vdragun.tms.ui.web.util.Constants.Page;

/**
 * @author Vitaliy Dragun
 *
 */
@Controller
@RequestMapping({ "/home", "/" })
public class HomeController extends AbstractController {

    @GetMapping
    public String showIndexPage() {
        log.trace("Received GET request to show home page, URI={}", getRequestUri());

        return Page.HOME;
    }
}