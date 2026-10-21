package com.va4815.springresourcebackend.service;

import com.va4815.springresourcebackend.dto.request.CreateRestaurantRequestDTO;
import com.va4815.springresourcebackend.entity.Restaurant;
import com.va4815.springresourcebackend.exception.GlobalArgumentException;
import com.va4815.springresourcebackend.model.CurrentUser;
import com.va4815.springresourcebackend.repository.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final CurrentUser currentUser;

    public RestaurantService(RestaurantRepository restaurantRepository, CurrentUser currentUser) {
        this.restaurantRepository = restaurantRepository;
        this.currentUser = currentUser;
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id).orElse(null);
    }

    public Restaurant createRestaurant(CreateRestaurantRequestDTO requestDTO) {

        if (restaurantRepository.existsByName(requestDTO.name())) {
            throw new GlobalArgumentException(HttpStatus.BAD_REQUEST, "Restaurant with the same name already exists");
        }

        Restaurant restaurant = new Restaurant();
        restaurant.setName(requestDTO.name());
        restaurant.setRestaurantType(requestDTO.restaurantType().name());
        restaurant.setLocation(requestDTO.location());

        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant.setUpdatedAt(LocalDateTime.now());
        restaurant.setCreatedBy(currentUser.getUsername());
        restaurant.setUpdatedBy(currentUser.getUsername());

        return restaurantRepository.save(restaurant);
    }

}
