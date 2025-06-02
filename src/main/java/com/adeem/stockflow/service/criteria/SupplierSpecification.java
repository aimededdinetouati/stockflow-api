package com.adeem.stockflow.service.criteria;

import com.adeem.stockflow.domain.Supplier;
import com.adeem.stockflow.domain.Supplier_;
import com.adeem.stockflow.domain.enumeration.AddressType;
import com.adeem.stockflow.service.criteria.filter.SupplierCriteria;
import jakarta.persistence.criteria.JoinType;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;
import tech.jhipster.service.QueryService;

/**
 * Specifications for filtering Supplier entities.
 */
public class SupplierSpecification extends QueryService<Supplier> {

    public static Specification<Supplier> createSpecification(SupplierCriteria criteria) {
        Specification<Supplier> specification = Specification.where(null);
        SupplierSpecification helper = new SupplierSpecification();

        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(helper.buildRangeSpecification(criteria.getId(), Supplier_.id));
            }
            if (criteria.getFirstName() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getFirstName(), Supplier_.firstName));
            }
            if (criteria.getLastName() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getLastName(), Supplier_.lastName));
            }
            if (criteria.getCompanyName() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getCompanyName(), Supplier_.companyName));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getPhone(), Supplier_.phone));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getEmail(), Supplier_.email));
            }
            if (criteria.getFax() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getFax(), Supplier_.fax));
            }
            if (criteria.getTaxId() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getTaxId(), Supplier_.taxId));
            }
            if (criteria.getRegistrationArticle() != null) {
                specification = specification.and(
                    helper.buildStringSpecification(criteria.getRegistrationArticle(), Supplier_.registrationArticle)
                );
            }
            if (criteria.getStatisticalId() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getStatisticalId(), Supplier_.statisticalId));
            }
            if (criteria.getRc() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getRc(), Supplier_.rc));
            }
            if (criteria.getActive() != null) {
                specification = specification.and(helper.buildSpecification(criteria.getActive(), Supplier_.active));
            }
            if (criteria.getNotes() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getNotes(), Supplier_.notes));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getCreatedBy(), Supplier_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(helper.buildRangeSpecification(criteria.getCreatedDate(), Supplier_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getLastModifiedBy(), Supplier_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(
                    helper.buildRangeSpecification(criteria.getLastModifiedDate(), Supplier_.lastModifiedDate)
                );
            }
            if (criteria.getClientAccountId() != null) {
                specification = specification.and(
                    helper.buildSpecification(criteria.getClientAccountId(), root ->
                        root.join(Supplier_.clientAccount, JoinType.LEFT).get("id")
                    )
                );
            }
            if (criteria.getAddressId() != null) {
                specification = specification.and(
                    helper.buildSpecification(criteria.getAddressId(), root -> root.join(Supplier_.address, JoinType.LEFT).get("id"))
                );
            }
        }

        return specification;
    }

    /**
     * Filter by supplier first name (case-insensitive, partial match).
     */
    public static Specification<Supplier> withFirstName(String firstName) {
        return BaseSpecification.contains("firstName", firstName);
    }

    /**
     * Filter by supplier last name (case-insensitive, partial match).
     */
    public static Specification<Supplier> withLastName(String lastName) {
        return BaseSpecification.contains("lastName", lastName);
    }

    /**
     * Filter by supplier phone number (partial match).
     */
    public static Specification<Supplier> withPhone(String phone) {
        return BaseSpecification.contains("phone", phone);
    }

    /**
     * Filter by supplier fax number.
     */
    public static Specification<Supplier> withFax(String fax) {
        return BaseSpecification.contains("fax", fax);
    }

    /**
     * Filter by tax ID.
     */
    public static Specification<Supplier> withTaxId(String taxId) {
        return BaseSpecification.equals("taxId", taxId);
    }

    /**
     * Filter by registration article.
     */
    public static Specification<Supplier> withRegistrationArticle(String registrationArticle) {
        return BaseSpecification.equals("registrationArticle", registrationArticle);
    }

    /**
     * Filter by statistical ID.
     */
    public static Specification<Supplier> withStatisticalId(String statisticalId) {
        return BaseSpecification.equals("statisticalId", statisticalId);
    }

    /**
     * Filter by RC.
     */
    public static Specification<Supplier> withRc(String rc) {
        return BaseSpecification.equals("rc", rc);
    }

    /**
     * Filter by client account ID.
     */
    public static Specification<Supplier> withClientAccountId(Long clientAccountId) {
        return (root, query, criteriaBuilder) -> {
            if (clientAccountId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("clientAccount", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), clientAccountId);
        };
    }

    /**
     * Filter by city (from address).
     */
    public static Specification<Supplier> withCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (city == null || city.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("address", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(join.get("city")), "%" + city.toLowerCase() + "%");
        };
    }

    /**
     * Filter by state/province (from address).
     */
    public static Specification<Supplier> withState(String state) {
        return (root, query, criteriaBuilder) -> {
            if (state == null || state.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("address", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(join.get("state")), "%" + state.toLowerCase() + "%");
        };
    }

    /**
     * Filter by country (from address).
     */
    public static Specification<Supplier> withCountry(String country) {
        return (root, query, criteriaBuilder) -> {
            if (country == null || country.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("address", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(join.get("country")), "%" + country.toLowerCase() + "%");
        };
    }

    /**
     * Filter by postal code (from address).
     */
    public static Specification<Supplier> withPostalCode(String postalCode) {
        return (root, query, criteriaBuilder) -> {
            if (postalCode == null || postalCode.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("address", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(join.get("postalCode")), "%" + postalCode.toLowerCase() + "%");
        };
    }

    /**
     * Filter by address type.
     */
    public static Specification<Supplier> withAddressType(AddressType addressType) {
        return (root, query, criteriaBuilder) -> {
            if (addressType == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("address", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("addressType"), addressType);
        };
    }

    /**
     * Filter by created by user.
     */
    public static Specification<Supplier> withCreatedBy(String createdBy) {
        return BaseSpecification.equals("createdBy", createdBy);
    }

    /**
     * Filter by created date between two dates.
     */
    public static Specification<Supplier> withCreatedDateBetween(Instant start, Instant end) {
        return BaseSpecification.between("createdDate", start, end);
    }

    /**
     * Filter by last modified by user.
     */
    public static Specification<Supplier> withLastModifiedBy(String lastModifiedBy) {
        return BaseSpecification.equals("lastModifiedBy", lastModifiedBy);
    }

    /**
     * Filter by last modified date between two dates.
     */
    public static Specification<Supplier> withLastModifiedDateBetween(Instant start, Instant end) {
        return BaseSpecification.between("lastModifiedDate", start, end);
    }

    public static Specification<Supplier> withActive(Boolean active) {
        return BaseSpecification.equals("active", active);
    }
}
