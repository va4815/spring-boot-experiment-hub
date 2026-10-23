package com.va4815.springresourcebackend.service;

import com.va4815.springresourcebackend.dto.request.CreateMenuRequestDTO;
import com.va4815.springresourcebackend.dto.request.MenuItemRequestDTO;
import com.va4815.springresourcebackend.dto.request.UpdateMenuItemRequestDTO;
import com.va4815.springresourcebackend.dto.response.MenuItemResponseDTO;
import com.va4815.springresourcebackend.dto.response.MenuResponseDTO;
import com.va4815.springresourcebackend.entity.Menu;
import com.va4815.springresourcebackend.entity.MenuItem;
import com.va4815.springresourcebackend.entity.Restaurant;
import com.va4815.springresourcebackend.exception.GlobalArgumentException;
import com.va4815.springresourcebackend.model.CurrentUser;
import com.va4815.springresourcebackend.repository.MenuRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final CurrentUser currentUser;

    private final RestaurantService restaurantService;
    private final MenuItemService menuItemService;

    public MenuService(MenuRepository menuRepository, CurrentUser currentUser, RestaurantService restaurantService, MenuItemService menuItemService) {
        this.menuRepository = menuRepository;
        this.currentUser = currentUser;
        this.restaurantService = restaurantService;
        this.menuItemService = menuItemService;
    }

    public MenuResponseDTO createMenu(CreateMenuRequestDTO requestDTO) {

        Long restaurantId = requestDTO.restaurantId();
        Set<MenuItemRequestDTO> items = requestDTO.menuItems();

        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);


        Menu menu = new Menu();
        menu.setRestaurant(restaurant);
        menu.setActive(true);

        for (MenuItemRequestDTO item : items) {
            menu.addMenuItem(MenuItemRequestDTO.toMenuItem(item, currentUser));
        }

        menu.setCreatedAt(LocalDateTime.now());
        menu.setUpdatedAt(LocalDateTime.now());
        menu.setCreatedBy(currentUser.getUsername());
        menu.setUpdatedBy(currentUser.getUsername());

        menu = menuRepository.save(menu);

        return MenuResponseDTO.from(menu);
    }

    public MenuItemResponseDTO updateMenuItem(Long menuId, Long itemId, UpdateMenuItemRequestDTO requestDTO) {
        MenuItem updateMenuItem = menuItemService.updateMenuItem(menuId, itemId, requestDTO);

        return MenuItemResponseDTO.from(updateMenuItem);
    }

}
