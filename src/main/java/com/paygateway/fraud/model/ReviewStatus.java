package com.paygateway.fraud.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReviewStatus {
    @JsonProperty("pending") PENDING,
    @JsonProperty("approved") APPROVED,
    @JsonProperty("rejected") REJECTED
}
