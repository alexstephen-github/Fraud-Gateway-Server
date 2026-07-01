package com.paygateway.fraud.controller;

import com.paygateway.fraud.model.ApiErrorResponse;
import com.paygateway.fraud.model.FraudScore;
import com.paygateway.fraud.model.FraudScoreRequest;
import com.paygateway.fraud.service.FraudScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/fraud", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Risk Scoring", description = "Real-time fraud risk scoring for transactions")
public class RiskScoringController {

    private final FraudScoreService scoreService;

    public RiskScoringController(FraudScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping(value = "/score", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(operationId = "getFraudScore", summary = "Get fraud risk score for a transaction",
            description = "Returns a real-time risk score (0–100) and recommended action for a "
                    + "transaction before it is processed.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fraud score result"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed or missing",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected internal server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public FraudScore getFraudScore(@Valid @RequestBody FraudScoreRequest request) {
        return scoreService.score(request);
    }
}
