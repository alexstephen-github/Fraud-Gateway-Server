package com.paygateway.fraud.service;

import com.paygateway.fraud.exception.NotFoundException;
import com.paygateway.fraud.exception.UnprocessableEntityException;
import com.paygateway.fraud.model.FraudReview;
import com.paygateway.fraud.model.FraudReviewList;
import com.paygateway.fraud.model.ListMeta;
import com.paygateway.fraud.model.ReviewStatus;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FraudReviewService {

    private final Map<String, FraudReview> store = new ConcurrentHashMap<>();

    @PostConstruct
    void seed() {
        FraudReview r1 = FraudReview.builder()
                .id("rev_seed0000000001")
                .paymentId("pay_seed0000000001")
                .status(ReviewStatus.PENDING)
                .reason("High velocity from this IP in the last 24 hours")
                .riskScore(72)
                .createdAt(OffsetDateTime.parse("2026-06-24T10:00:00Z"))
                .build();
        FraudReview r2 = FraudReview.builder()
                .id("rev_seed0000000002")
                .paymentId("pay_seed0000000002")
                .status(ReviewStatus.APPROVED)
                .reason("Card country mismatch")
                .notes("Verified with customer over phone")
                .reviewerId("usr_reviewer01")
                .reviewedAt(OffsetDateTime.parse("2026-06-25T09:15:00Z"))
                .riskScore(64)
                .createdAt(OffsetDateTime.parse("2026-06-25T08:00:00Z"))
                .build();
        store.put(r1.getId(), r1);
        store.put(r2.getId(), r2);
    }

    public FraudReview create(String paymentId, String reason) {
        FraudReview review = FraudReview.builder()
                .id(IdGenerator.withPrefix("rev_"))
                .paymentId(paymentId)
                .status(ReviewStatus.PENDING)
                .reason(reason)
                .riskScore(Math.floorMod((paymentId == null ? "" : paymentId).hashCode(), 40) + 55)
                .createdAt(OffsetDateTime.now())
                .build();
        store.put(review.getId(), review);
        return review;
    }

    public FraudReview get(String reviewId) {
        FraudReview review = store.get(reviewId);
        if (review == null) {
            throw new NotFoundException("Fraud review '" + reviewId + "' was not found");
        }
        return review;
    }

    public FraudReview approve(String reviewId, String notes) {
        FraudReview review = get(reviewId);
        requirePending(review);
        review.setStatus(ReviewStatus.APPROVED);
        review.setNotes(notes);
        review.setReviewerId("usr_reviewer01");
        review.setReviewedAt(OffsetDateTime.now());
        return review;
    }

    public FraudReview reject(String reviewId, String reason, String notes) {
        FraudReview review = get(reviewId);
        requirePending(review);
        review.setStatus(ReviewStatus.REJECTED);
        if (reason != null) {
            review.setReason(reason);
        }
        review.setNotes(notes);
        review.setReviewerId("usr_reviewer01");
        review.setReviewedAt(OffsetDateTime.now());
        return review;
    }

    public FraudReviewList list(ReviewStatus status, String paymentId,
                                OffsetDateTime createdGte, OffsetDateTime createdLte,
                                int limit, String startingAfter) {
        List<FraudReview> all = new ArrayList<>(store.values());
        all.sort(Comparator.comparing(FraudReview::getCreatedAt).reversed());

        List<FraudReview> filtered = all.stream()
                .filter(r -> status == null || r.getStatus() == status)
                .filter(r -> paymentId == null || paymentId.equals(r.getPaymentId()))
                .filter(r -> createdGte == null || !r.getCreatedAt().isBefore(createdGte))
                .filter(r -> createdLte == null || !r.getCreatedAt().isAfter(createdLte))
                .toList();

        int startIndex = 0;
        if (startingAfter != null) {
            for (int i = 0; i < filtered.size(); i++) {
                if (filtered.get(i).getId().equals(startingAfter)) {
                    startIndex = i + 1;
                    break;
                }
            }
        }

        int endIndex = Math.min(startIndex + limit, filtered.size());
        List<FraudReview> page = startIndex >= filtered.size()
                ? List.of() : filtered.subList(startIndex, endIndex);

        return FraudReviewList.builder()
                .data(page)
                .meta(ListMeta.builder()
                        .hasMore(endIndex < filtered.size())
                        .totalCount(filtered.size())
                        .url("/fraud/review")
                        .build())
                .build();
    }

    private void requirePending(FraudReview review) {
        if (review.getStatus() != ReviewStatus.PENDING) {
            throw new UnprocessableEntityException("status",
                    "Review is not in a pending/reviewable state");
        }
    }
}
