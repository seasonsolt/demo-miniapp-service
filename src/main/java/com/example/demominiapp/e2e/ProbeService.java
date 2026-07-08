package com.example.demominiapp.e2e;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Service
class ProbeService {

    private static final String TEST_PREFIX = "TEST_";

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

    private void requireTestProbeId(String probeId) {
        if (probeId == null || !probeId.startsWith(TEST_PREFIX)) {
            throw new InvalidProbeIdException();
        }
    }
}
