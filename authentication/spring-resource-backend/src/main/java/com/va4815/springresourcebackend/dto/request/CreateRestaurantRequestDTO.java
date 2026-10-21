package com.va4815.springresourcebackend.dto.request;

import com.va4815.springresourcebackend.enumeration.RestaurantType;

public record CreateRestaurantRequestDTO(
        String name,
        RestaurantType restaurantType,
        String location
) {
}
