package com.va4815.springresourcebackend.controller;

import com.va4815.springresourcebackend.dto.request.CreateMenuRequestDTO;
import com.va4815.springresourcebackend.dto.response.MenuResponseDTO;
import com.va4815.springresourcebackend.service.MenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping
    public ResponseEntity<MenuResponseDTO> createMenu(@RequestBody CreateMenuRequestDTO createMenuRequestDTO) {
        MenuResponseDTO responseDTO = menuService.createMenu(createMenuRequestDTO);

        return ResponseEntity.ok(responseDTO);
    }

}
