package com.va4815.sessioncookie.controller;

import com.va4815.sessioncookie.config.authentication.AuthUserDetails;
import com.va4815.sessioncookie.dto.AuthRequestDTO;
import com.va4815.sessioncookie.dto.UserDTO;
import com.va4815.sessioncookie.entity.User;
import com.va4815.sessioncookie.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;

        this.securityContextRepository = new HttpSessionSecurityContextRepository();
    }

    @GetMapping("/csrf")
    public CsrfToken getCsrfToken(CsrfToken token) {
        return token;
    }

    @PostMapping("/login")
    public UserDTO login(@RequestBody AuthRequestDTO requestDTO, HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(requestDTO.username(), requestDTO.password())
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        AuthUserDetails principal = (AuthUserDetails) authentication.getPrincipal();

        User user = userService.getUserByUsername(principal.getUsername());

        return UserDTO.fromUser(user);
    }

}
