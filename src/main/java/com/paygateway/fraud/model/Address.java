package com.paygateway.fraud.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Postal address")
public class Address {

    @Size(max = 200)
    private String line1;

    @Size(max = 200)
    private String line2;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Size(max = 20)
    private String postalCode;

    @Pattern(regexp = "^[A-Z]{2}$")
    @Schema(description = "ISO 3166-1 alpha-2 country code", example = "US")
    private String country;
}
