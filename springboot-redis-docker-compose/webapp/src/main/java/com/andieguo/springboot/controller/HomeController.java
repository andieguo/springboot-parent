package com.andieguo.springboot.controller;

import com.andieguo.springboot.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("/")
    @ResponseBody
    public String index(){
        redisUtil.incr("hello",1);
        Object hello = redisUtil.get("hello");
        return "That's one small step for man,one giant leap for mankind," + hello;
    }

    @RequestMapping("/home")
    @ResponseBody
    public String home(){
        return "Stay hungry, stay foolish";
    }
}
