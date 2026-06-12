package com.sharepad.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Catches any route not matched by a REST controller and forwards to index.html.
 * This enables SPA-style client-side routing (e.g. refreshing on /#my-note).
 */
@Controller
public class FallbackController {

    @RequestMapping(value = { "/", "/{path:[^\\.]*}", "/{path:^(?!api|ws).*$}/**" })
    public String forward() {
        return "forward:/index.html";
    }
}
