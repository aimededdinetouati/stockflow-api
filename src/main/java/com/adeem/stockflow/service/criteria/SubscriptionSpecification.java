package com.adeem.stockflow.service.criteria;

import com.adeem.stockflow.domain.Subscription;
import com.adeem.stockflow.domain.enumeration.BillingCycle;
import com.adeem.stockflow.domain.enumeration.SubscriptionStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for filtering Subscription entities.
 */
public class SubscriptionSpecification {

    /**
     * Filter by subscription status.
     */
    public static Specification<Subscription> withStatus(SubscriptionStatus status) {
        return BaseSpecification.equals("status", status);
    }

    /**
     * Filter by start date between two dates.
     */
    public static Specification<Subscription> withStartDateBetween(ZonedDateTime start, ZonedDateTime end) {
        return BaseSpecification.between("startDate", start, end);
    }

    /**
     * Filter by end date between two dates.
     */
    public static Specification<Subscription> withEndDateBetween(ZonedDateTime start, ZonedDateTime end) {
        return BaseSpecification.between("endDate", start, end);
    }

    /**
     * Filter subscriptions that are expiring soon (end date before a specific date).
     */
    public static Specification<Subscription> expiringBefore(ZonedDateTime date) {
        return BaseSpecification.lessThan("endDate", date);
    }

    /**
     * Filter active subscriptions that are expiring soon.
     */
    public static Specification<Subscription> activeAndExpiringBefore(ZonedDateTime date) {
        return Specification.where(withStatus(SubscriptionStatus.ACTIVE)).and(expiringBefore(date));
    }

    /**
     * Filter by payment method.
     */
    public static Specification<Subscription> withPaymentMethod(String paymentMethod) {
        return BaseSpecification.equals("paymentMethod", paymentMethod);
    }

    /**
     * Filter by actual price greater than or equal to a specific amount.
     */
    public static Specification<Subscription> withActualPriceGreaterThanOrEqual(BigDecimal price) {
        return BaseSpecification.greaterThanOrEqual("actualPrice", price);
    }

    /**
     * Filter by actual price less than or equal to a specific amount.
     */
    public static Specification<Subscription> withActualPriceLessThanOrEqual(BigDecimal price) {
        return BaseSpecification.lessThanOrEqual("actualPrice", price);
    }

    /**
     * Filter by actual price between a range.
     */
    public static Specification<Subscription> withActualPriceBetween(BigDecimal min, BigDecimal max) {
        return BaseSpecification.between("actualPrice", min, max);
    }

    /**
     * Filter by client account ID.
     */
    public static Specification<Subscription> withClientAccountId(Long clientAccountId) {
        return (root, query, criteriaBuilder) -> {
            if (clientAccountId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("clientAccount", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), clientAccountId);
        };
    }

    /**
     * Filter by plan formula ID.
     */
    public static Specification<Subscription> withPlanFormulaId(Long planFormulaId) {
        return (root, query, criteriaBuilder) -> {
            if (planFormulaId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("planFormula", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), planFormulaId);
        };
    }

    /**
     * Filter by plan formula name.
     */
    public static Specification<Subscription> withPlanFormulaName(String planFormulaName) {
        return (root, query, criteriaBuilder) -> {
            if (planFormulaName == null || planFormulaName.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("planFormula", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(join.get("name")), "%" + planFormulaName.toLowerCase() + "%");
        };
    }

    /**
     * Filter by plan formula billing cycle.
     */
    public static Specification<Subscription> withPlanFormulaBillingCycle(BillingCycle billingCycle) {
        return (root, query, criteriaBuilder) -> {
            if (billingCycle == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("planFormula", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("billingCycle"), billingCycle);
        };
    }

    /**
     * Filter subscriptions by created by user.
     */
    public static Specification<Subscription> withCreatedBy(String createdBy) {
        return BaseSpecification.equals("createdBy", createdBy);
    }

    /**
     * Filter subscriptions by created date between two dates.
     */
    public static Specification<Subscription> withCreatedDateBetween(Instant start, Instant end) {
        return BaseSpecification.between("createdDate", start, end);
    }

    /**
     * Filter subscriptions by last modified by user.
     */
    public static Specification<Subscription> withLastModifiedBy(String lastModifiedBy) {
        return BaseSpecification.equals("lastModifiedBy", lastModifiedBy);
    }

    /**
     * Filter subscriptions by last modified date between two dates.
     */
    public static Specification<Subscription> withLastModifiedDateBetween(Instant start, Instant end) {
        return BaseSpecification.between("lastModifiedDate", start, end);
    }
}
