package com.example.demominiapp.e2e;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/e2e")
class HealthController {

    @GetMapping("/health")
    HealthResponse getHealth() {
        return new HealthResponse(
                "UP",
                "demo-miniapp-service",
                "0.1.0",
                Instant.now().toString()
        );
    }
}
