package br.rafaeros.smp.core.dto;

import br.rafaeros.smp.core.enums.Severity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Do not send null fields (e.g., date in case of a simple error)
public class ApiResponse<T> {

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    private String message;
    private Severity severity;
    private T data; // The returned Object, List, or Page
    private Object errors; // For field validation errors


    // Success with Data (GET, POST, PUT)
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .message(message)
                .severity(Severity.SUCCESS)
                .data(data)
                .build();
    }

    // Success without Data (DELETE ou ações simples)
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .message(message)
                .severity(Severity.SUCCESS)
                .build();
    }

    // @Valid error validation
    public static <T> ApiResponse<T> validationError(String message, Object errors) {
        return ApiResponse.<T>builder()
                .message(message)
                .severity(Severity.ERROR)
                .errors(errors)
                .build();
    }

    // Error (Used by Exception Handlers)
    public static <T> ApiResponse<T> error(String message, Severity severity) {
        return ApiResponse.<T>builder()
                .message(message)
                .severity(severity)
                .build();
    }
}