package com.dt.employ_chatbot.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController implements ErrorController {
    @GetMapping({"/", "/error"})
    public String index() {
        return "index.html";
    }
}

