package com.va4815.beareropaquestateful.config;

import com.va4815.beareropaquestateful.config.authentication.AuthTokenFilter;
import com.va4815.beareropaquestateful.config.authentication.AuthUserCache;
import com.va4815.beareropaquestateful.config.authentication.AuthUserDetailService;
import com.va4815.beareropaquestateful.config.authentication.AuthUserDetails;
import com.va4815.beareropaquestateful.controller.PublicController;
import com.va4815.beareropaquestateful.controller.UserController;
import com.va4815.beareropaquestateful.entity.Role;
import com.va4815.beareropaquestateful.entity.User;
import com.va4815.beareropaquestateful.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, PublicController.class})
@Import({SecurityConfig.class, AuthTokenFilter.class})
class SecurityConfigTest {

    private static final String TOKEN = "opaque-access-token";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthUserCache authUserCache;

    @MockitoBean
    private AuthUserDetailService authUserDetailService;

    @MockitoBean
    private UserService userService;

    @Test
    void publicEndpointDoesNotRequireAuthentication() throws Exception {
        mockMvc.perform(get("/public/greeting"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpointRejectsMissingToken() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointRejectsInvalidToken() throws Exception {
        when(authUserCache.getUserByToken("invalid-token")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointAcceptsTokenFromServerSideStore() throws Exception {
        User user = user("myuser", "STAFF");
        AuthUserDetails userDetails = AuthUserDetails.from(user);
        when(authUserCache.getUserByToken(TOKEN)).thenReturn(Optional.of(user));
        when(authUserDetailService.loadUserByUsername("myuser")).thenReturn(userDetails);
        when(userService.getUserByUsername("myuser")).thenReturn(user);

        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("myuser"))
                .andExpect(jsonPath("$.roleCode").value("STAFF"));

        verify(authUserCache).getUserByToken(TOKEN);
        verify(authUserDetailService).loadUserByUsername("myuser");
    }

    private User user(String username, String roleCode) {
        Role role = new Role();
        role.setCode(roleCode);

        User user = new User();
        user.setUsername(username);
        user.setPassword("encoded-password");
        user.setRole(role);
        return user;
    }
}
