package com.uom.Software_design_competition.application.util.exception;

import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;
import com.uom.Software_design_competition.application.util.resultenum.ResponseCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException ex) {
        log.error("BaseException occurred: {}", ex.getMessage(), ex);
        ApiResponse<Void> response = new ApiResponse<>(ex.getResponseCode(), ex.getMessage());
        return ResponseEntity.status(getHttpStatus(ex.getResponseCode())).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("Validation failed: {}", errors);
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
            ResponseCodeEnum.BAD_REQUEST_INVALID_FIELDS.code(),
            "Validation failed",
            errors
        );
        
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        ApiResponse<Void> response = new ApiResponse<>(
            ResponseCodeEnum.INTERNAL_SERVER_ERROR.code(),
            "An unexpected error occurred"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private HttpStatus getHttpStatus(String errorCode) {
        return switch (errorCode) {
            case "4000", "4001" -> HttpStatus.BAD_REQUEST;
            case "4003" -> HttpStatus.UNPROCESSABLE_ENTITY;
            case "4004" -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
