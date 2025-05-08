package com.dt.employ_chatbot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {

    @RequestMapping(value = { "/", "/{path:^(?!api|static|favicon\\.ico|logo192\\.png|manifest\\.json$).*$}" })
    public String forward() {
        return "/index.html";
    }
}

