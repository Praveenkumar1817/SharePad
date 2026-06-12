package com.sharepad.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Catches any non-asset, non-API route and forwards to index.html.
 * Excluded: /api/**, /ws/**, /js/**, /css/**, and any path containing a dot (file extension).
 */
@Controller
public class FallbackController {

    // Only match extension-less paths that are NOT api, ws, js, css, or favicon
    @RequestMapping(value = {
        "/",
        "/{path:^(?!api|ws|js|css|favicon)[^\\.]*$}",
        "/{path:^(?!api|ws|js|css|favicon)[^\\.]*$}/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
