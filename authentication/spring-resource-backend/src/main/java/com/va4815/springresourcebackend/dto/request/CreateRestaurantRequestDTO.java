package com.va4815.springresourcebackend.dto.request;

import com.va4815.springresourcebackend.enumeration.RestaurantType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRestaurantRequestDTO(
        @NotBlank
        String name,

        @NotNull
        RestaurantType restaurantType,

        @NotBlank
        String location
) {
}
