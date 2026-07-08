package com.example.demominiapp.e2e;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/e2e/probes")
class ProbeController {

    private final TestSessionService sessionService;
    private final ProbeService probeService;

    ProbeController(TestSessionService sessionService, ProbeService probeService) {
        this.sessionService = sessionService;
        this.probeService = probeService;
    }

    @PutMapping("/{probeId}")
    ProbeResponse writeProbe(
            @PathVariable String probeId,
            @RequestBody ProbeRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        TestSession session = sessionService.requireSession(authorizationHeader);
        return probeService.write(probeId, request, session.account());
    }

    @GetMapping("/{probeId}")
    ProbeResponse readProbe(
            @PathVariable String probeId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        sessionService.requireSession(authorizationHeader);
        return probeService.read(probeId);
    }

    @DeleteMapping("/{probeId}")
    ProbeDeleteResponse resetProbe(
            @PathVariable String probeId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        sessionService.requireSession(authorizationHeader);
        return probeService.reset(probeId);
    }
}
