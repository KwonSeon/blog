package com.snowk.blog.api.global.security.converter;

import com.snowk.blog.api.global.security.authentication.AuthenticatedUserAuthenticationToken;
import com.snowk.blog.api.global.security.principal.AuthenticatedUser;
import java.util.Collection;
import java.util.List;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtAuthenticatedUserConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<String> roles = extractRoles(jwt);
        Collection<GrantedAuthority> authorities = roles.stream()
            .map(SimpleGrantedAuthority::new)
            .map(GrantedAuthority.class::cast)
            .toList();

        AuthenticatedUser principal = new AuthenticatedUser(
            jwt.getSubject(),
            resolveUserId(jwt),
            jwt.getClaimAsString("username"),
            roles
        );

        return new AuthenticatedUserAuthenticationToken(principal, jwt, authorities);
    }

    private Long resolveUserId(Jwt jwt) {
        Object userIdClaim = jwt.getClaims().get("userId");
        if (userIdClaim instanceof Number number) {
            return number.longValue();
        }
        if (userIdClaim instanceof String value && StringUtils.hasText(value)) {
            return Long.parseLong(value);
        }
        if (StringUtils.hasText(jwt.getSubject())) {
            return Long.parseLong(jwt.getSubject());
        }

        return null;
    }

    private List<String> extractRoles(Jwt jwt) {
        Object rolesClaim = jwt.getClaims().get("roles");
        if (!(rolesClaim instanceof List<?> roles)) {
            return List.of();
        }

        return roles.stream()
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .filter(StringUtils::hasText)
            .toList();
    }
}
