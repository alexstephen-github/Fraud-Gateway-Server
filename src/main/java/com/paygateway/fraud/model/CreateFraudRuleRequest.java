package com.paygateway.fraud.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "Request to create a fraud detection rule")
public class CreateFraudRuleRequest {

    @NotNull
    @Size(max = 100)
    @Schema(description = "Descriptive rule name", example = "Block High-Risk Card Countries")
    private String name;

    @NotNull
    @Schema(description = "Action to take when the rule matches")
    private RiskAction action;

    @NotEmpty
    @Size(min = 1, max = 10)
    @Valid
    @Schema(description = "All conditions must match (AND logic) for the rule to trigger")
    private List<RuleCondition> conditions;

    @Min(1)
    @Max(1000)
    @Schema(description = "Evaluation order. Lower numbers are evaluated first.", example = "10")
    private Integer priority;

    @Size(max = 500)
    private String description;

    @Schema(description = "Key-value pairs for custom data")
    private Map<String, String> metadata;
}
