package com.va4815.basicauth.service;

import com.va4815.basicauth.dto.RoleDTO;
import com.va4815.basicauth.entity.Role;
import com.va4815.basicauth.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleDTO> findAll() {
        return roleRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public RoleDTO findById(Long id) {
        Optional<Role> roleOpt = roleRepository.findById(id);

        if (roleOpt.isEmpty()) {
            throw new IllegalArgumentException("Role not found");
        }

        return toDTO(roleOpt.get());
    }

    public RoleDTO findByCode(String code) {
        Optional<Role> roleOpt = roleRepository.findByCode(code);

        if (roleOpt.isEmpty()) {
            throw new IllegalArgumentException("Role not found");
        }

        return toDTO(roleOpt.get());
    }

    private RoleDTO toDTO(Role role) {
        return new RoleDTO(role.getId(), role.getCode(), role.getName());
    }

}
