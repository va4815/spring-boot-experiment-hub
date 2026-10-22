package com.va4815.springresourcebackend.dto.response;

import com.va4815.springresourcebackend.entity.Menu;

import java.util.Set;
import java.util.stream.Collectors;

public record MenuResponseDTO(
        Long id,
        Long restaurantId,
        Set<MenuItemResponseDTO> menuItems
) {
    public static MenuResponseDTO from(Menu menu) {
        return new MenuResponseDTO(
                menu.getId(),
                menu.getRestaurant().getId(),
                menu.getMenuItems().stream()
                        .map(MenuItemResponseDTO::from)
                        .collect(Collectors.toSet())
        );
    }
}
