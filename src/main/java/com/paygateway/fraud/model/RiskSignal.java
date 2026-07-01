package com.paygateway.fraud.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Individual risk signal contributing to a fraud score")
public class RiskSignal {

    @Schema(description = "Signal type identifier")
    private SignalType type;

    @Schema(description = "Human-readable explanation of the signal")
    private String description;

    private Severity severity;

    public enum SignalType {
        @JsonProperty("ip_velocity") IP_VELOCITY,
        @JsonProperty("card_velocity") CARD_VELOCITY,
        @JsonProperty("email_velocity") EMAIL_VELOCITY,
        @JsonProperty("card_country_mismatch") CARD_COUNTRY_MISMATCH,
        @JsonProperty("billing_shipping_mismatch") BILLING_SHIPPING_MISMATCH,
        @JsonProperty("high_risk_country") HIGH_RISK_COUNTRY,
        @JsonProperty("proxy_detected") PROXY_DETECTED,
        @JsonProperty("unusual_amount") UNUSUAL_AMOUNT,
        @JsonProperty("repeated_decline") REPEATED_DECLINE
    }

    public enum Severity {
        @JsonProperty("low") LOW,
        @JsonProperty("medium") MEDIUM,
        @JsonProperty("high") HIGH
    }
}
