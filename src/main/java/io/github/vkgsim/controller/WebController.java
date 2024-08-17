package io.github.vkgsim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    // Handle all routes except those containing a dot (i.e., exclude static resources)
    @RequestMapping(value = "/**/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/index.html";
    }
}