package com.va4815.basicauth.service;

import com.va4815.basicauth.entity.Role;
import com.va4815.basicauth.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findByCode(String code) {
        Optional<Role> roleOpt = roleRepository.findByCode(code);

        if (roleOpt.isEmpty()) {
            throw new IllegalArgumentException("Role not found");
        }

        return roleOpt.get();
    }

}
