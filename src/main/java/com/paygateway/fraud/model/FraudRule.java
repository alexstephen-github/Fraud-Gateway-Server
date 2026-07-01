package com.paygateway.fraud.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@Schema(description = "A configured fraud detection rule")
public class FraudRule {

    @Schema(description = "Unique rule identifier. Prefix fr_", example = "fr_4V5W6X")
    private String id;

    @Builder.Default
    private String object = "fraud_rule";

    private String name;

    private RiskAction action;

    private List<RuleCondition> conditions;

    private Integer priority;

    private Boolean active;

    @Schema(description = "Total number of transactions matched by this rule")
    private Integer matchCount;

    private String description;

    private Map<String, String> metadata;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}
