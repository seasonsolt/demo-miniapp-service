package com.example.demominiapp.e2e;

import java.util.List;

record TestDataSeedResponse(
        String status,
        int deletedCount,
        int seededCount,
        List<ProbeResponse> probes
) {
}
