package com.va4815.bearerjwtwithrefresh.service;

import com.va4815.bearerjwtwithrefresh.dto.CreateUserRequestDTO;
import com.va4815.bearerjwtwithrefresh.entity.Role;
import com.va4815.bearerjwtwithrefresh.entity.User;
import com.va4815.bearerjwtwithrefresh.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    private RoleService roleService;

    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Authenticated user no longer exists"
                ));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password")
                );
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User createUser(CreateUserRequestDTO requestDTO) {
        if (requestDTO.getUsername() == null || requestDTO.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (existsByUsername(requestDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (requestDTO.getPassword() == null || requestDTO.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (requestDTO.getRoleCode() == null || requestDTO.getRoleCode().isEmpty()) {
            throw new IllegalArgumentException("Role code cannot be null or empty");
        }
        if (roleService.findByCode(requestDTO.getRoleCode()) == null) {
            throw new IllegalArgumentException("Role code does not exist");
        }


        User user = new User();
        user.setUsername(requestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        Role role = roleService.findByCode(requestDTO.getRoleCode());
        user.setRole(role);

        user = userRepository.save(user);

        return user;
    }

}
