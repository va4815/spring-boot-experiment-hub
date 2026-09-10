package com.va4815.bearerjwtstateless.controller;

import com.va4815.bearerjwtstateless.dto.AuthRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody AuthRequestDTO requestDTO,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (!StringUtils.hasText(requestDTO.username()) || !StringUtils.hasText(requestDTO.password())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // TODO: Implement login logic


        return ResponseEntity.ok().build();
    }

}
