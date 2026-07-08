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
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DemoMiniappServiceApplication.class)
@AutoConfigureMockMvc
class ProbeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldWriteAndReadTestProbeData() throws Exception {
        String token = loginAndReturnToken();
        String probeId = "TEST_PROBE_READ_WRITE";

        mockMvc.perform(put("/api/e2e/probes/{probeId}", probeId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "value": "probe-value-001"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.probeId", is(probeId)))
                .andExpect(jsonPath("$.value", is("probe-value-001")))
                .andExpect(jsonPath("$.updatedBy", is("test-user")))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));

        mockMvc.perform(get("/api/e2e/probes/{probeId}", probeId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.probeId", is(probeId)))
                .andExpect(jsonPath("$.value", is("probe-value-001")))
                .andExpect(jsonPath("$.updatedBy", is("test-user")))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));
    }

    @Test
    void shouldReturnEmptyStateWhenTestProbeDoesNotExist() throws Exception {
        String token = loginAndReturnToken();

        mockMvc.perform(get("/api/e2e/probes/{probeId}", "TEST_PROBE_EMPTY")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("PROBE_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("未找到 TEST 探针数据")));
    }

    @Test
    void shouldRejectWriteWhenProbeIdDoesNotUseTestPrefix() throws Exception {
        String token = loginAndReturnToken();

        mockMvc.perform(put("/api/e2e/probes/{probeId}", "PROBE_NOT_ALLOWED")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "value": "probe-value-002"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("PROBE_ID_NOT_TEST")))
                .andExpect(jsonPath("$.message", is("探针 ID 必须以 TEST_ 开头")));
    }

    @Test
    void shouldRejectResetWhenProbeIdDoesNotUseTestPrefix() throws Exception {
        String token = loginAndReturnToken();

        mockMvc.perform(delete("/api/e2e/probes/{probeId}", "PROBE_NOT_ALLOWED")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("PROBE_ID_NOT_TEST")));
    }

    @Test
    void shouldResetTestProbeData() throws Exception {
        String token = loginAndReturnToken();
        String probeId = "TEST_PROBE_RESET";

        mockMvc.perform(put("/api/e2e/probes/{probeId}", probeId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "value": "probe-value-003"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/e2e/probes/{probeId}", probeId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.probeId", is(probeId)))
                .andExpect(jsonPath("$.deleted", is(true)));

        mockMvc.perform(get("/api/e2e/probes/{probeId}", probeId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("PROBE_NOT_FOUND")));
    }

    @Test
    void shouldRejectProbeRequestWhenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/e2e/probes/{probeId}", "TEST_PROBE_AUTH"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", is("TOKEN_MISSING")));
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
