package com.paygateway.fraud.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RiskLevel {
    @JsonProperty("low") LOW,
    @JsonProperty("medium") MEDIUM,
    @JsonProperty("high") HIGH,
    @JsonProperty("very_high") VERY_HIGH
}
