package com.paygateway.fraud.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request to flag a payment for manual review")
public class TriggerReviewRequest {

    @NotBlank
    @Schema(description = "ID of the payment to flag", example = "pay_1A2B3C4D")
    private String paymentId;

    @Size(max = 500)
    @Schema(description = "Reason for flagging", example = "High velocity from this IP in the last 24 hours")
    private String reason;
}
