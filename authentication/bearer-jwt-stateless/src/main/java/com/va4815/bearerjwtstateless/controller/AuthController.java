package com.va4815.bearerjwtstateless.controller;

import com.va4815.bearerjwtstateless.config.jwt.JwtUtil;
import com.va4815.bearerjwtstateless.dto.AuthRequestDTO;
import com.va4815.bearerjwtstateless.dto.CreateuserRequestDTO;
import com.va4815.bearerjwtstateless.dto.UserResponseDTO;
import com.va4815.bearerjwtstateless.entity.Role;
import com.va4815.bearerjwtstateless.entity.User;
import com.va4815.bearerjwtstateless.repository.RoleRepository;
import com.va4815.bearerjwtstateless.repository.UserRepository;
import com.va4815.bearerjwtstateless.service.RoleService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private RoleService roleService;

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, RoleService roleService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.roleService = roleService;
    }

    @PostMapping("/login")
    public UserResponseDTO login(
            @RequestBody AuthRequestDTO requestDTO) {

        if (!StringUtils.hasText(requestDTO.username()) || !StringUtils.hasText(requestDTO.password())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.username(), requestDTO.password())
        );

        final UserDetails principal = (UserDetails) authentication.getPrincipal();

        assert principal != null;
        if (!passwordEncoder.matches(requestDTO.password(), principal.getPassword()) ||
            !requestDTO.username().equals(principal.getUsername())
        ) {
            throw new BadCredentialsException("Invalid username or password");
        }

        User user = userRepository.findByUsername(requestDTO.username()).orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
        String token = jwtUtil.generateToken(principal.getUsername());

        return UserResponseDTO.fromUser(user, token);
    }

    @PostMapping("/signup")
    public UserResponseDTO signup(@RequestBody CreateuserRequestDTO requestDTO) throws BadCredentialsException {
        if (userRepository.findByUsername(requestDTO.getUsername()).isPresent()) {
            throw new BadCredentialsException("Username already exists");
        }

        User user = new User();
        user.setUsername(requestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        Role role = roleService.findByCode(requestDTO.getRoleCode());
        user.setRole(role);

        user = userRepository.save(user);

        return UserResponseDTO.fromUser(user);
    }


}
