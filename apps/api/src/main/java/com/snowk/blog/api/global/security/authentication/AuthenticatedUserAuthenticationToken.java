package com.snowk.blog.api.global.security.authentication;

import com.snowk.blog.api.global.security.principal.AuthenticatedUser;
import java.util.Collection;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

@Getter
public class AuthenticatedUserAuthenticationToken extends AbstractAuthenticationToken {

    private final AuthenticatedUser principal;
    private final Jwt token;

    public AuthenticatedUserAuthenticationToken(
        AuthenticatedUser principal,
        Jwt token,
        Collection<? extends GrantedAuthority> authorities
    ) {
        super(authorities);
        this.principal = principal;
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public String getName() {
        return principal.getSubject();
    }
}
