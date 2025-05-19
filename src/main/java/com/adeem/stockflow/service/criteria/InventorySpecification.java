package com.adeem.stockflow.service.criteria;

import com.adeem.stockflow.domain.Inventory;
import com.adeem.stockflow.domain.enumeration.InventoryStatus;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for filtering Inventory entities.
 */
public class InventorySpecification {

    /**
     * Filter by inventory status.
     */
    public static Specification<Inventory> withStatus(InventoryStatus status) {
        return BaseSpecification.equals("status", status);
    }

    /**
     * Filter by inventory location (partial match).
     */
    public static Specification<Inventory> withLocation(String location) {
        return BaseSpecification.contains("location", location);
    }

    /**
     * Filter by total quantity greater than or equal to the specified amount.
     */
    public static Specification<Inventory> withQuantityGreaterThanOrEqual(BigDecimal quantity) {
        return BaseSpecification.greaterThanOrEqual("quantity", quantity);
    }

    /**
     * Filter by total quantity less than or equal to the specified amount.
     */
    public static Specification<Inventory> withQuantityLessThanOrEqual(BigDecimal quantity) {
        return BaseSpecification.lessThanOrEqual("quantity", quantity);
    }

    /**
     * Filter by available quantity greater than or equal to the specified amount.
     */
    public static Specification<Inventory> withAvailableQuantityGreaterThanOrEqual(BigDecimal quantity) {
        return BaseSpecification.greaterThanOrEqual("availableQuantity", quantity);
    }

    /**
     * Filter by available quantity less than or equal to the specified amount.
     */
    public static Specification<Inventory> withAvailableQuantityLessThanOrEqual(BigDecimal quantity) {
        return BaseSpecification.lessThanOrEqual("availableQuantity", quantity);
    }

    /**
     * Filter by last updated date before the specified date.
     */
    public static Specification<Inventory> withLastUpdatedBefore(Instant date) {
        return BaseSpecification.lessThan("lastUpdated", date);
    }

    /**
     * Filter by last updated date after the specified date.
     */
    public static Specification<Inventory> withLastUpdatedAfter(Instant date) {
        return BaseSpecification.greaterThan("lastUpdated", date);
    }

    /**
     * Filter by last updated date between two dates.
     */
    public static Specification<Inventory> withLastUpdatedBetween(Instant start, Instant end) {
        return BaseSpecification.between("lastUpdated", start, end);
    }

    /**
     * Filter inventory items that are low in stock (available quantity less than or equal to minimum stock level).
     */
    public static Specification<Inventory> isLowStock() {
        return (root, query, criteriaBuilder) -> {
            var productJoin = root.join("product", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.lessThanOrEqualTo(root.get("availableQuantity"), productJoin.get("minimumStockLevel"));
        };
    }

    /**
     * Filter inventory items that are out of stock (available quantity is zero or less).
     */
    public static Specification<Inventory> isOutOfStock() {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.lessThanOrEqualTo(root.get("availableQuantity"), BigDecimal.ZERO);
        };
    }

    /**
     * Filter by product ID.
     */
    public static Specification<Inventory> withProductId(Long productId) {
        return (root, query, criteriaBuilder) -> {
            if (productId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("product", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), productId);
        };
    }

    /**
     * Filter by product code.
     */
    public static Specification<Inventory> withProductCode(String productCode) {
        return (root, query, criteriaBuilder) -> {
            if (productCode == null || productCode.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("product", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("code"), productCode);
        };
    }

    /**
     * Filter by product category.
     */
    public static Specification<Inventory> withProductCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null || category.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("product", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("category"), category);
        };
    }

    /**
     * Filter by product family ID.
     */
    public static Specification<Inventory> withProductFamilyId(Long productFamilyId) {
        return (root, query, criteriaBuilder) -> {
            if (productFamilyId == null) {
                return criteriaBuilder.conjunction();
            }
            var productJoin = root.join("product", jakarta.persistence.criteria.JoinType.LEFT);
            var familyJoin = productJoin.join("productFamily", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(familyJoin.get("id"), productFamilyId);
        };
    }

    /**
     * Filter by client account ID.
     */
    public static Specification<Inventory> withClientAccountId(Long clientAccountId) {
        return (root, query, criteriaBuilder) -> {
            if (clientAccountId == null) {
                return criteriaBuilder.conjunction();
            }
            var productJoin = root.join("product", jakarta.persistence.criteria.JoinType.LEFT);
            var clientAccountJoin = productJoin.join("clientAccount", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(clientAccountJoin.get("id"), clientAccountId);
        };
    }
}
