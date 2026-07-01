package com.paygateway.fraud.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
@Schema(description = "A fraud review record")
public class FraudReview {

    @Schema(description = "Unique review identifier. Prefix rev_", example = "rev_1A2B3C")
    private String id;

    @Builder.Default
    private String object = "fraud_review";

    @Schema(description = "Associated payment ID")
    private String paymentId;

    @Schema(description = "Current review status")
    private ReviewStatus status;

    @Schema(description = "Reason the payment was flagged for review")
    private String reason;

    @Schema(description = "Internal reviewer notes")
    private String notes;

    @Schema(description = "ID of the reviewer who approved/rejected (if actioned)")
    private String reviewerId;

    @Schema(description = "Timestamp when the review was actioned")
    private OffsetDateTime reviewedAt;

    @Schema(description = "Risk score at the time of flagging")
    private Integer riskScore;

    private OffsetDateTime createdAt;
}
