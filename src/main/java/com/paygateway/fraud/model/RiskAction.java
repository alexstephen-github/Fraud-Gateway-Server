package com.paygateway.fraud.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RiskAction {
    @JsonProperty("allow") ALLOW,
    @JsonProperty("review") REVIEW,
    @JsonProperty("block") BLOCK
}
