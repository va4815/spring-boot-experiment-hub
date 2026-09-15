package com.va4815.bearerjwtstateless.config.authentication;

import com.va4815.bearerjwtstateless.entity.User;
import com.va4815.bearerjwtstateless.repository.UserRepository;
import com.va4815.bearerjwtstateless.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthUserDetailService implements UserDetailsService {

    private final UserService userService;

    public AuthUserDetailService(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);

        return AuthUserDetails.from(user);
    }
}

