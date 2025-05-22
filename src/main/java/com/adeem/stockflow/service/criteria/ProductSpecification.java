package com.adeem.stockflow.service.criteria;

import com.adeem.stockflow.domain.*;
import com.adeem.stockflow.domain.enumeration.InventoryStatus;
import com.adeem.stockflow.service.criteria.filter.ProductCriteria;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.springframework.data.jpa.domain.Specification;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.RangeFilter;

/**
 * Specifications for filtering Product entities.
 */
public class ProductSpecification extends QueryService<Product> {

    /**
     * Return a {@link Specification} based on the given criteria.
     *
     * @param criteria the criteria to create the specification from.
     * @return the specification.
     */
    public static Specification<Product> createSpecification(ProductCriteria criteria) {
        ProductSpecification helper = new ProductSpecification();
        Specification<Product> specification = Specification.where(null);

        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(helper.buildRangeSpecification(criteria.getId(), Product_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getName(), Product_.name));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getCode(), Product_.code));
            }
            if (criteria.getManufacturerCode() != null) {
                specification = specification.and(
                    helper.buildStringSpecification(criteria.getManufacturerCode(), Product_.manufacturerCode)
                );
            }
            if (criteria.getUpc() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getUpc(), Product_.upc));
            }
            if (criteria.getSellingPrice() != null) {
                specification = specification.and(helper.buildRangeSpecification(criteria.getSellingPrice(), Product_.sellingPrice));
            }
            if (criteria.getCostPrice() != null) {
                specification = specification.and(helper.buildRangeSpecification(criteria.getCostPrice(), Product_.costPrice));
            }
            if (criteria.getProfitMargin() != null) {
                specification = specification.and(helper.buildRangeSpecification(criteria.getProfitMargin(), Product_.profitMargin));
            }
            if (criteria.getMinimumStockLevel() != null) {
                specification = specification.and(
                    helper.buildRangeSpecification(criteria.getMinimumStockLevel(), Product_.minimumStockLevel)
                );
            }
            if (criteria.getCategory() != null) {
                specification = specification.and(helper.buildSpecification(criteria.getCategory(), Product_.category));
            }
            if (criteria.getApplyTva() != null) {
                specification = specification.and(helper.buildSpecification(criteria.getApplyTva(), Product_.applyTva));
            }
            if (criteria.getIsVisibleToCustomers() != null) {
                specification = specification.and(
                    helper.buildSpecification(criteria.getIsVisibleToCustomers(), Product_.isVisibleToCustomers)
                );
            }
            if (criteria.getExpirationDate() != null) {
                specification = specification.and(helper.buildRangeSpecification(criteria.getExpirationDate(), Product_.expirationDate));
            }
            if (criteria.getProductFamilyId() != null) {
                specification = specification.and(
                    helper.buildSpecification(criteria.getProductFamilyId(), root -> root.get(Product_.productFamily).get(ProductFamily_.id)
                    )
                );
            }

            if (criteria.getInventoryQuantity() != null) {
                RangeFilter<BigDecimal> q = criteria.getInventoryQuantity();
                specification = specification.and((root, query, cb) -> {
                    Join<Product, Inventory> inv = root.join(Product_.inventories, JoinType.LEFT);
                    Predicate p = cb.conjunction();
                    if (q.getGreaterThan() != null) {
                        p = cb.and(p, cb.greaterThan(inv.get(Inventory_.quantity), q.getGreaterThan()));
                    }
                    if (q.getLessThan() != null) {
                        p = cb.and(p, cb.lessThan(inv.get(Inventory_.quantity), q.getLessThan()));
                    }
                    if (q.getEquals() != null) {
                        p = cb.and(p, cb.equal(inv.get(Inventory_.quantity), q.getEquals()));
                    }
                    return p;
                });
            }

            if (criteria.getLowStock() != null && criteria.getLowStock().getEquals()) {
                specification = specification.and(withLowStock());
            }
        }

        return specification;
    }

    public static Specification<Product> withId(Long id) {
        return BaseSpecification.equals("id", id);
    }

    /**
     * Filter by product name (case-insensitive, partial match).
     */
    public static Specification<Product> withName(String name) {
        return BaseSpecification.contains("name", name);
    }

    /**
     * Filter by product code (exact match).
     */
    public static Specification<Product> withCode(String code) {
        return BaseSpecification.equals("code", code);
    }

    /**
     * Filter by manufacturer code.
     */
    public static Specification<Product> withManufacturerCode(String manufacturerCode) {
        return BaseSpecification.equals("manufacturerCode", manufacturerCode);
    }

    /**
     * Filter by UPC.
     */
    public static Specification<Product> withUpc(String upc) {
        return BaseSpecification.equals("upc", upc);
    }

    /**
     * Filter by product category.
     */
    public static Specification<Product> withCategory(String category) {
        return BaseSpecification.equals("category", category);
    }

    /**
     * Filter by selling price greater than or equal to the specified value.
     */
    public static Specification<Product> withSellingPriceGreaterThanOrEqual(BigDecimal price) {
        return BaseSpecification.greaterThanOrEqual("sellingPrice", price);
    }

    /**
     * Filter by selling price less than or equal to the specified value.
     */
    public static Specification<Product> withSellingPriceLessThanOrEqual(BigDecimal price) {
        return BaseSpecification.lessThanOrEqual("sellingPrice", price);
    }

    /**
     * Filter by selling price between the specified values.
     */
    public static Specification<Product> withSellingPriceBetween(BigDecimal min, BigDecimal max) {
        return BaseSpecification.between("sellingPrice", min, max);
    }

    /**
     * Filter by cost price greater than or equal to the specified value.
     */
    public static Specification<Product> withCostPriceGreaterThanOrEqual(BigDecimal price) {
        return BaseSpecification.greaterThanOrEqual("costPrice", price);
    }

    /**
     * Filter by cost price less than or equal to the specified value.
     */
    public static Specification<Product> withCostPriceLessThanOrEqual(BigDecimal price) {
        return BaseSpecification.lessThanOrEqual("costPrice", price);
    }

    /**
     * Filter by profit margin greater than or equal to the specified value.
     */
    public static Specification<Product> withProfitMarginGreaterThanOrEqual(BigDecimal margin) {
        return BaseSpecification.greaterThanOrEqual("profitMargin", margin);
    }

    /**
     * Filter by whether the product applies TVA.
     */
    public static Specification<Product> withApplyTva(Boolean applyTva) {
        return applyTva ? BaseSpecification.isTrue("applyTva") : BaseSpecification.isFalse("applyTva");
    }

    /**
     * Filter by whether the product is visible to customers.
     */
    public static Specification<Product> withVisibleToCustomers(Boolean visible) {
        return visible ? BaseSpecification.isTrue("isVisibleToCustomers") : BaseSpecification.isFalse("isVisibleToCustomers");
    }

    /**
     * Filter by expiration date before the specified date.
     */
    public static Specification<Product> withExpirationDateBefore(ZonedDateTime date) {
        return BaseSpecification.lessThan("expirationDate", date);
    }

    /**
     * Filter by expiration date after the specified date.
     */
    public static Specification<Product> withExpirationDateAfter(ZonedDateTime date) {
        return BaseSpecification.greaterThan("expirationDate", date);
    }

    /**
     * Filter by inventory status.
     */
    public static Specification<Product> withInventoryStatus(InventoryStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("inventories", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("status"), status);
        };
    }

    /**
     * Filter by minimum stock level less than or equal to available quantity
     * (potentially low stock products).
     */
    public static Specification<Product> withLowStock() {
        return (root, query, criteriaBuilder) -> {
            var join = root.join("inventories", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.lessThanOrEqualTo(root.get("minimumStockLevel"), join.get("availableQuantity"));
        };
    }

    /**
     * Filter by product family ID.
     */
    public static Specification<Product> withProductFamilyId(Long productFamilyId) {
        return (root, query, criteriaBuilder) -> {
            if (productFamilyId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("productFamily", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), productFamilyId);
        };
    }

    /**
     * Filter by client account ID.
     */
    public static Specification<Product> withClientAccountId(Long clientAccountId) {
        return (root, query, criteriaBuilder) -> {
            if (clientAccountId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("clientAccount", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), clientAccountId);
        };
    }
}
