package com.va4815.bearerjwtwithrefresh.dto;

public record TokenResponseDTO(String accessToken, String refreshToken, Long userId) {

}
