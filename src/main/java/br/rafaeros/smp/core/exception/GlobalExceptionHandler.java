package br.rafaeros.smp.core.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex,
            HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                Severity.WARNING,
                "Resource Not Found",
                ex.getMessage(),
                request);
    }

    @ExceptionHandler(BussinessException.class)
    public ResponseEntity<Map<String, Object>> handleBussinessException(BussinessException ex,
            HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                Severity.WARNING,
                "Bussiness Exception",
                ex.getMessage(),
                request);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, Severity severity,
            String errorType, String message, HttpServletRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("severity", severity);
        body.put("errorType", errorType);
        body.put("message", message);
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
