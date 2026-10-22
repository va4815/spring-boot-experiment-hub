package com.va4815.springresourcebackend.dto.response;

import com.va4815.springresourcebackend.entity.MenuItem;

import java.math.BigDecimal;

public record MenuItemResponseDTO(
        Long id,
        String name,
        String itemType,
        String description,
        BigDecimal price
) {

    public static MenuItemResponseDTO from(MenuItem menuItem) {
        return new MenuItemResponseDTO(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getItemType(),
                menuItem.getDescription(),
                menuItem.getPrice()
        );
    }

}
