package com.va4815.springresourcebackend.dto.request;

import java.util.Set;

public record CreateMenuRequestDTO (
        Long restaurantId,
        Set<MenuItemRequestDTO> menuItems
) {

}
