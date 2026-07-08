package com.example.demominiapp.e2e;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class SessionExceptionHandler {

    @ExceptionHandler(MissingTokenException.class)
    ResponseEntity<ErrorResponse> handleMissingToken() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("TOKEN_MISSING", "缺少测试会话 token"));
    }

    @ExceptionHandler(InvalidTokenException.class)
    ResponseEntity<ErrorResponse> handleInvalidToken() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("TOKEN_INVALID_OR_EXPIRED", "测试会话 token 无效或已过期"));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    ResponseEntity<ErrorResponse> handleInvalidCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("INVALID_CREDENTIALS", "测试账号或密码错误"));
    }

    @ExceptionHandler(InvalidProbeIdException.class)
    ResponseEntity<ErrorResponse> handleInvalidProbeId() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("PROBE_ID_NOT_TEST", "探针 ID 必须以 TEST_ 开头"));
    }

    @ExceptionHandler(ProbeNotFoundException.class)
    ResponseEntity<ErrorResponse> handleProbeNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("PROBE_NOT_FOUND", "未找到 TEST 探针数据"));
    }
}
