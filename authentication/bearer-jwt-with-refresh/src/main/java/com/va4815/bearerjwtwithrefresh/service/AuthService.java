package com.va4815.bearerjwtwithrefresh.service;

import com.va4815.bearerjwtwithrefresh.config.jwt.JwtUtil;
import com.va4815.bearerjwtwithrefresh.dto.LoginRequestDTO;
import com.va4815.bearerjwtwithrefresh.dto.TokenResponseDTO;
import com.va4815.bearerjwtwithrefresh.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager,
                       UserService userService,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil
    ) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public TokenResponseDTO login(LoginRequestDTO requestDTO) {

        if (!StringUtils.hasText(requestDTO.username()) || !StringUtils.hasText(requestDTO.password())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.username(), requestDTO.password())
        );
        final UserDetails principal = (UserDetails) authentication.getPrincipal();

        if (principal == null || !passwordEncoder.matches(requestDTO.password(), principal.getPassword()) ||
                !requestDTO.username().equals(principal.getUsername())
        ) {
            throw new BadCredentialsException("Invalid username or password");
        }

        User user = userService.findByUsername(requestDTO.username());
        String token = jwtUtil.generateToken(principal.getUsername());

        return new TokenResponseDTO(token, user.getId());
    }

}
