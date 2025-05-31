package com.elitefolk.authservice.security.models;

import com.elitefolk.authservice.models.LoginMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private LoginMode loginMode;

    public CustomAuthenticationToken(String principal, String credentials, LoginMode loginMode) {
        super(principal, credentials);
        this.loginMode = loginMode;
    }

    public CustomAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
