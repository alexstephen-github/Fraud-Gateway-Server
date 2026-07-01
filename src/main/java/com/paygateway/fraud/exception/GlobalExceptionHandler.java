package com.paygateway.fraud.exception;

import com.paygateway.fraud.model.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), req, null);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<ApiErrorResponse> handleUnprocessable(UnprocessableEntityException ex,
                                                                HttpServletRequest req) {
        List<ApiErrorResponse.ValidationError> errs = ex.getField() == null ? null
                : List.of(ApiErrorResponse.ValidationError.builder()
                        .field(ex.getField())
                        .message(ex.getMessage())
                        .build());
        ApiErrorResponse body = baseError("VALIDATION_ERROR", ex.getMessage(), req)
                .validationErrors(errs)
                .build();
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                            HttpServletRequest req) {
        List<ApiErrorResponse.ValidationError> errs = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> ApiErrorResponse.ValidationError.builder()
                        .field(fe.getField())
                        .message(fe.getDefaultMessage())
                        .build())
                .toList();
        ApiErrorResponse body = baseError("VALIDATION_ERROR", "Request validation failed", req)
                .validationErrors(errs)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadable(HttpMessageNotReadableException ex,
                                                            HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_REQUEST",
                "Malformed or unreadable request body", req, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please try again later.", req, null);
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String code, String message,
                                                   HttpServletRequest req,
                                                   List<ApiErrorResponse.ValidationError> errs) {
        return ResponseEntity.status(status)
                .body(baseError(code, message, req).validationErrors(errs).build());
    }

    private ApiErrorResponse.ApiErrorResponseBuilder baseError(String code, String message,
                                                               HttpServletRequest req) {
        return ApiErrorResponse.builder()
                .success(false)
                .errorCode(code)
                .message(message)
                .timestamp(OffsetDateTime.now())
                .path(req.getRequestURI());
    }
}
