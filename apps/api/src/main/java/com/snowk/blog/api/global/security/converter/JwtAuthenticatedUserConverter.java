package com.snowk.blog.api.global.security.converter;

import com.snowk.blog.api.global.security.authentication.AuthenticatedUserAuthenticationToken;
import com.snowk.blog.api.global.security.principal.AuthenticatedUser;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Value;
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

    private final Map<String, Long> editorBindings = new HashMap<>();
    private final boolean sharedIdentity;

    public JwtAuthenticatedUserConverter(
        @Value("${BLOG_IDENTITY_ISSUER:}") String issuer,
        @Value("${BLOG_IDENTITY_EDITOR_BINDINGS:}") String bindings
    ) {
        sharedIdentity = !issuer.isBlank();
        if (!bindings.isBlank()) for (String pair : bindings.split(",")) {
            String[] parts = pair.trim().split(":", -1);
            if (parts.length != 2 || !parts[0].matches("[0-9]+") || !parts[1].matches("[1-9][0-9]*"))
                throw new IllegalArgumentException("Invalid blog editor binding");
            if (editorBindings.putIfAbsent(parts[0], Long.valueOf(parts[1])) != null)
                throw new IllegalArgumentException("Duplicate blog editor binding");
        }
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<String> roles = sharedIdentity ? (editorBindings.containsKey(jwt.getSubject()) ? List.of("ROLE_ADMIN") : List.of("ROLE_USER")) : extractRoles(jwt);
        Collection<GrantedAuthority> authorities = roles.stream()
            .map(SimpleGrantedAuthority::new)
            .map(GrantedAuthority.class::cast)
            .toList();

        AuthenticatedUser principal = new AuthenticatedUser(
            jwt.getSubject(),
            sharedIdentity ? editorBindings.get(jwt.getSubject()) : resolveUserId(jwt),
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
