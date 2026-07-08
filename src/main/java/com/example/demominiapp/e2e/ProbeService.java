package com.example.demominiapp.e2e;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
class ProbeService {

    private static final String TEST_PREFIX = "TEST_";
    private static final String SEED_PROBE_ID = "TEST_SEED_PROBE";
    private static final String SEED_PROBE_VALUE = "seed-value-miniapp";

    private final Clock clock = Clock.systemUTC();
    private final ProbeRepository probeRepository;

    ProbeService(ProbeRepository probeRepository) {
        this.probeRepository = probeRepository;
    }

    ProbeResponse write(String probeId, ProbeRequest request, String account) {
        requireTestProbeId(probeId);
        String value = request == null || request.value() == null ? "" : request.value();
        return probeRepository.save(new ProbeResponse(
                probeId,
                value,
                account,
                Instant.now(clock).toString()
        ));
    }

    ProbeResponse read(String probeId) {
        requireTestProbeId(probeId);
        return probeRepository.findById(probeId)
                .orElseThrow(ProbeNotFoundException::new);
    }

    ProbeDeleteResponse reset(String probeId) {
        requireTestProbeId(probeId);
        return new ProbeDeleteResponse(probeId, probeRepository.deleteById(probeId));
    }

    TestDataResetResponse resetTestData() {
        int deletedCount = probeRepository.deleteByPrefix(TEST_PREFIX);
        return new TestDataResetResponse("RESET_DONE", deletedCount);
    }

    TestDataSeedResponse seedTestData(String account) {
        int deletedCount = probeRepository.deleteByPrefix(TEST_PREFIX);
        ProbeResponse probe = probeRepository.save(new ProbeResponse(
                SEED_PROBE_ID,
                SEED_PROBE_VALUE,
                account,
                Instant.now(clock).toString()
        ));
        return new TestDataSeedResponse("SEED_DONE", deletedCount, 1, List.of(probe));
    }

    private void requireTestProbeId(String probeId) {
        if (probeId == null || !probeId.startsWith(TEST_PREFIX)) {
            throw new InvalidProbeIdException();
        }
    }
}
