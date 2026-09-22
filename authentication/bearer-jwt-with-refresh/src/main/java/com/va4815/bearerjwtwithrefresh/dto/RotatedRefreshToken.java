package com.va4815.bearerjwtwithrefresh.dto;

import com.va4815.bearerjwtwithrefresh.entity.User;

public record RotatedRefreshToken(User user, String rawToken) {
}
