package com.andieguo.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @RequestMapping("/home")
    @ResponseBody
    public String home() {
        return "Stay hungry, stay foolish";
    }
}
