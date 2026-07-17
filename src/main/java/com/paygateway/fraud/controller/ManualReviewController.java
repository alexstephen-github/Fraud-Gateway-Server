package com.paygateway.fraud.controller;

import com.paygateway.fraud.model.ApproveReviewRequest;
import com.paygateway.fraud.model.FraudReview;
import com.paygateway.fraud.model.FraudReviewList;
import com.paygateway.fraud.model.RejectReviewRequest;
import com.paygateway.fraud.model.ReviewStatus;
import com.paygateway.fraud.model.TriggerReviewRequest;
import com.paygateway.fraud.service.FraudReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.OffsetDateTime;

@RestController
@RequestMapping(value = "/fraud/review", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Manual Review", description = "Flag payments for manual review and approve/reject workflows")
public class ManualReviewController {

    private final FraudReviewService reviewService;

    public ManualReviewController(FraudReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(operationId = "triggerFraudReview", summary = "Flag a payment for manual review",
            description = "Places a payment in review status and queues it for manual inspection.")
    public ResponseEntity<FraudReview> triggerFraudReview(@Valid @RequestBody TriggerReviewRequest request,
                                                          UriComponentsBuilder uriBuilder) {
        FraudReview review = reviewService.create(request.getPaymentId(), request.getReason());
        URI location = uriBuilder.path("/fraud/review/{id}").buildAndExpand(review.getId()).toUri();
        return ResponseEntity.created(location).body(review);
    }

    @GetMapping
    @Operation(operationId = "listFraudReviews", summary = "List fraud reviews",
            description = "Returns a paginated list of fraud reviews. Supports filtering by review "
                    + "status (pending, approved, rejected), payment ID, and creation date range. "
                    + "Use cursor-based pagination with the starting_after parameter for large result sets.")
    public FraudReviewList listFraudReviews(
            @Parameter(description = "Filter by review status")
            @RequestParam(required = false) ReviewStatus status,
            @Parameter(description = "Filter by payment ID")
            @RequestParam(required = false, name = "payment_id") String paymentId,
            @Parameter(description = "Filter by created date >= value (ISO 8601)")
            @RequestParam(required = false, name = "created[gte]")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime createdGte,
            @Parameter(description = "Filter by created date <= value (ISO 8601)")
            @RequestParam(required = false, name = "created[lte]")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime createdLte,
            @RequestParam(defaultValue = "20") int limit,
            @Parameter(description = "Cursor for forward pagination")
            @RequestParam(required = false, name = "starting_after") String startingAfter) {
        return reviewService.list(status, paymentId, createdGte, createdLte, limit, startingAfter);
    }

    @GetMapping("/{reviewId}")
    @Operation(operationId = "getFraudReview", summary = "Retrieve a fraud review",
            description = "Fetches the full details of a single fraud review by its unique identifier, "
                    + "including current status, associated payment, risk score at time of flagging, "
                    + "and reviewer notes if the review has been actioned.")
    public FraudReview getFraudReview(
            @Parameter(description = "Unique fraud review identifier") @PathVariable String reviewId) {
        return reviewService.get(reviewId);
    }

    @PostMapping(value = "/{reviewId}/approve", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(operationId = "approveFraudReview", summary = "Approve a manually reviewed transaction",
            description = "Approves the held payment and allows it to proceed to settlement.")
    public FraudReview approveFraudReview(@PathVariable String reviewId,
                                          @RequestBody(required = false) @Valid ApproveReviewRequest request) {
        String notes = request == null ? null : request.getNotes();
        return reviewService.approve(reviewId, notes);
    }

    @PostMapping(value = "/{reviewId}/reject", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(operationId = "rejectFraudReview", summary = "Reject a manually reviewed transaction",
            description = "Rejects and cancels the held payment, releasing any authorization hold.")
    public FraudReview rejectFraudReview(@PathVariable String reviewId,
                                         @RequestBody(required = false) @Valid RejectReviewRequest request) {
        String reason = request == null ? null : request.getReason();
        String notes = request == null ? null : request.getNotes();
        return reviewService.reject(reviewId, reason, notes);
    }
}
