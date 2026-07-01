package com.paygateway.fraud.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response")
public class ApiErrorResponse {

    @Builder.Default
    private Boolean success = false;

    @JsonProperty("errorCode")
    @Schema(description = "Machine-readable error code", example = "VALIDATION_ERROR")
    private String errorCode;

    @Schema(description = "Human-readable error summary", example = "Invalid request parameters")
    private String message;

    @Schema(description = "Additional error context")
    private Details details;

    @JsonProperty("validationErrors")
    @Schema(description = "Field-level validation errors")
    private List<ValidationError> validationErrors;

    @Schema(description = "ISO 8601 timestamp of when the error occurred")
    private OffsetDateTime timestamp;

    @Schema(description = "Request path that caused the error")
    private String path;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Details {
        @Schema(description = "Request field that caused the error")
        private String param;
        @Schema(description = "Link to documentation for this error")
        private String docUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private String message;
    }
}
