package com.andieguo.springboot.controller;

import com.andieguo.springboot.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Controller
public class HomeController {

    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("/")
    @ResponseBody
    public String index() throws UnknownHostException {
        redisUtil.incr("times",1);
        Object times = redisUtil.get("times");
        InetAddress inetAddress = InetAddress.getLocalHost();
        StringBuilder content = new StringBuilder();
        content.append("the current page ipAddress is ").append(inetAddress.getHostAddress()).append("\n")
                .append("the current page has been accessed ").append(times).append(" times");

        return content.toString();
    }

    @RequestMapping("/home")
    @ResponseBody
    public String home(){
        return "Stay hungry, stay foolish";
    }
}
