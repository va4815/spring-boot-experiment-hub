package com.va4815.basicauth.dto;

import com.va4815.basicauth.entity.Role;

public class RoleDTO {
    private Long id;
    private String code;
    private String name;

    public RoleDTO(Long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public static RoleDTO toDTO(Role role) {
        return new RoleDTO(role.getId(), role.getCode(), role.getName());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
