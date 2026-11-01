package com.va4815.springresourcebackend.dto.response;

import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;

public class TokenResponseDTO {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private Long refreshExpiresIn;
    private String tokenType;

    public TokenResponseDTO(AccessTokenResponse accessTokenResponse) {
        if (accessTokenResponse == null) {
            throw new IllegalArgumentException("User not found");
        }

        this.accessToken = accessTokenResponse.getToken();
        this.refreshToken = accessTokenResponse.getRefreshToken();
        this.expiresIn = accessTokenResponse.getExpiresIn();
        this.refreshExpiresIn = accessTokenResponse.getRefreshExpiresIn();
        this.tokenType = accessTokenResponse.getTokenType();

    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Long getRefreshExpiresIn() {
        return refreshExpiresIn;
    }

    public void setRefreshExpiresIn(Long refreshExpiresIn) {
        this.refreshExpiresIn = refreshExpiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
