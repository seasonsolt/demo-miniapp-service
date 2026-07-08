package com.example.demominiapp.e2e;

import java.time.Instant;

record TestSession(
        String account,
        Instant expiresAt
) {
}
