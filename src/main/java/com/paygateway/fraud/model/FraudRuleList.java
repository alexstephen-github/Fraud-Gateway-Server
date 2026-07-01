package com.paygateway.fraud.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "Paginated list of fraud rules")
public class FraudRuleList {

    @Builder.Default
    private String object = "list";

    private List<FraudRule> data;

    private ListMeta meta;
}
