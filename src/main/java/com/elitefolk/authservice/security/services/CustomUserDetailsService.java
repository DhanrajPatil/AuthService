package com.elitefolk.authservice.security.services;

import com.elitefolk.authservice.security.models.CustomUserDetails;
import com.elitefolk.authservice.models.User;
import com.elitefolk.authservice.services.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByUsername(username);
        if (user != null) {
            return new CustomUserDetails(
                    user.getEmail(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getMobile(),
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    true,
                    user.getRoles().get(0).getName(),
                    true,
                    true,
                    true
            );
        }
        return null;
    }
}
