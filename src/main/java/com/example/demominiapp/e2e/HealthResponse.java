package com.example.demominiapp.e2e;

record HealthResponse(
        String status,
        String service,
        String version,
        String time
) {
}
