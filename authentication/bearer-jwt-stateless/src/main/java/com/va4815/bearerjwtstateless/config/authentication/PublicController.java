package com.va4815.bearerjwtstateless.config.authentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {

    @GetMapping("/greeting")
    public String greeting() {
        return "Hello, World!";
    }

}
