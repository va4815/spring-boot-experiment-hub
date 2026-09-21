package com.va4815.bearerjwtwithrefresh.service;

import com.va4815.bearerjwtwithrefresh.entity.RefreshToken;
import com.va4815.bearerjwtwithrefresh.entity.User;
import com.va4815.bearerjwtwithrefresh.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${jwt.token.refresh.expiration}")
    private Long refreshTokenExpiration;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
    }

    public String createRefreshToken(String username) {
        boolean existsUser = userService.existsByUsername(username);
        if (!existsUser) {
            throw new IllegalArgumentException("User does not exist");
        }
        User user = userService.findByUsername(username);

        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId()).orElse(null);
        if (refreshToken == null) {
            // new refresh token

            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setCreatedAt(LocalDateTime.now());
            refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration));

            // TODO: hash refresh token
            refreshToken.setTokenHash(UUID.randomUUID().toString());

            refreshToken = refreshTokenRepository.save(refreshToken);
        }

        return refreshToken.getTokenHash();
    }

}
