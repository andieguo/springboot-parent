package com.andieguo.springboot.controller;

import com.andieguo.springboot.entity.User;
import com.andieguo.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping(path = "/add")
    public @ResponseBody
    User addUser(@RequestParam String name, @RequestParam String email) {
        User n = new User();
        n.setName(name);
        n.setEmail(email);
        return userRepository.save(n);
    }

    @GetMapping("/{id}")
    public User getUserInfo(@PathVariable("id") Integer userId) {
        Optional<User> optional = userRepository.findById(userId);
        return optional.orElseGet(User::new);
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
}
