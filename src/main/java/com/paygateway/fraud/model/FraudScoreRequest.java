package com.paygateway.fraud.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request payload to score a transaction for fraud risk")
public class FraudScoreRequest {

    @NotBlank
    @Schema(description = "Payment method token or saved payment method ID", example = "pm_1A2B3C")
    private String paymentMethod;

    @NotNull
    @Schema(description = "Transaction amount in smallest currency unit", example = "5000")
    private Integer amount;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{3}$")
    @Schema(description = "ISO 4217 currency code", example = "USD")
    private String currency;

    @Schema(description = "Customer IP address for geo and velocity checks", example = "203.0.113.42")
    private String customerIp;

    @Schema(description = "Customer email for email velocity checks", example = "buyer@example.com")
    private String customerEmail;

    @Valid
    private Address billingAddress;

    @Size(max = 500)
    @Schema(description = "Browser user-agent for device fingerprinting")
    private String userAgent;
}
