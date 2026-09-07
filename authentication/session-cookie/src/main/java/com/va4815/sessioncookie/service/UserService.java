package com.va4815.sessioncookie.service;

import com.va4815.sessioncookie.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final RoleRepository roleRepository;

    public UserService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

}
