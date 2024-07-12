package io.github.vkgsim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String homePage() {
        return "mainPage";
    }

    @GetMapping("/mainPage")
    public String mainPage() {
        return "mainPage";
    }

    @GetMapping("/mappingPage")
    public String mappingPage() {
        return "mappingPage";
    }

    @GetMapping("/queryPage")
    public String queryPage() {
        return "queryPage";
    }

    @GetMapping("/error")
    public String errorPage() {
        return "errorPage";
    }
}

