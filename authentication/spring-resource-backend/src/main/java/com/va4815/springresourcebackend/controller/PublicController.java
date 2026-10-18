package com.va4815.springresourcebackend.controller;

import com.va4815.springresourcebackend.service.RestaurantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/public")
public class PublicController {

    private final RestaurantService restaurantService;

    public PublicController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping("/greeting")
    public String hello() {
        return "Greeting";
    }

}
