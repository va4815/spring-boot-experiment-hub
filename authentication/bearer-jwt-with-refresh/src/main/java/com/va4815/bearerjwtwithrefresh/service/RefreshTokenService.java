package com.va4815.bearerjwtwithrefresh.service;

import com.va4815.bearerjwtwithrefresh.config.token.TokenCodec;
import com.va4815.bearerjwtwithrefresh.dto.RotatedRefreshToken;
import com.va4815.bearerjwtwithrefresh.entity.RefreshToken;
import com.va4815.bearerjwtwithrefresh.entity.User;
import com.va4815.bearerjwtwithrefresh.exception.InvalidRefreshTokenException;
import com.va4815.bearerjwtwithrefresh.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Optional;

import static org.springframework.data.util.ClassUtils.ifPresent;

@Service
public class RefreshTokenService {
    @Value("${jwt.token.refresh.expiration}")
    private Long refreshTokenExpiration;

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenCodec tokenCodec;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               TokenCodec tokenCodec) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenCodec = tokenCodec;
    }

    @Transactional
    public String createRefreshToken(User user) {
        return createRefreshToken(user, Instant.now());
    }

    @Transactional
    public RotatedRefreshToken rotateRefreshToken(String rawToken) {
        if (!StringUtils.hasText(rawToken)) {
            throw new InvalidRefreshTokenException();
        }

        RefreshToken currentToken = refreshTokenRepository
                .findByTokenHashForUpdate(tokenCodec.hash(rawToken))
                .orElseThrow(InvalidRefreshTokenException::new);

        Instant now = Instant.now();
        if (currentToken.getRevokedAt() != null || !currentToken.getExpiresAt().isAfter(now)) {
            throw new InvalidRefreshTokenException();
        }

        currentToken.setLastUsedAt(now);
        currentToken.setRevokedAt(now);

        String replacementToken = createRefreshToken(currentToken.getUser(), now);
        return new RotatedRefreshToken(currentToken.getUser(), replacementToken);
    }

    private String createRefreshToken(User user, Instant createdAt) {
        String rawToken = tokenCodec.generateToken();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenCodec.hash(rawToken));
        refreshToken.setCreatedAt(createdAt);
        refreshToken.setExpiresAt(createdAt.plusSeconds(refreshTokenExpiration));

        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    public Optional<RefreshToken> findByRawToken(String rawToken) {
        if (!StringUtils.hasText(rawToken)) {
            return Optional.empty();
        }
        return refreshTokenRepository.findByTokenHash(tokenCodec.hash(rawToken));
    }

    @Transactional
    public void deleteByRawToken(String rawToken) {
        refreshTokenRepository.findByTokenHashForUpdate(tokenCodec.hash(rawToken))
                        .ifPresent(refreshTokenRepository::delete);
    }

}
