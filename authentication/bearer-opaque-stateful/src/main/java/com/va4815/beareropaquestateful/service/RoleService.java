package com.va4815.beareropaquestateful.service;

import com.va4815.beareropaquestateful.entity.Role;
import com.va4815.beareropaquestateful.repository.RoleRepository;
import org.springframework.security.authentication.BadCredentialsException;
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
            throw new BadCredentialsException("Role not found");
        }

        return roleOpt.get();
    }

}
