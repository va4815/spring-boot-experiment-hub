package com.va4815.sessioncookie.dto;


import com.va4815.sessioncookie.entity.User;

public class UserDTO {
    private Long id;
    private String username;
    private String roleCode;

    public static UserDTO fromUser(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());

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

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }
}
