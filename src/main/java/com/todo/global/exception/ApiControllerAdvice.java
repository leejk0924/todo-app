package com.todo.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = RestApiException.class)
    public ResponseEntity<ProblemDetail> handleRestApiException(RestApiException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        logRestApiException(errorCode, request, ex);

        return ResponseEntity
                .status(errorCode.status())
                .body(buildProblemDetail(errorCode));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ProblemDetail> handleException(Exception ex, HttpServletRequest request) {
        log.error("[UNEXPECTED] {} {}", request.getMethod(), request.getRequestURI(), ex);

        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(errorCode.status())
                .body(buildProblemDetail(errorCode));
    }

    private ProblemDetail buildProblemDetail(ErrorCode errorCode) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(errorCode.status(), errorCode.getMessage());
        problemDetail.setType(errorCode.type());
        problemDetail.setProperty("errorCode", errorCode.name());
        return problemDetail;
    }

    private void logRestApiException(ErrorCode errorCode, HttpServletRequest request, RestApiException ex) {
        if (errorCode.status().is5xxServerError()) {
            log.error("[{}] {} - {} {}", errorCode.name(), errorCode.getMessage(),
                    request.getMethod(), request.getRequestURI(), ex);
        } else {
            log.warn("[{}] {} - {} {}", errorCode.name(), errorCode.getMessage(),
                    request.getMethod(), request.getRequestURI());
        }
    }
}
