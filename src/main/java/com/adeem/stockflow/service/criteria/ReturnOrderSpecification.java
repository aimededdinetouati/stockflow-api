package com.adeem.stockflow.service.criteria;

import com.adeem.stockflow.domain.ReturnOrder;
import com.adeem.stockflow.domain.enumeration.DiscountAllocationMethod;
import com.adeem.stockflow.domain.enumeration.ReturnStatus;
import com.adeem.stockflow.domain.enumeration.ReturnType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for filtering ReturnOrder entities.
 */
public class ReturnOrderSpecification {

    /**
     * Filter by return order reference.
     */
    public static Specification<ReturnOrder> withReference(String reference) {
        return BaseSpecification.equals("reference", reference);
    }

    /**
     * Filter by return order status.
     */
    public static Specification<ReturnOrder> withStatus(ReturnStatus status) {
        return BaseSpecification.equals("status", status);
    }

    /**
     * Filter by return type.
     */
    public static Specification<ReturnOrder> withReturnType(ReturnType returnType) {
        return BaseSpecification.equals("returnType", returnType);
    }

    /**
     * Filter by return date between two dates.
     */
    public static Specification<ReturnOrder> withReturnDateBetween(ZonedDateTime start, ZonedDateTime end) {
        return BaseSpecification.between("returnDate", start, end);
    }

    /**
     * Filter by processed date between two dates.
     */
    public static Specification<ReturnOrder> withProcessedDateBetween(ZonedDateTime start, ZonedDateTime end) {
        return BaseSpecification.between("processedDate", start, end);
    }

    /**
     * Filter returns that have been processed.
     */
    public static Specification<ReturnOrder> isProcessed() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("processedDate"));
    }

    /**
     * Filter returns that have not been processed yet.
     */
    public static Specification<ReturnOrder> isNotProcessed() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("processedDate"));
    }

    /**
     * Filter by refund amount greater than or equal to a specific amount.
     */
    public static Specification<ReturnOrder> withRefundAmountGreaterThanOrEqual(BigDecimal amount) {
        return BaseSpecification.greaterThanOrEqual("refundAmount", amount);
    }

    /**
     * Filter by refund amount less than or equal to a specific amount.
     */
    public static Specification<ReturnOrder> withRefundAmountLessThanOrEqual(BigDecimal amount) {
        return BaseSpecification.lessThanOrEqual("refundAmount", amount);
    }

    /**
     * Filter by original order reference.
     */
    public static Specification<ReturnOrder> withOriginalOrderReference(String originalOrderReference) {
        return BaseSpecification.equals("originalOrderReference", originalOrderReference);
    }

    /**
     * Filter returns that are partial returns.
     */
    public static Specification<ReturnOrder> isPartialReturn(Boolean isPartial) {
        return isPartial ? BaseSpecification.isTrue("isPartialReturn") : BaseSpecification.isFalse("isPartialReturn");
    }

    /**
     * Filter by discount allocation method.
     */
    public static Specification<ReturnOrder> withDiscountAllocationMethod(DiscountAllocationMethod method) {
        return BaseSpecification.equals("discountAllocationMethod", method);
    }

    /**
     * Filter by notes containing specific text.
     */
    public static Specification<ReturnOrder> withNotesContaining(String text) {
        return BaseSpecification.contains("notes", text);
    }

    /**
     * Filter by customer ID.
     */
    public static Specification<ReturnOrder> withCustomerId(Long customerId) {
        return (root, query, criteriaBuilder) -> {
            if (customerId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("customer", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), customerId);
        };
    }

    /**
     * Filter by supplier ID.
     */
    public static Specification<ReturnOrder> withSupplierId(Long supplierId) {
        return (root, query, criteriaBuilder) -> {
            if (supplierId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("supplier", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), supplierId);
        };
    }

    /**
     * Filter by client account ID.
     */
    public static Specification<ReturnOrder> withClientAccountId(Long clientAccountId) {
        return (root, query, criteriaBuilder) -> {
            if (clientAccountId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("clientAccount", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), clientAccountId);
        };
    }

    /**
     * Filter by processed by admin ID.
     */
    public static Specification<ReturnOrder> withProcessedByAdminId(Long adminId) {
        return (root, query, criteriaBuilder) -> {
            if (adminId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("processedBy", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), adminId);
        };
    }

    /**
     * Filter by original sale order ID.
     */
    public static Specification<ReturnOrder> withOriginalSaleOrderId(Long saleOrderId) {
        return (root, query, criteriaBuilder) -> {
            if (saleOrderId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("originalSaleOrder", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), saleOrderId);
        };
    }

    /**
     * Filter by original purchase order ID.
     */
    public static Specification<ReturnOrder> withOriginalPurchaseOrderId(Long purchaseOrderId) {
        return (root, query, criteriaBuilder) -> {
            if (purchaseOrderId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("originalPurchaseOrder", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), purchaseOrderId);
        };
    }

    /**
     * Filter by product ID in the return items.
     */
    public static Specification<ReturnOrder> withProductId(Long productId) {
        return (root, query, criteriaBuilder) -> {
            if (productId == null) {
                return criteriaBuilder.conjunction();
            }
            var itemsJoin = root.join("items", jakarta.persistence.criteria.JoinType.LEFT);
            var productJoin = itemsJoin.join("product", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(productJoin.get("id"), productId);
        };
    }

    /**
     * Filter by created by user.
     */
    public static Specification<ReturnOrder> withCreatedBy(String createdBy) {
        return BaseSpecification.equals("createdBy", createdBy);
    }

    /**
     * Filter by created date between two dates.
     */
    public static Specification<ReturnOrder> withCreatedDateBetween(Instant start, Instant end) {
        return BaseSpecification.between("createdDate", start, end);
    }
}
