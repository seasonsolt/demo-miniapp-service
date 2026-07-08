package com.example.demominiapp.e2e;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/e2e/test-data")
class TestDataController {

    private final TestSessionService sessionService;
    private final ProbeService probeService;

    TestDataController(TestSessionService sessionService, ProbeService probeService) {
        this.sessionService = sessionService;
        this.probeService = probeService;
    }

    @PostMapping("/reset")
    TestDataResetResponse reset(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        sessionService.requireSession(authorizationHeader);
        return probeService.resetTestData();
    }

    @PostMapping("/seed")
    TestDataSeedResponse seed(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        TestSession session = sessionService.requireSession(authorizationHeader);
        return probeService.seedTestData(session.account());
    }
}
