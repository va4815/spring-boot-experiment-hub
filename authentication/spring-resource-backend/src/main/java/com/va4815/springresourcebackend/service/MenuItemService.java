package com.va4815.springresourcebackend.service;

import com.va4815.springresourcebackend.dto.request.UpdateMenuItemRequestDTO;
import com.va4815.springresourcebackend.entity.MenuItem;
import com.va4815.springresourcebackend.exception.GlobalArgumentException;
import com.va4815.springresourcebackend.model.CurrentUser;
import com.va4815.springresourcebackend.repository.MenuItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final CurrentUser currentUser;

    public MenuItemService(MenuItemRepository menuItemRepository, CurrentUser currentUser) {
        this.menuItemRepository = menuItemRepository;
        this.currentUser = currentUser;
    }

    public MenuItem updateMenuItem(Long menuId, Long itemId, UpdateMenuItemRequestDTO requestDTO) {
        MenuItem menuItem = menuItemRepository.findByIdAndMenuId(itemId, menuId).orElseThrow(() -> new GlobalArgumentException(HttpStatus.NOT_FOUND, "Item not found"));

        if (requestDTO.getName() != null && !requestDTO.getName().isEmpty()) {
            menuItem.setName(requestDTO.getName());
        }
        if (requestDTO.getDescription() != null && !requestDTO.getDescription().isEmpty()) {
            menuItem.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getItemType() != null && !requestDTO.getItemType().isEmpty()) {
            menuItem.setItemType(requestDTO.getItemType());
        }
        if (requestDTO.getCategory() != null && !requestDTO.getCategory().isEmpty()) {
            menuItem.setCategory(requestDTO.getCategory());
        }
        if (requestDTO.getPrice() != null) {
            menuItem.setPrice(requestDTO.getPrice());
        }

        menuItem.setUpdatedAt(LocalDateTime.now());
        menuItem.setUpdatedBy(currentUser.getUsername());

        return menuItemRepository.save(menuItem);
    }

}
