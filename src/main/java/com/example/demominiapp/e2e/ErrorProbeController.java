package com.example.demominiapp.e2e;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/e2e/error-probe")
class ErrorProbeController {

    @GetMapping("/{errorType}")
    ResponseEntity<ErrorResponse> triggerError(@PathVariable String errorType) {
        if ("bad-request".equals(errorType)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("E2E_BAD_REQUEST", "稳定 400 错误探针"));
        }
        if ("server-error".equals(errorType)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("E2E_SERVER_ERROR", "稳定 500 错误探针"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("E2E_ERROR_TYPE_UNSUPPORTED", "不支持的错误探针类型"));
    }
}
