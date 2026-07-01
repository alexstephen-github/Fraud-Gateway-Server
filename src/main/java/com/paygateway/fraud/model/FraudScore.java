package com.paygateway.fraud.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "Real-time fraud risk score result")
public class FraudScore {

    @Schema(description = "Composite risk score. 0 = lowest risk, 100 = highest risk.", example = "12")
    private Integer score;

    @Schema(description = "Human-readable risk band")
    private RiskLevel riskLevel;

    @Schema(description = "Individual risk signals that contributed to the score")
    private List<RiskSignal> signals;

    @Schema(description = "Recommended action based on score and active fraud rules")
    private RiskAction action;

    @Schema(description = "ID of the first fraud rule that matched, if any")
    private String matchedRuleId;

    private OffsetDateTime createdAt;
}
