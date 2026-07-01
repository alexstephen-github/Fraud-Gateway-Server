package com.paygateway.fraud.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "A single condition evaluated by a fraud rule")
public class RuleCondition {

    @NotNull
    @Schema(description = "Transaction attribute to evaluate")
    private Field field;

    @NotNull
    @Schema(description = "Comparison operator")
    private Operator operator;

    @NotNull
    @Schema(description = "Value or list of values to compare against. May be a string, number, or array of strings.")
    private Object value;

    @Schema(description = "Time window for velocity fields (in seconds)", example = "3600")
    private Integer windowSeconds;

    public enum Field {
        @JsonProperty("card_country") CARD_COUNTRY,
        @JsonProperty("ip_country") IP_COUNTRY,
        @JsonProperty("amount") AMOUNT,
        @JsonProperty("currency") CURRENCY,
        @JsonProperty("velocity_card_count") VELOCITY_CARD_COUNT,
        @JsonProperty("velocity_email_count") VELOCITY_EMAIL_COUNT,
        @JsonProperty("velocity_ip_count") VELOCITY_IP_COUNT,
        @JsonProperty("card_brand") CARD_BRAND,
        @JsonProperty("card_funding") CARD_FUNDING
    }

    public enum Operator {
        @JsonProperty("eq") EQ,
        @JsonProperty("neq") NEQ,
        @JsonProperty("gt") GT,
        @JsonProperty("gte") GTE,
        @JsonProperty("lt") LT,
        @JsonProperty("lte") LTE,
        @JsonProperty("in") IN,
        @JsonProperty("not_in") NOT_IN
    }
}
