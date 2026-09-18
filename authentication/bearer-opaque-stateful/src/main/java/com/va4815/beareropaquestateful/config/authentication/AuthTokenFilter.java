package com.va4815.beareropaquestateful.config.authentication;

import com.va4815.beareropaquestateful.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    private static final String BEARER_ = "Bearer ";

    private final AuthUserCache authUserCache;
    private AuthUserDetailService authUserDetailService;

    public AuthTokenFilter(AuthUserCache authUserCache, AuthUserDetailService authUserDetailService) {
        this.authUserCache = authUserCache;
        this.authUserDetailService = authUserDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = parseToken(request);

            if (token == null) {
                filterChain.doFilter(request, response);
            }

            Optional<User> userOpt = authUserCache.getUserByToken(token);
            if (userOpt.isPresent()) {
                User user = userOpt.get();

                UserDetails userDetails = authUserDetailService.loadUserByUsername(user.getUsername());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request, response);
            }

        } catch (Exception e) {
            System.out.println("Cannot set user authentication " + e.getLocalizedMessage());
        }
    }

    private String parseToken(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (headerAuth != null && headerAuth.startsWith(BEARER_)) {
            return headerAuth.substring(BEARER_.length());
        }

        return null;
    }

}
