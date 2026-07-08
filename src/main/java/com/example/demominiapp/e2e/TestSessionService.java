package com.example.demominiapp.e2e;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
class TestSessionService {

    private static final String TEST_ACCOUNT = "test-user";
    private static final String TEST_PASSWORD = "test-password";

    private final Clock clock;
    private final Duration tokenTtl;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, TestSession> sessions = new ConcurrentHashMap<>();

    TestSessionService(@Value("${demo-miniapp.session.token-ttl-seconds:900}") long tokenTtlSeconds) {
        this.clock = Clock.systemUTC();
        this.tokenTtl = Duration.ofSeconds(tokenTtlSeconds);
    }

    LoginResponse login(LoginRequest request) {
        if (request == null
                || !TEST_ACCOUNT.equals(request.account())
                || !TEST_PASSWORD.equals(request.password())) {
            throw new InvalidCredentialsException();
        }

        String token = generateToken();
        Instant expiresAt = Instant.now(clock).plus(tokenTtl);
        sessions.put(token, new TestSession(request.account(), expiresAt));
        return new LoginResponse(token, request.account(), expiresAt.toString());
    }

    TestSession requireSession(String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);
        TestSession session = sessions.get(token);
        if (session == null || !session.expiresAt().isAfter(Instant.now(clock))) {
            sessions.remove(token);
            throw new InvalidTokenException();
        }
        return session;
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new MissingTokenException();
        }
        String prefix = "Bearer ";
        if (!authorizationHeader.startsWith(prefix)) {
            throw new MissingTokenException();
        }
        String token = authorizationHeader.substring(prefix.length()).trim();
        if (token.isEmpty()) {
            throw new MissingTokenException();
        }
        return token;
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
