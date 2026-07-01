package com.paygateway.fraud.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Reject a held payment")
public class RejectReviewRequest {

    @Size(max = 500)
    @Schema(description = "Rejection reason for audit trail")
    private String reason;

    @Size(max = 1000)
    private String notes;
}
