package com.paygateway.fraud.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Approve a held payment")
public class ApproveReviewRequest {

    @Size(max = 1000)
    @Schema(description = "Internal reviewer notes")
    private String notes;
}
