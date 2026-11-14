package com.snowk.blog.api.global.security.converter;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import static org.assertj.core.api.Assertions.*;
class SharedIdentityConverterTest {
 @Test void centralRoleDoesNotGrantBlogAdmin() {
  var converter=new JwtAuthenticatedUserConverter("https://identity.test", "42:7");
  var jwt=Jwt.withTokenValue("test").header("alg","RS256").subject("99").claim("roles",java.util.List.of("ROLE_ADMIN")).build();
  assertThat(converter.convert(jwt).getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
 }
 @Test void explicitBindingGrantsEditorAndPreservesLocalAuthor() {
  var converter=new JwtAuthenticatedUserConverter("https://identity.test", "42:7");
  var jwt=Jwt.withTokenValue("test").header("alg","RS256").subject("42").build();
  var auth=converter.convert(jwt);
  assertThat(auth.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
  assertThat(((com.snowk.blog.api.global.security.principal.AuthenticatedUser)auth.getPrincipal()).getUserId()).isEqualTo(7L);
 }
 @Test void duplicateBindingsAreRejected(){assertThatThrownBy(()->new JwtAuthenticatedUserConverter("https://identity.test","42:7,42:8")).isInstanceOf(IllegalArgumentException.class);}
}
