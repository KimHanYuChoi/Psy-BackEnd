package com.sentinels.psychology.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class TestController {

    @GetMapping("/admin")
    public String adminP() {
        return "admin Controller";
    }
}
