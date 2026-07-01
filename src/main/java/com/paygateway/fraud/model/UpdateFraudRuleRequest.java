package com.paygateway.fraud.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "Request to update a fraud rule. Only supplied fields are changed.")
public class UpdateFraudRuleRequest {

    @Size(max = 100)
    private String name;

    private RiskAction action;

    @Valid
    private List<RuleCondition> conditions;

    @Min(1)
    @Max(1000)
    private Integer priority;

    @Schema(description = "Enable or disable the rule without deleting it")
    private Boolean active;

    private String description;

    private Map<String, String> metadata;
}
