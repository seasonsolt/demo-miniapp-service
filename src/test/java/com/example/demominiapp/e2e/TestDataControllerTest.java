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

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DemoMiniappServiceApplication.class)
@AutoConfigureMockMvc
class TestDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRejectResetWhenTokenIsMissing() throws Exception {
        mockMvc.perform(post("/api/e2e/test-data/reset"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", is("TOKEN_MISSING")));
    }

    @Test
    void shouldRejectSeedWhenTokenIsInvalid() throws Exception {
        mockMvc.perform(post("/api/e2e/test-data/seed")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", is("TOKEN_INVALID_OR_EXPIRED")));
    }

    @Test
    void shouldResetOnlyTestProbeData() throws Exception {
        String token = loginAndReturnToken();

        writeProbe(token, "TEST_RESET_SEED_A", "reset-seed-a");
        writeProbe(token, "TEST_RESET_SEED_B", "reset-seed-b");

        mockMvc.perform(post("/api/e2e/test-data/reset")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("RESET_DONE")))
                .andExpect(jsonPath("$.deletedCount", greaterThanOrEqualTo(2)));

        mockMvc.perform(get("/api/e2e/probes/{probeId}", "TEST_RESET_SEED_A")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("PROBE_NOT_FOUND")));
    }

    @Test
    void shouldSeedDeterministicTestProbeDataAfterReset() throws Exception {
        String token = loginAndReturnToken();
        writeProbe(token, "TEST_SEED_OLD", "old-value");

        mockMvc.perform(post("/api/e2e/test-data/seed")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SEED_DONE")))
                .andExpect(jsonPath("$.deletedCount", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.seededCount", is(1)))
                .andExpect(jsonPath("$.probes[0].probeId", is("TEST_SEED_PROBE")))
                .andExpect(jsonPath("$.probes[0].value", is("seed-value-miniapp")))
                .andExpect(jsonPath("$.probes[0].updatedBy", is("test-user")));

        mockMvc.perform(get("/api/e2e/probes/{probeId}", "TEST_SEED_PROBE")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.probeId", is("TEST_SEED_PROBE")))
                .andExpect(jsonPath("$.value", is("seed-value-miniapp")));

        mockMvc.perform(get("/api/e2e/probes/{probeId}", "TEST_SEED_OLD")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("PROBE_NOT_FOUND")));
    }

    private void writeProbe(String token, String probeId, String value) throws Exception {
        mockMvc.perform(put("/api/e2e/probes/{probeId}", probeId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "value": "%s"
                                }
                                """.formatted(value)))
                .andExpect(status().isOk());
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
