package com.example.demominiapp.e2e;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/e2e")
class SessionController {

    private final TestSessionService sessionService;

    SessionController(TestSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/login")
    LoginResponse login(@RequestBody LoginRequest request) {
        return sessionService.login(request);
    }

    @GetMapping("/session")
    SessionResponse getSession(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        TestSession session = sessionService.requireSession(authorizationHeader);
        return new SessionResponse("AUTHENTICATED", session.account(), session.expiresAt().toString());
    }

    @GetMapping("/protected-probe")
    ProtectedProbeResponse getProtectedProbe(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        TestSession session = sessionService.requireSession(authorizationHeader);
        return new ProtectedProbeResponse("AUTHORIZED", session.account());
    }
}
