package com.adeem.stockflow.service.criteria;

import com.adeem.stockflow.domain.*;
import com.adeem.stockflow.domain.enumeration.InventoryStatus;
import com.adeem.stockflow.service.criteria.filter.InventoryCriteria;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.BigDecimalFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Specifications for filtering Inventory entities.
 */
public class InventorySpecification extends QueryService<Inventory> {

    /**
     * Return a {@link Specification} based on the given criteria.
     *
     * @param criteria the criteria to create the specification from.
     * @return the specification.
     */
    public static Specification<Inventory> createSpecification(InventoryCriteria criteria) {
        InventorySpecification helper = new InventorySpecification();
        Specification<Inventory> specification = Specification.where(null);

        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(helper.buildRangeSpecification(criteria.getId(), Inventory_.id));
            }
            if (criteria.getQuantity() != null) {
                specification = specification.and(helper.buildRangeSpecification(criteria.getQuantity(), Inventory_.quantity));
            }
            if (criteria.getAvailableQuantity() != null) {
                specification = specification.and(
                    helper.buildRangeSpecification(criteria.getAvailableQuantity(), Inventory_.availableQuantity)
                );
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(helper.buildSpecification(criteria.getStatus(), Inventory_.status));
            }
            if (criteria.getLastUpdated() != null) {
                specification = specification.and(helper.buildRangeSpecification(criteria.getLastUpdated(), Inventory_.lastModifiedDate));
            }
            if (criteria.getProductId() != null) {
                specification = specification.and(
                    helper.buildSpecification(criteria.getProductId(), root -> root.get(Inventory_.product).get(Product_.id))
                );
            }

