package com.adeem.stockflow.service.criteria;

import com.adeem.stockflow.domain.Supplier;
import com.adeem.stockflow.domain.enumeration.AddressType;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for filtering Supplier entities.
 */
public class SupplierSpecification {

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
}
