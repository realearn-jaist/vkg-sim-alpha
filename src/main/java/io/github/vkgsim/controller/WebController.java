package io.github.vkgsim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    // Mapping for the home page
    @GetMapping("/")
    public String homePage() {
        return "mainPage";
    }

    // Mapping for the main page
    @GetMapping("/mainPage")
    public String mainPage() {
        return "mainPage";
    }

    // Mapping for the mapping page
    @GetMapping("/mappingPage")
    public String mappingPage() {
        return "mappingPage";
    }

    // Mapping for the query page
    @GetMapping("/queryPage")
    public String queryPage() {
        return "queryPage";
    }

    // Mapping for the error page
    @GetMapping("/error")
    public String errorPage() {
        return "errorPage";
    }
}

