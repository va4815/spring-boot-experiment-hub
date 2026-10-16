package com.va4815.springresourcebackend.service;

import com.va4815.springresourcebackend.repository.MenuItemRepository;
import org.springframework.stereotype.Service;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

}
