package com.adeem.stockflow.service.criteria;

import com.adeem.stockflow.domain.InventoryTransaction;
import com.adeem.stockflow.domain.enumeration.TransactionType;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for filtering InventoryTransaction entities.
 */
public class InventoryTransactionSpecification {

    /**
     * Filter by transaction reference number.
     */
    public static Specification<InventoryTransaction> withReferenceNumber(String referenceNumber) {
        return BaseSpecification.equals("referenceNumber", referenceNumber);
    }

    /**
     * Filter by transaction type.
     */
    public static Specification<InventoryTransaction> withTransactionType(TransactionType transactionType) {
        return BaseSpecification.equals("transactionType", transactionType);
    }

    /**
     * Filter by transaction date between the specified dates.
     */
    public static Specification<InventoryTransaction> withTransactionDateBetween(ZonedDateTime start, ZonedDateTime end) {
        return BaseSpecification.between("transactionDate", start, end);
    }

    /**
     * Filter by transaction date before the specified date.
     */
    public static Specification<InventoryTransaction> withTransactionDateBefore(ZonedDateTime date) {
        return BaseSpecification.lessThan("transactionDate", date);
    }

    /**
     * Filter by transaction date after the specified date.
     */
    public static Specification<InventoryTransaction> withTransactionDateAfter(ZonedDateTime date) {
        return BaseSpecification.greaterThan("transactionDate", date);
    }

    /**
     * Filter by quantity greater than or equal to the specified amount.
     */
    public static Specification<InventoryTransaction> withQuantityGreaterThanOrEqual(BigDecimal quantity) {
        return BaseSpecification.greaterThanOrEqual("quantity", quantity);
    }

    /**
     * Filter by quantity less than or equal to the specified amount.
     */
    public static Specification<InventoryTransaction> withQuantityLessThanOrEqual(BigDecimal quantity) {
        return BaseSpecification.lessThanOrEqual("quantity", quantity);
    }

    /**
     * Filter by notes containing the specified text.
     */
    public static Specification<InventoryTransaction> withNotesContaining(String notes) {
        return BaseSpecification.contains("notes", notes);
    }

    /**
     * Filter by return order item ID.
     */
    public static Specification<InventoryTransaction> withReturnOrderItemId(Long returnOrderItemId) {
        return (root, query, criteriaBuilder) -> {
            if (returnOrderItemId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("returnOrderItem", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), returnOrderItemId);
        };
    }

    /**
     * Filter by sale order ID.
     */
    public static Specification<InventoryTransaction> withSaleOrderId(Long saleOrderId) {
        return (root, query, criteriaBuilder) -> {
            if (saleOrderId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("saleOrderItem", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), saleOrderId);
        };
    }

    /**
     * Filter by purchase order ID.
     */
    public static Specification<InventoryTransaction> withPurchaseOrderId(Long purchaseOrderId) {
        return (root, query, criteriaBuilder) -> {
            if (purchaseOrderId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("purchaseOrderItem", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), purchaseOrderId);
        };
    }

    /**
     * Filter by product ID across all related entities.
     */
    public static Specification<InventoryTransaction> withProductId(Long productId) {
        return (root, query, criteriaBuilder) -> {
            if (productId == null) {
                return criteriaBuilder.conjunction();
            }

            // Create disjunction to check for product in all possible paths
            Predicate result = criteriaBuilder.disjunction();

            // Check return order item's product
            try {
                var returnItemJoin = root.join("returnOrderItem", jakarta.persistence.criteria.JoinType.LEFT);
                var productJoin1 = returnItemJoin.join("product", jakarta.persistence.criteria.JoinType.LEFT);
                result = criteriaBuilder.or(result, criteriaBuilder.equal(productJoin1.get("id"), productId));
            } catch (IllegalArgumentException e) {
                // Path might not exist, continue with other checks
            }

            // Check sale order's products
            try {
                var saleOrderJoin = root.join("saleOrderItem", jakarta.persistence.criteria.JoinType.LEFT);
                var orderItemsJoin1 = saleOrderJoin.join("orderItems", jakarta.persistence.criteria.JoinType.LEFT);
                var productJoin2 = orderItemsJoin1.join("product", jakarta.persistence.criteria.JoinType.LEFT);
                result = criteriaBuilder.or(result, criteriaBuilder.equal(productJoin2.get("id"), productId));
            } catch (IllegalArgumentException e) {
                // Path might not exist, continue with other checks
            }

            // Check purchase order's products
            try {
                var purchaseOrderJoin = root.join("purchaseOrderItem", jakarta.persistence.criteria.JoinType.LEFT);
                var orderItemsJoin2 = purchaseOrderJoin.join("orderItems", jakarta.persistence.criteria.JoinType.LEFT);
                var productJoin3 = orderItemsJoin2.join("product", jakarta.persistence.criteria.JoinType.LEFT);
                result = criteriaBuilder.or(result, criteriaBuilder.equal(productJoin3.get("id"), productId));
            } catch (IllegalArgumentException e) {
                // Path might not exist
            }

            return result;
        };
    }

    /**
     * Filter transactions by created by user.
     */
    public static Specification<InventoryTransaction> withCreatedBy(String createdBy) {
        return BaseSpecification.equals("createdBy", createdBy);
    }

    /**
     * Filter transactions by last modified by user.
     */
    public static Specification<InventoryTransaction> withLastModifiedBy(String lastModifiedBy) {
        return BaseSpecification.equals("lastModifiedBy", lastModifiedBy);
    }

    /**
     * Filter transactions created between the specified dates.
     */
    public static Specification<InventoryTransaction> withCreatedDateBetween(Instant start, Instant end) {
        return BaseSpecification.between("createdDate", start, end);
    }
}
