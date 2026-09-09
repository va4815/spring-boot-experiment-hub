package com.va4815.sessioncookie.controller;

import com.va4815.sessioncookie.config.SecurityConfig;
import com.va4815.sessioncookie.config.authentication.AuthUserDetails;
import com.va4815.sessioncookie.entity.Role;
import com.va4815.sessioncookie.entity.User;
import com.va4815.sessioncookie.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AuthController.class, UserController.class})
@Import({SecurityConfig.class, SessionAuthenticationFlowTest.AuthenticationTestConfiguration.class})
class SessionAuthenticationFlowTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setUpCurrentUser() {
        Role role = new Role();
        role.setCode("SHOP_MANAGER");
        role.setName("Shop Manager");

        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setRole(role);

        when(userService.getUserByUsername("admin")).thenReturn(user);
    }

    @Test
    void completeCsrfLoginCurrentUserAndLogoutFlow() throws Exception {
        MvcResult csrfResult = mockMvc.perform(get("/auth/csrf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        JsonNode csrfBody = objectMapper.readTree(csrfResult.getResponse().getContentAsString());
        String csrfHeader = csrfBody.get("headerName").asText();
        String csrfToken = csrfBody.get("token").asText();
        MockHttpSession anonymousSession = (MockHttpSession) csrfResult.getRequest().getSession(false);
        String anonymousSessionId = anonymousSession.getId();

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .session(anonymousSession)
                        .header(csrfHeader, csrfToken)
                        .contentType("application/json")
                        .content("""
                                {"username":"admin","password":"password"}
                                """))
                .andExpect(status().isNoContent())
                .andExpect(authenticated().withUsername("admin"))
                .andExpect(content().string(""))
                .andReturn();

        MockHttpSession authenticatedSession = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertThat(authenticatedSession).isNotNull();
        assertThat(authenticatedSession.getId()).isNotEqualTo(anonymousSessionId);

        mockMvc.perform(get("/users/me").session(authenticatedSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.roleCode").value("SHOP_MANAGER"))
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(post("/auth/logout")
                        .session(authenticatedSession)
                        .header(csrfHeader, csrfToken))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("JSESSIONID", 0));

        assertThat(authenticatedSession.isInvalid()).isTrue();
    }

    @Test
    void invalidCredentialsReturnGenericUnauthorizedResponse() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {"username":"admin","password":"incorrect"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void loginWithoutCsrfTokenIsForbidden() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"username":"admin","password":"password"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void currentUserRequiresAuthenticatedSession() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @TestConfiguration
    static class AuthenticationTestConfiguration {

        @Bean
        UserDetailsService testUserDetailsService(PasswordEncoder passwordEncoder) {
            AuthUserDetails admin = new AuthUserDetails(
                    "admin",
                    passwordEncoder.encode("password"),
                    List.of(new SimpleGrantedAuthority("ROLE_SHOP_MANAGER")));

            return username -> {
                if (!admin.getUsername().equals(username)) {
                    throw new UsernameNotFoundException("Invalid username or password");
                }
                return admin;
            };
        }

    }
}
