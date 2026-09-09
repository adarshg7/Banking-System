package com.bank.common.exception;

import com.bank.common.constants.ErrorCodes;
import com.bank.common.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice   // <- this annotation means: "watch ALL controllers, everywhere"
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>>  handleBusinessException(BusinessException ex) {
        log.warn("Business exception: {}-{}", ex.getHttpStatus(), ex.getMessage());

        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ApiResponse.error(ex.getMessage(),ex.getErrorCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex){
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("Validation failed: {}",details);

        ErrorResponse error = ErrorResponse.builder()
                .errorCode(ErrorCodes.VALIDATION_FAILED)
                .message("Validation failed")
                .details(details)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex){
        log.warn("Access denied: {}",ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("You do not have permission to perform this action",
                        ErrorCodes.FORBIDDEN));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex){
        log.warn("Constraint violation : {}",ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(),ErrorCodes.VALIDATION_FAILED));
    }

    @ExceptionHandler(Exception.class)   // catch-all safety net
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred",ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred. Please try again later.", ErrorCodes.INTERNAL_SERVER_ERROR));
    }
}