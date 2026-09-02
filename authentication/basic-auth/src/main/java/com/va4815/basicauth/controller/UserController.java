package com.va4815.basicauth.controller;

import com.va4815.basicauth.entity.User;
import com.va4815.basicauth.service.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private MyUserDetailService myUserDetailService;

    @PostMapping
    public User createUser(@RequestBody User user) {
        return myUserDetailService.createuser(user);
    }

}
