package com.paygateway.fraud.controller;

import com.paygateway.fraud.model.CreateFraudRuleRequest;
import com.paygateway.fraud.model.FraudRule;
import com.paygateway.fraud.model.FraudRuleList;
import com.paygateway.fraud.model.RiskAction;
import com.paygateway.fraud.model.UpdateFraudRuleRequest;
import com.paygateway.fraud.service.FraudRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/fraud/rules", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Fraud Rules", description = "Configurable fraud detection rules (block, allow, review)")
public class FraudRulesController {

    private final FraudRuleService ruleService;

    public FraudRulesController(FraudRuleService ruleService) {
        this.ruleService = ruleService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(operationId = "createFraudRule", summary = "Create a fraud detection rule",
            description = "Rules are evaluated in ascending priority order. The first matching "
                    + "rule's action is applied to the transaction.")
    public ResponseEntity<FraudRule> createFraudRule(@Valid @RequestBody CreateFraudRuleRequest request,
                                                     UriComponentsBuilder uriBuilder) {
        FraudRule rule = ruleService.create(request);
        URI location = uriBuilder.path("/fraud/rules/{id}").buildAndExpand(rule.getId()).toUri();
        return ResponseEntity.created(location).body(rule);
    }

    @GetMapping
    @Operation(operationId = "listFraudRules", summary = "List fraud rules")
    public FraudRuleList listFraudRules(
            @Parameter(description = "Filter by rule action")
            @RequestParam(required = false) RiskAction action,
            @Parameter(description = "Filter by active/inactive status")
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "20") int limit,
            @Parameter(description = "Cursor for forward pagination")
            @RequestParam(required = false, name = "starting_after") String startingAfter) {
        return ruleService.list(action, active, limit, startingAfter);
    }

    @GetMapping("/{ruleId}")
    @Operation(operationId = "getFraudRule", summary = "Retrieve a fraud rule")
    public FraudRule getFraudRule(
            @Parameter(description = "Unique fraud rule identifier") @PathVariable String ruleId) {
        return ruleService.get(ruleId);
    }

    @PutMapping(value = "/{ruleId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(operationId = "updateFraudRule", summary = "Update a fraud rule")
    public FraudRule updateFraudRule(@PathVariable String ruleId,
                                     @Valid @RequestBody UpdateFraudRuleRequest request) {
        return ruleService.update(ruleId, request);
    }

    @DeleteMapping("/{ruleId}")
    @Operation(operationId = "deleteFraudRule", summary = "Delete a fraud rule")
    public ResponseEntity<Void> deleteFraudRule(@PathVariable String ruleId) {
        ruleService.delete(ruleId);
        return ResponseEntity.noContent().build();
    }
}
