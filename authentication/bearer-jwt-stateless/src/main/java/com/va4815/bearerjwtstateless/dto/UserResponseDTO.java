package com.va4815.bearerjwtstateless.dto;

import com.va4815.bearerjwtstateless.entity.User;

public class UserResponseDTO {
    private Long id;

    private String username;
    private String token;
    private String roleCode;

    public static UserResponseDTO fromUser(User user) {
        UserResponseDTO userDTO = new UserResponseDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());

        if (user.getRole() != null) {
            userDTO.setRoleCode(user.getRole().getCode());
        }

        return userDTO;
    }

    public static UserResponseDTO fromUser(User user, String token) {
        UserResponseDTO userDTO = new UserResponseDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setToken(token);

        if (user.getRole() != null) {
            userDTO.setRoleCode(user.getRole().getCode());
        }

        return userDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }
}
