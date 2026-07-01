package com.paygateway.fraud.service;

import com.paygateway.fraud.model.FraudScore;
import com.paygateway.fraud.model.FraudScoreRequest;
import com.paygateway.fraud.model.RiskAction;
import com.paygateway.fraud.model.RiskLevel;
import com.paygateway.fraud.model.RiskSignal;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Produces a deterministic mock fraud score. The score is derived from simple
 * heuristics over the request so responses are stable and demonstrable, while
 * still exercising the full response shape (signals, risk level, action).
 */
@Service
public class FraudScoreService {

    public FraudScore score(FraudScoreRequest request) {
        List<RiskSignal> signals = new ArrayList<>();
        int score = 5;

        // High amount raises risk.
        if (request.getAmount() != null && request.getAmount() >= 100_000) {
            score += 35;
            signals.add(new RiskSignal(RiskSignal.SignalType.UNUSUAL_AMOUNT,
                    "Transaction amount is unusually high", RiskSignal.Severity.HIGH));
        } else if (request.getAmount() != null && request.getAmount() >= 20_000) {
            score += 15;
            signals.add(new RiskSignal(RiskSignal.SignalType.UNUSUAL_AMOUNT,
                    "Transaction amount above typical range", RiskSignal.Severity.MEDIUM));
        }

        // Derive a stable pseudo-velocity from the IP string.
        if (request.getCustomerIp() != null && !request.getCustomerIp().isBlank()) {
            int velocity = Math.floorMod(request.getCustomerIp().hashCode(), 20);
            if (velocity > 10) {
                score += 30;
                signals.add(new RiskSignal(RiskSignal.SignalType.IP_VELOCITY,
                        "IP address used in " + velocity + " transactions in last hour",
                        RiskSignal.Severity.HIGH));
            }
        }

        // Email domain heuristic.
        if (request.getCustomerEmail() != null && request.getCustomerEmail().contains("+")) {
            score += 10;
            signals.add(new RiskSignal(RiskSignal.SignalType.EMAIL_VELOCITY,
                    "Email uses sub-addressing associated with velocity abuse",
                    RiskSignal.Severity.MEDIUM));
        }

        score = Math.min(100, Math.max(0, score));

        return FraudScore.builder()
                .score(score)
                .riskLevel(toRiskLevel(score))
                .signals(signals)
                .action(toAction(score))
                .createdAt(OffsetDateTime.now())
                .build();
    }

    private RiskLevel toRiskLevel(int score) {
        if (score <= 24) return RiskLevel.LOW;
        if (score <= 59) return RiskLevel.MEDIUM;
        if (score <= 84) return RiskLevel.HIGH;
        return RiskLevel.VERY_HIGH;
    }

    private RiskAction toAction(int score) {
        if (score <= 24) return RiskAction.ALLOW;
        if (score <= 84) return RiskAction.REVIEW;
        return RiskAction.BLOCK;
    }
}
