package com.andieguo.springboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    Logger logger = LoggerFactory.getLogger(HomeController.class);
    Logger webLogger = LoggerFactory.getLogger("webLogger");

    @RequestMapping("/home")
    @ResponseBody
    public String home(){
        logger.info("access home page,print applogger!");
        webLogger.info("access home page,print webLogger");
        return "HELLO SPRINT BOOT!";
    }
}
