package br.rafaeros.smp.core.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.rafaeros.smp.core.dto.ApiResponse;
import br.rafaeros.smp.core.enums.Severity;

@RestControllerAdvice
public class GlobalExceptionHandler {


    // Disables Invalid Credentials (Incorrect Login) -> 401 Unauthorized
    @ExceptionHandler({BadCredentialsException.class, InternalAuthenticationServiceException.class})
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Usuário ou senha inválidos.", Severity.ERROR));
    }

    // Handles Resource Not Found (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        // Using the static helper method created in the DTO
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), Severity.WARNING));
    }

    // Handles Business Exception (400)
    // Note: Consider renaming the class to BusinessException (typo fix) if possible
    @ExceptionHandler(BussinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BussinessException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), Severity.WARNING));
    }

    // Handles @Valid validations (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        // Using the builder/helper to include the map of errors
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.validationError("Invalid data provided.", fieldErrors));
    }

    // (Optional) Internal Server Error Fallback (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        ex.printStackTrace(); // It is good practice to log the actual error in the console/logs

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An internal server error occurred.", Severity.ERROR));
    }
}