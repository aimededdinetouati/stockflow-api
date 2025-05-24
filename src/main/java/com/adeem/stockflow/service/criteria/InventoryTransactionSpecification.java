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
     * Filter by product ID across all related entities.
     */
    public static Specification<InventoryTransaction> withProductId(Long productId) {
        return (root, query, criteriaBuilder) -> {
            if (productId == null) {
                return criteriaBuilder.conjunction();
            }

            var join = root.join("product", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), productId);
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
