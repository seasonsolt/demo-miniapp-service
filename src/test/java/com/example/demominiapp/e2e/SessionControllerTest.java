package com.example.demominiapp.e2e;

import com.example.demominiapp.DemoMiniappServiceApplication;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DemoMiniappServiceApplication.class)
@AutoConfigureMockMvc
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldIssueTokenForTestAccount() throws Exception {
        mockMvc.perform(post("/api/e2e/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "account": "test-user",
                                  "password": "test-password"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(nullValue())))
                .andExpect(jsonPath("$.account", is("test-user")))
                .andExpect(jsonPath("$.expiresAt", not(nullValue())));
    }

    @Test
    void shouldReturnSessionForValidToken() throws Exception {
        String token = loginAndReturnToken();

        mockMvc.perform(get("/api/e2e/session")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("AUTHENTICATED")))
                .andExpect(jsonPath("$.account", is("test-user")))
                .andExpect(jsonPath("$.expiresAt", not(nullValue())));
    }

    @Test
    void shouldRejectProtectedProbeWhenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/e2e/protected-probe"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", is("TOKEN_MISSING")))
                .andExpect(jsonPath("$.message", is("缺少测试会话 token")));
    }

    @Test
    void shouldRejectSessionWhenTokenIsInvalid() throws Exception {
        mockMvc.perform(get("/api/e2e/session")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", is("TOKEN_INVALID_OR_EXPIRED")))
                .andExpect(jsonPath("$.message", is("测试会话 token 无效或已过期")));
    }

    private String loginAndReturnToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/e2e/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "account": "test-user",
                                  "password": "test-password"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }
}
