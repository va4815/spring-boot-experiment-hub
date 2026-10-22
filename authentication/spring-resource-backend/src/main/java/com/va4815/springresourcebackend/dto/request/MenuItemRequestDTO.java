package com.va4815.springresourcebackend.dto.request;

import com.va4815.springresourcebackend.entity.MenuItem;
import com.va4815.springresourcebackend.model.CurrentUser;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public record MenuItemRequestDTO(
        String name,
        String description,
        String itemType,
        String category,

        @DecimalMin("0.00")
        @Digits(integer = 10, fraction = 2)
        BigDecimal price
) {

    public static MenuItem toMenuItem(MenuItemRequestDTO requestDTO, CurrentUser currentUser) {
        Objects.requireNonNull(requestDTO, "request must not be null");

        MenuItem menuItem = new MenuItem();
        menuItem.setName(requestDTO.name());
        menuItem.setDescription(requestDTO.description());
        menuItem.setItemType(requestDTO.itemType());
        menuItem.setCategory(requestDTO.category());
        menuItem.setPrice(requestDTO.price());

        menuItem.setCreatedAt(LocalDateTime.now());
        menuItem.setUpdatedAt(LocalDateTime.now());
        menuItem.setCreatedBy(currentUser.getUsername());
        menuItem.setUpdatedBy(currentUser.getUsername());

        return menuItem;
    }

}
