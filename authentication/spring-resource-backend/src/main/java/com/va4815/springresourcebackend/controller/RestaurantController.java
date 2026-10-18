package com.va4815.springresourcebackend.controller;

import com.va4815.springresourcebackend.dto.response.RestaurantResponseDTO;
import com.va4815.springresourcebackend.entity.Restaurant;
import com.va4815.springresourcebackend.service.RestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping("")
    public List<RestaurantResponseDTO> getRestaurants() {
        return restaurantService.getAllRestaurants().stream().map(RestaurantResponseDTO::new).toList();
    }

    @GetMapping("/{restaurant_id}")
    public ResponseEntity<RestaurantResponseDTO> getRestaurant(@PathVariable("restaurant_id") Long restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);

        RestaurantResponseDTO response = new RestaurantResponseDTO(restaurant);

        return ResponseEntity.ok(response);
    }

}
