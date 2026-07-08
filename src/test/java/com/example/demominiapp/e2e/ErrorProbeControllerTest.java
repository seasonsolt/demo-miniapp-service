package com.example.demominiapp.e2e;

import com.example.demominiapp.DemoMiniappServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DemoMiniappServiceApplication.class)
@AutoConfigureMockMvc
class ErrorProbeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnStableBadRequestProbe() throws Exception {
        mockMvc.perform(get("/api/e2e/error-probe/{errorType}", "bad-request"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("E2E_BAD_REQUEST")))
                .andExpect(jsonPath("$.message", is("稳定 400 错误探针")));
    }

    @Test
    void shouldReturnStableServerErrorProbe() throws Exception {
        mockMvc.perform(get("/api/e2e/error-probe/{errorType}", "server-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code", is("E2E_SERVER_ERROR")))
                .andExpect(jsonPath("$.message", is("稳定 500 错误探针")));
    }

    @Test
    void shouldReturnStableBadRequestWhenErrorTypeIsUnsupported() throws Exception {
        mockMvc.perform(get("/api/e2e/error-probe/{errorType}", "unsupported"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("E2E_ERROR_TYPE_UNSUPPORTED")))
                .andExpect(jsonPath("$.message", is("不支持的错误探针类型")));
    }
}
