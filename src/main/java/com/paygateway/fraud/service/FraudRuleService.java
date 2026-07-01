package com.paygateway.fraud.service;

import com.paygateway.fraud.exception.NotFoundException;
import com.paygateway.fraud.model.CreateFraudRuleRequest;
import com.paygateway.fraud.model.FraudRule;
import com.paygateway.fraud.model.FraudRuleList;
import com.paygateway.fraud.model.ListMeta;
import com.paygateway.fraud.model.RiskAction;
import com.paygateway.fraud.model.RuleCondition;
import com.paygateway.fraud.model.UpdateFraudRuleRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FraudRuleService {

    private final Map<String, FraudRule> store = new ConcurrentHashMap<>();

    @PostConstruct
    void seed() {
        FraudRule rule = FraudRule.builder()
                .id("fr_seed0000000001")
                .name("IP Velocity Review")
                .action(RiskAction.REVIEW)
                .conditions(List.of(velocityCondition()))
                .priority(20)
                .active(true)
                .matchCount(42)
                .description("Review transactions from high-velocity IP addresses")
                .createdAt(OffsetDateTime.parse("2026-06-01T00:00:00Z"))
                .updatedAt(OffsetDateTime.parse("2026-06-01T00:00:00Z"))
                .build();
        store.put(rule.getId(), rule);
    }

    private RuleCondition velocityCondition() {
        RuleCondition c = new RuleCondition();
        c.setField(RuleCondition.Field.VELOCITY_IP_COUNT);
        c.setOperator(RuleCondition.Operator.GT);
        c.setValue(10);
        c.setWindowSeconds(3600);
        return c;
    }

    public FraudRule create(CreateFraudRuleRequest request) {
        OffsetDateTime now = OffsetDateTime.now();
        FraudRule rule = FraudRule.builder()
                .id(IdGenerator.withPrefix("fr_"))
                .name(request.getName())
                .action(request.getAction())
                .conditions(request.getConditions())
                .priority(request.getPriority() == null ? 100 : request.getPriority())
                .active(true)
                .matchCount(0)
                .description(request.getDescription())
                .metadata(request.getMetadata())
                .createdAt(now)
                .updatedAt(now)
                .build();
        store.put(rule.getId(), rule);
        return rule;
    }

    public FraudRule get(String ruleId) {
        FraudRule rule = store.get(ruleId);
        if (rule == null) {
            throw new NotFoundException("Fraud rule '" + ruleId + "' was not found");
        }
        return rule;
    }

    public FraudRule update(String ruleId, UpdateFraudRuleRequest request) {
        FraudRule rule = get(ruleId);
        if (request.getName() != null) rule.setName(request.getName());
        if (request.getAction() != null) rule.setAction(request.getAction());
        if (request.getConditions() != null) rule.setConditions(request.getConditions());
        if (request.getPriority() != null) rule.setPriority(request.getPriority());
        if (request.getActive() != null) rule.setActive(request.getActive());
        if (request.getDescription() != null) rule.setDescription(request.getDescription());
        if (request.getMetadata() != null) rule.setMetadata(request.getMetadata());
        rule.setUpdatedAt(OffsetDateTime.now());
        return rule;
    }

    public void delete(String ruleId) {
        if (store.remove(ruleId) == null) {
            throw new NotFoundException("Fraud rule '" + ruleId + "' was not found");
        }
    }

    public FraudRuleList list(RiskAction action, Boolean active, int limit, String startingAfter) {
        List<FraudRule> all = new ArrayList<>(store.values());
        all.sort(Comparator.comparing(FraudRule::getPriority,
                Comparator.nullsLast(Comparator.naturalOrder())));

        List<FraudRule> filtered = all.stream()
                .filter(r -> action == null || r.getAction() == action)
                .filter(r -> active == null || active.equals(r.getActive()))
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
        List<FraudRule> page = startIndex >= filtered.size()
                ? List.of() : filtered.subList(startIndex, endIndex);

        return FraudRuleList.builder()
                .data(page)
                .meta(ListMeta.builder()
                        .hasMore(endIndex < filtered.size())
                        .totalCount(filtered.size())
                        .url("/fraud/rules")
                        .build())
                .build();
    }
}
