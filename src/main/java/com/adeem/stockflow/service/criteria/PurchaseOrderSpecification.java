package com.adeem.stockflow.service.criteria;

import com.adeem.stockflow.domain.PurchaseOrder;
import com.adeem.stockflow.domain.enumeration.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for filtering PurchaseOrder entities.
 */
public class PurchaseOrderSpecification {

    /**
     * Filter by purchase order reference.
     */
    public static Specification<PurchaseOrder> withReference(String reference) {
        return BaseSpecification.equals("reference", reference);
    }

    /**
     * Filter by purchase order status.
     */
    public static Specification<PurchaseOrder> withStatus(OrderStatus status) {
        return BaseSpecification.equals("status", status);
    }

    /**
     * Filter by purchase order date between two dates.
     */
    public static Specification<PurchaseOrder> withDateBetween(ZonedDateTime start, ZonedDateTime end) {
        return BaseSpecification.between("date", start, end);
    }

    /**
     * Filter by purchase order date before a specific date.
     */
    public static Specification<PurchaseOrder> withDateBefore(ZonedDateTime date) {
        return BaseSpecification.lessThan("date", date);
    }

    /**
     * Filter by purchase order date after a specific date.
     */
    public static Specification<PurchaseOrder> withDateAfter(ZonedDateTime date) {
        return BaseSpecification.greaterThan("date", date);
    }

    /**
     * Filter by purchase order total greater than or equal to a specific amount.
     */
    public static Specification<PurchaseOrder> withTotalGreaterThanOrEqual(BigDecimal total) {
        return BaseSpecification.greaterThanOrEqual("total", total);
    }

    /**
     * Filter by purchase order total less than or equal to a specific amount.
     */
    public static Specification<PurchaseOrder> withTotalLessThanOrEqual(BigDecimal total) {
        return BaseSpecification.lessThanOrEqual("total", total);
    }

    /**
     * Filter by purchase order total between a range.
     */
    public static Specification<PurchaseOrder> withTotalBetween(BigDecimal min, BigDecimal max) {
        return BaseSpecification.between("total", min, max);
    }

    /**
     * Filter by notes containing specific text.
     */
    public static Specification<PurchaseOrder> withNotesContaining(String text) {
        return BaseSpecification.contains("notes", text);
    }

    /**
     * Filter by supplier ID.
     */
    public static Specification<PurchaseOrder> withSupplierId(Long supplierId) {
        return (root, query, criteriaBuilder) -> {
            if (supplierId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("supplier", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), supplierId);
        };
    }

    /**
     * Filter by admin ID.
     */
    public static Specification<PurchaseOrder> withAdminId(Long adminId) {
        return (root, query, criteriaBuilder) -> {
            if (adminId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("admin", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), adminId);
        };
    }

    /**
     * Filter by client account ID.
     */
    public static Specification<PurchaseOrder> withClientAccountId(Long clientAccountId) {
        return (root, query, criteriaBuilder) -> {
            if (clientAccountId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("clientAccount", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), clientAccountId);
        };
    }

    /**
     * Filter by product ID (purchase orders containing the specified product).
     */
    public static Specification<PurchaseOrder> withProductId(Long productId) {
        return (root, query, criteriaBuilder) -> {
            if (productId == null) {
                return criteriaBuilder.conjunction();
            }
            var orderItemsJoin = root.join("orderItems", jakarta.persistence.criteria.JoinType.LEFT);
            var productJoin = orderItemsJoin.join("product", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(productJoin.get("id"), productId);
        };
    }

    /**
     * Filter by shipping cost greater than or equal to a specific amount.
     */
    public static Specification<PurchaseOrder> withShippingCostGreaterThanOrEqual(BigDecimal cost) {
        return BaseSpecification.greaterThanOrEqual("shipping", cost);
    }

    /**
     * Filter by handling cost greater than or equal to a specific amount.
     */
    public static Specification<PurchaseOrder> withHandlingCostGreaterThanOrEqual(BigDecimal cost) {
        return BaseSpecification.greaterThanOrEqual("handling", cost);
    }

    /**
     * Filter by mission fee greater than or equal to a specific amount.
     */
    public static Specification<PurchaseOrder> withMissionFeeGreaterThanOrEqual(BigDecimal fee) {
        return BaseSpecification.greaterThanOrEqual("missionFee", fee);
    }

    /**
     * Filter purchase orders that have discounts applied.
     */
    public static Specification<PurchaseOrder> hasDiscount() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.and(
                criteriaBuilder.isNotNull(root.get("discountRate")),
                criteriaBuilder.greaterThan(root.get("discountRate"), BigDecimal.ZERO)
            );
    }

    /**
     * Filter by created by user.
     */
    public static Specification<PurchaseOrder> withCreatedBy(String createdBy) {
        return BaseSpecification.equals("createdBy", createdBy);
    }

    /**
     * Filter by created date between two dates.
     */
    public static Specification<PurchaseOrder> withCreatedDateBetween(Instant start, Instant end) {
        return BaseSpecification.between("createdDate", start, end);
    }
}
