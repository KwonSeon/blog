package com.snowk.blog.api.view;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(properties="BLOG_VIEW_SIGNING_SECRET=test-secret-not-for-runtime-use-123456789")
@AutoConfigureMockMvc @ActiveProfiles("test")
class ViewAuthorizationTest {
 @Autowired MockMvc mvc;
 @MockitoBean ViewService service;
 @Test void anonymousReadsAreAvailableAndUnsignedWritesForbidden() throws Exception {
  when(service.read("posts",1)).thenReturn(new ViewService.State(3));
  mvc.perform(get("/api/public/views/posts/1")).andExpect(status().isOk()).andExpect(jsonPath("$.count").value(3));
  mvc.perform(post("/api/public/views/posts/1").contentType("application/json").content("{\"timestamp\":0}")) .andExpect(status().isForbidden());
  verify(service,never()).record(anyString(),anyLong(),anyString(),any());
 }
 @Test void signedAnonymousRequestReachesService() throws Exception {
  long ts=java.time.Instant.now().getEpochSecond();var helper=new ViewSignatureTest();
  when(service.record(eq("posts"),eq(1L),eq(helper.visitor),any())).thenReturn(new ViewService.State(1));
  mvc.perform(post("/api/public/views/posts/1").contentType("application/json").content("{\"visitor\":\""+helper.visitor+"\",\"timestamp\":"+ts+",\"signature\":\""+helper.sign(ts)+"\"}"))
    .andExpect(status().isOk()).andExpect(jsonPath("$.count").value(1));
 }
}
