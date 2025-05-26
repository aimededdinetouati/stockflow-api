package com.adeem.stockflow.service.criteria;

import com.adeem.stockflow.domain.*;
import com.adeem.stockflow.service.criteria.filter.ProductFamilyCriteria;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;
import tech.jhipster.service.QueryService;

/**
 * Specifications for filtering ProductFamily entities.
 */
public class ProductFamilySpecification extends QueryService<ProductFamily> {

    /**
     * Return a {@link Specification} based on the given criteria.
     *
     * @param criteria the criteria to create the specification from.
     * @return the specification.
     */
    public static Specification<ProductFamily> createSpecification(ProductFamilyCriteria criteria) {
        ProductFamilySpecification helper = new ProductFamilySpecification();
        Specification<ProductFamily> specification = Specification.where(null);

        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(helper.buildRangeSpecification(criteria.getId(), ProductFamily_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getName(), ProductFamily_.name));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(helper.buildRangeSpecification(criteria.getCreatedDate(), ProductFamily_.createdDate));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(
                    helper.buildRangeSpecification(criteria.getLastModifiedDate(), ProductFamily_.lastModifiedDate)
                );
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getCreatedBy(), ProductFamily_.createdBy));
            }
            if (criteria.getHasProducts() != null && criteria.getHasProducts().getEquals() != null) {
                if (criteria.getHasProducts().getEquals()) {
                    specification = specification.and(withProducts());
                } else {
                    specification = specification.and(withoutProducts());
                }
            }
            if (criteria.getProductCount() != null) {
                specification = specification.and(withProductCount(criteria.getProductCount()));
            }
        }

        return specification;
    }

    /**
     * Filter by product family ID.
     */
    public static Specification<ProductFamily> withId(Long id) {
        return BaseSpecification.equals("id", id);
    }

    /**
     * Filter by family name (case-insensitive, partial match).
     */
    public static Specification<ProductFamily> withName(String name) {
        return BaseSpecification.contains("name", name);
    }

    /**
     * Filter by exact family name (case-sensitive).
     */
    public static Specification<ProductFamily> withExactName(String name) {
        return BaseSpecification.equals("name", name);
    }

    /**
     * Filter by client account ID.
     */
    public static Specification<ProductFamily> withClientAccountId(Long clientAccountId) {
        return (root, query, criteriaBuilder) -> {
            if (clientAccountId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<ProductFamily, ClientAccount> clientAccountJoin = root.join(ProductFamily_.clientAccount, JoinType.LEFT);
            return criteriaBuilder.equal(clientAccountJoin.get(ClientAccount_.id), clientAccountId);
        };
    }

    /**
     * Filter by families that have products assigned.
     */
    public static Specification<ProductFamily> withProducts() {
        return (root, query, criteriaBuilder) -> {
            // Use subquery to check if family has products
            var subquery = query.subquery(Long.class);
            var productRoot = subquery.from(Product.class);
            subquery.select(criteriaBuilder.count(productRoot.get(Product_.id)));
            subquery.where(criteriaBuilder.equal(productRoot.get(Product_.productFamily), root));

            return criteriaBuilder.greaterThan(subquery, 0L);
        };
    }

    /**
     * Filter by families that have no products assigned.
     */
    public static Specification<ProductFamily> withoutProducts() {
        return (root, query, criteriaBuilder) -> {
            // Use subquery to check if family has no products
            var subquery = query.subquery(Long.class);
            var productRoot = subquery.from(Product.class);
            subquery.select(criteriaBuilder.count(productRoot.get(Product_.id)));
            subquery.where(criteriaBuilder.equal(productRoot.get(Product_.productFamily), root));

            return criteriaBuilder.equal(subquery, 0L);
        };
    }

    /**
     * Filter by product count criteria.
     */
    public static Specification<ProductFamily> withProductCount(tech.jhipster.service.filter.LongFilter productCountFilter) {
        return (root, query, criteriaBuilder) -> {
            if (productCountFilter == null) {
                return criteriaBuilder.conjunction();
            }

            // Use subquery to count products
            var subquery = query.subquery(Long.class);
            var productRoot = subquery.from(Product.class);
            subquery.select(criteriaBuilder.count(productRoot.get(Product_.id)));
            subquery.where(criteriaBuilder.equal(productRoot.get(Product_.productFamily), root));

            Predicate predicate = criteriaBuilder.conjunction();

            if (productCountFilter.getEquals() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(subquery, productCountFilter.getEquals()));
            }
            if (productCountFilter.getGreaterThan() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThan(subquery, productCountFilter.getGreaterThan()));
            }
            if (productCountFilter.getGreaterThanOrEqual() != null) {
                predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.greaterThanOrEqualTo(subquery, productCountFilter.getGreaterThanOrEqual())
                );
            }
            if (productCountFilter.getLessThan() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThan(subquery, productCountFilter.getLessThan()));
            }
            if (productCountFilter.getLessThanOrEqual() != null) {
                predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.lessThanOrEqualTo(subquery, productCountFilter.getLessThanOrEqual())
                );
            }

            return predicate;
        };
    }

    /**
     * Filter by creation date after the specified date.
     */
    public static Specification<ProductFamily> withCreatedDateAfter(Instant date) {
        return BaseSpecification.greaterThan("createdDate", date);
    }

    /**
     * Filter by creation date before the specified date.
     */
    public static Specification<ProductFamily> withCreatedDateBefore(Instant date) {
        return BaseSpecification.lessThan("createdDate", date);
    }

    /**
     * Filter by creation date between two dates.
     */
    public static Specification<ProductFamily> withCreatedDateBetween(Instant startDate, Instant endDate) {
        return BaseSpecification.between("createdDate", startDate, endDate);
    }

    /**
     * Filter by last modified date after the specified date.
     */
    public static Specification<ProductFamily> withLastModifiedDateAfter(Instant date) {
        return BaseSpecification.greaterThan("lastModifiedDate", date);
    }

    /**
     * Filter by last modified date before the specified date.
     */
    public static Specification<ProductFamily> withLastModifiedDateBefore(Instant date) {
        return BaseSpecification.lessThan("lastModifiedDate", date);
    }

    /**
     * Filter by creator (createdBy field).
     */
    public static Specification<ProductFamily> withCreatedBy(String createdBy) {
        return BaseSpecification.equals("createdBy", createdBy);
    }

    /**
     * Filter by families with products in specific categories.
     */
    public static Specification<ProductFamily> withProductsInCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null) {
                return criteriaBuilder.conjunction();
            }

            // Use subquery to check if family has products in specified category
            var subquery = query.subquery(Long.class);
            var productRoot = subquery.from(Product.class);
            subquery.select(criteriaBuilder.count(productRoot.get(Product_.id)));
            subquery.where(
                criteriaBuilder.and(
                    criteriaBuilder.equal(productRoot.get(Product_.productFamily), root),
                    criteriaBuilder.equal(productRoot.get(Product_.category), category)
                )
            );

            return criteriaBuilder.greaterThan(subquery, 0L);
        };
    }

    /**
     * Filter by families with low stock products.
     */
    public static Specification<ProductFamily> withLowStockProducts() {
        return (root, query, criteriaBuilder) -> {
            // Use subquery to check if family has products with low stock
            var subquery = query.subquery(Long.class);
            var productRoot = subquery.from(Product.class);
            var inventoryJoin = productRoot.join(Product_.inventories, JoinType.LEFT);

            subquery.select(criteriaBuilder.count(productRoot.get(Product_.id)));
            subquery.where(
                criteriaBuilder.and(
                    criteriaBuilder.equal(productRoot.get(Product_.productFamily), root),
                    criteriaBuilder.lessThanOrEqualTo(
                        inventoryJoin.get(Inventory_.availableQuantity),
                        productRoot.get(Product_.minimumStockLevel)
                    )
                )
            );

            return criteriaBuilder.greaterThan(subquery, 0L);
        };
    }

    /**
     * Complex specification that combines client account filtering with search term.
     * This is commonly used in REST endpoints.
     */
    public static Specification<ProductFamily> withClientAccountAndSearch(Long clientAccountId, String searchTerm) {
        Specification<ProductFamily> spec = withClientAccountId(clientAccountId);

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            spec = spec.and(withName(searchTerm.trim()));
        }

        return spec;
    }
}
