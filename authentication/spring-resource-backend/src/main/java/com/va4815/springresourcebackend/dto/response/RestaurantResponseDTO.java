package com.va4815.springresourcebackend.dto.response;

import com.va4815.springresourcebackend.entity.Restaurant;

public record RestaurantResponseDTO(
        Long id,
        String name,
        String restaurantType,
        String location
) {

    public RestaurantResponseDTO(Restaurant restaurant) {
        this(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getRestaurantType(),
                restaurant.getLocation()
        );
    }

}
