package com.snowk.blog.api.reaction;
import java.util.List;
import com.snowk.blog.api.global.security.converter.JwtAuthenticatedUserConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.oauth2.jwt.Jwt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class ReactionAuthorizationTest {
 @Autowired MockMvc mvc;
 @MockitoBean ReactionService service;
 private org.springframework.security.core.Authentication member() {
  return new JwtAuthenticatedUserConverter("https://identity.test", "").convert(Jwt.withTokenValue("test-only").header("alg","RS256").subject("10").claim("merged_subjects",List.of("9")).build());
 }
 @Test void anonymousCanReadButCannotWrite() throws Exception {
  when(service.read("posts",1,List.of())).thenReturn(new ReactionService.State(2,false,false));
  mvc.perform(get("/api/public/reactions/posts/1")).andExpect(status().isOk()).andExpect(jsonPath("$.count").value(2));
  mvc.perform(put("/api/reactions/posts/1").contentType("application/json").content("{\"liked\":true}")).andExpect(status().isUnauthorized());
  verify(service,never()).set(anyString(),anyLong(),anyList(),anyBoolean());
 }
 @Test void ordinaryMemberUsesSignedSubjectAndDoesNotNeedAuthorBinding() throws Exception {
  when(service.set("posts",1,List.of("10","9"),true)).thenReturn(new ReactionService.State(1,true,true));
  mvc.perform(put("/api/reactions/posts/1").with(authentication(member())).contentType("application/json").content("{\"liked\":true,\"identitySubject\":\"999\"}"))
   .andExpect(status().isOk()).andExpect(jsonPath("$.liked").value(true));
  verify(service).set("posts",1,List.of("10","9"),true);
 }
 @Test void missingDesiredStateIsRejected() throws Exception {
  mvc.perform(put("/api/reactions/posts/1").with(authentication(member())).contentType("application/json").content("{}"))
   .andExpect(status().isBadRequest());
  verifyNoInteractions(service);
 }
}
