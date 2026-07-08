package com.example.demominiapp.e2e;

record LoginResponse(
        String token,
        String account,
        String expiresAt
) {
}
