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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = DemoMiniappServiceApplication.class,
        properties = "demo-miniapp.session.token-ttl-seconds=0"
)
@AutoConfigureMockMvc
class ExpiredSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRejectExpiredToken() throws Exception {
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
        String token = JsonPath.read(result.getResponse().getContentAsString(), "$.token");

        mockMvc.perform(get("/api/e2e/session")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", is("TOKEN_INVALID_OR_EXPIRED")));
    }
}
