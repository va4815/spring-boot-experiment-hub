package com.va4815.bearerjwtwithrefresh.service;

import com.va4815.bearerjwtwithrefresh.config.token.JwtUtil;
import com.va4815.bearerjwtwithrefresh.dto.*;
import com.va4815.bearerjwtwithrefresh.entity.RefreshToken;
import com.va4815.bearerjwtwithrefresh.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserService userService,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       RefreshTokenService refreshTokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
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
        String accessToken = jwtUtil.generateToken(principal.getUsername());
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return new TokenResponseDTO(accessToken, refreshToken, user.getId());
    }

    @Transactional
    public RefreshTokenResponseDTO refresh(RefreshTokenRequestDTO requestDTO) {
        RotatedRefreshToken rotatedToken = refreshTokenService.rotateRefreshToken(requestDTO.refreshToken());
        String accessToken = jwtUtil.generateToken(rotatedToken.user().getUsername());

        return new RefreshTokenResponseDTO(accessToken, rotatedToken.rawToken());
    }

    public void logout(RefreshTokenRequestDTO requestDTO) {
        refreshTokenService.deleteByRawToken(requestDTO.refreshToken());
    }

}