            if (criteria.getProductName() != null) {
                StringFilter q = criteria.getProductName();
                specification = specification.and((root, query, cb) -> {
                    Join<Inventory, Product> productJoin = root.join(Inventory_.product, JoinType.LEFT);
                    Predicate p = cb.conjunction();
                    if (q.getEquals() != null) {
                        p = cb.and(p, cb.equal(productJoin.get(Product_.name), q.getEquals()));
                    } else if (q.getContains() != null) {
                        p = cb.and(p, cb.like(cb.lower(productJoin.get(Product_.name)), "%" + q.getContains().toLowerCase() + "%"));
                    }
                    return p;
                });
            }
            if (criteria.getProductCode() != null) {
                StringFilter q = criteria.getProductCode();
                specification = specification.and((root, query, cb) -> {
                    Join<Inventory, Product> productJoin = root.join(Inventory_.product, JoinType.LEFT);
                    Predicate p = cb.conjunction();
                    if (q.getEquals() != null) {
                        p = cb.and(p, cb.equal(productJoin.get(Product_.code), q.getEquals()));
                    } else if (q.getContains() != null) {
                        p = cb.and(p, cb.like(cb.lower(productJoin.get(Product_.code)), "%" + q.getContains().toLowerCase() + "%"));
                    }
                    return p;
                });
            }
            if (criteria.getProductCategory() != null) {
                specification = specification.and(
                    helper.buildSpecification(criteria.getProductCategory(), root ->
                        root.get(Inventory_.product).get(Product_.category.toString())
                    )
                );
            }
            if (criteria.getMinimumStockLevel() != null) {
                BigDecimalFilter q = criteria.getMinimumStockLevel();
                specification = specification.and((root, query, cb) -> {
                    Join<Inventory, Product> productJoin = root.join(Inventory_.product, JoinType.LEFT);
                    Predicate p = cb.conjunction();
                    if (q.getGreaterThan() != null) {
                        p = cb.and(p, cb.greaterThan(productJoin.get(Product_.minimumStockLevel), q.getGreaterThan()));
                    }
                    if (q.getGreaterThanOrEqual() != null) {
                        p = cb.and(p, cb.greaterThanOrEqualTo(productJoin.get(Product_.minimumStockLevel), q.getGreaterThanOrEqual()));
                    }
                    if (q.getLessThan() != null) {
                        p = cb.and(p, cb.lessThan(productJoin.get(Product_.minimumStockLevel), q.getLessThan()));
                    }
                    if (q.getLessThanOrEqual() != null) {
                        p = cb.and(p, cb.lessThanOrEqualTo(productJoin.get(Product_.minimumStockLevel), q.getLessThanOrEqual()));
                    }
                    if (q.getEquals() != null) {
                        p = cb.and(p, cb.equal(productJoin.get(Product_.minimumStockLevel), q.getEquals()));
                    }
                    return p;
                });
            }
            if (criteria.getLowStock() != null && criteria.getLowStock().getEquals() != null && criteria.getLowStock().getEquals()) {
                specification = specification.and(withLowStock());
            }
            if (criteria.getOutOfStock() != null && criteria.getOutOfStock().getEquals() != null && criteria.getOutOfStock().getEquals()) {
                specification = specification.and(withOutOfStock());
            }
            if (criteria.getStockLevel() != null) {
                specification = specification.and(helper.buildRangeSpecification(criteria.getStockLevel(), Inventory_.availableQuantity));
            }
        }

        return specification;
    }

    /**
     * Filter by inventory ID.
     */
    public static Specification<Inventory> withId(Long id) {
        return BaseSpecification.equals("id", id);
    }

    /**
     * Filter by product ID.
     */
    public static Specification<Inventory> withProductId(Long productId) {
        return (root, query, criteriaBuilder) -> {
            if (productId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Inventory, Product> productJoin = root.join(Inventory_.product, JoinType.LEFT);
            return criteriaBuilder.equal(productJoin.get(Product_.id), productId);
        };
    }

    /**
     * Filter by inventory status.
     */
    public static Specification<Inventory> withStatus(InventoryStatus status) {
        return BaseSpecification.equals("status", status);
    }

    /**
     * Filter by quantity greater than or equal to the specified value.
     */
    public static Specification<Inventory> withQuantityGreaterThanOrEqual(BigDecimal quantity) {
        return BaseSpecification.greaterThanOrEqual("quantity", quantity);
    }

    /**
     * Filter by quantity less than or equal to the specified value.
     */
    public static Specification<Inventory> withQuantityLessThanOrEqual(BigDecimal quantity) {
        return BaseSpecification.lessThanOrEqual("quantity", quantity);
    }

    /**
     * Filter by available quantity greater than or equal to the specified value.
     */
    public static Specification<Inventory> withAvailableQuantityGreaterThanOrEqual(BigDecimal quantity) {
        return BaseSpecification.greaterThanOrEqual("availableQuantity", quantity);
    }

    /**
     * Filter by available quantity less than or equal to the specified value.
     */
    public static Specification<Inventory> withAvailableQuantityLessThanOrEqual(BigDecimal quantity) {
        return BaseSpecification.lessThanOrEqual("availableQuantity", quantity);
    }

    /**
     * Filter by inventory items with low stock (available quantity <= minimum stock level).
     */
    public static Specification<Inventory> withLowStock() {
        return (root, query, criteriaBuilder) -> {
            Join<Inventory, Product> productJoin = root.join(Inventory_.product, JoinType.LEFT);
            return criteriaBuilder.and(
                criteriaBuilder.lessThanOrEqualTo(root.get(Inventory_.availableQuantity), productJoin.get(Product_.minimumStockLevel)),
                criteriaBuilder.greaterThan(root.get(Inventory_.availableQuantity), BigDecimal.ZERO)
            );
        };
    }

    /**
     * Filter by inventory items that are out of stock (available quantity = 0).
     */
    public static Specification<Inventory> withOutOfStock() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Inventory_.availableQuantity), BigDecimal.ZERO);
    }

    /**
     * Filter by inventory items that need attention (low stock or out of stock).
     */
    public static Specification<Inventory> withStockAlert() {
        return withLowStock().or(withOutOfStock());
    }

    /**
     * Filter by product name (case-insensitive, partial match).
     */
    public static Specification<Inventory> withProductName(String productName) {
        return (root, query, criteriaBuilder) -> {
            if (productName == null || productName.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<Inventory, Product> productJoin = root.join(Inventory_.product, JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(productJoin.get(Product_.name)), "%" + productName.toLowerCase() + "%");
        };
    }

    /**
     * Filter by product code.
     */
    public static Specification<Inventory> withProductCode(String productCode) {
        return (root, query, criteriaBuilder) -> {
            if (productCode == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Inventory, Product> productJoin = root.join(Inventory_.product, JoinType.LEFT);
            return criteriaBuilder.equal(productJoin.get(Product_.code), productCode);
        };
    }

    /**
     * Filter by product category.
     */
    public static Specification<Inventory> withProductCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Inventory, Product> productJoin = root.join(Inventory_.product, JoinType.LEFT);
            return criteriaBuilder.equal(productJoin.get(Product_.category), category);
        };
    }

    /**
     * Filter by client account ID through the product relationship.
     */
    public static Specification<Inventory> withClientAccountId(Long clientAccountId) {
        return (root, query, criteriaBuilder) -> {
            if (clientAccountId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Inventory, Product> productJoin = root.join(Inventory_.product, JoinType.LEFT);
            Join<Product, ClientAccount> clientAccountJoin = productJoin.join(Product_.clientAccount, JoinType.LEFT);
            return criteriaBuilder.equal(clientAccountJoin.get(ClientAccount_.id), clientAccountId);
        };
    }

    /**
     * Filter by last updated date after the specified date.
     */
    public static Specification<Inventory> withLastUpdatedAfter(Instant date) {
        return BaseSpecification.greaterThan("lastModifiedDate", date);
    }

    /**
     * Filter by last updated date before the specified date.
     */
    public static Specification<Inventory> withLastUpdatedBefore(Instant date) {
        return BaseSpecification.lessThan("lastModifiedDate", date);
    }

    /**
     * Filter by last updated date between two dates.
     */
    public static Specification<Inventory> withLastUpdatedBetween(Instant startDate, Instant endDate) {
        return BaseSpecification.between("lastModifiedDate", startDate, endDate);
    }

    /**
     * Filter inventory by multiple statuses.
     */
    public static Specification<Inventory> withStatuses(java.util.List<InventoryStatus> statuses) {
        return BaseSpecification.in("status", statuses);
    }

    /**
     * Filter by inventory items that are available (status = AVAILABLE and available quantity > 0).
     */
    public static Specification<Inventory> withAvailableItems() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.and(
                criteriaBuilder.equal(root.get(Inventory_.status), InventoryStatus.AVAILABLE),
                criteriaBuilder.greaterThan(root.get(Inventory_.availableQuantity), BigDecimal.ZERO)
            );
    }

    /**
     * Filter by inventory items with reserved quantity.
     */
    public static Specification<Inventory> withReservedQuantity() {
        return (root, query, criteriaBuilder) -> {
            // Reserved quantity = total quantity - available quantity
            return criteriaBuilder.greaterThan(
                criteriaBuilder.diff(root.get(Inventory_.quantity), root.get(Inventory_.availableQuantity)),
                BigDecimal.ZERO
            );
        };
    }
}
