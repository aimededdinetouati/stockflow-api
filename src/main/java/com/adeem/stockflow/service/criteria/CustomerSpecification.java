package com.adeem.stockflow.service.criteria;

import com.adeem.stockflow.domain.Customer;
import com.adeem.stockflow.domain.enumeration.AddressType;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for filtering Customer entities.
 */
public class CustomerSpecification {

    /**
     * Filter by customer first name (case-insensitive, partial match).
     */
    public static Specification<Customer> withFirstName(String firstName) {
        return BaseSpecification.contains("firstName", firstName);
    }

    /**
     * Filter by customer last name (case-insensitive, partial match).
     */
    public static Specification<Customer> withLastName(String lastName) {
        return BaseSpecification.contains("lastName", lastName);
    }

    /**
     * Filter by customer phone number (partial match).
     */
    public static Specification<Customer> withPhone(String phone) {
        return BaseSpecification.contains("phone", phone);
    }

    /**
     * Filter by customer fax number.
     */
    public static Specification<Customer> withFax(String fax) {
        return BaseSpecification.contains("fax", fax);
    }

    /**
     * Filter by tax ID.
     */
    public static Specification<Customer> withTaxId(String taxId) {
        return BaseSpecification.equals("taxId", taxId);
    }

    /**
     * Filter by registration article.
     */
    public static Specification<Customer> withRegistrationArticle(String registrationArticle) {
        return BaseSpecification.equals("registrationArticle", registrationArticle);
    }

    /**
     * Filter by statistical ID.
     */
    public static Specification<Customer> withStatisticalId(String statisticalId) {
        return BaseSpecification.equals("statisticalId", statisticalId);
    }

    /**
     * Filter by RC.
     */
    public static Specification<Customer> withRc(String rc) {
        return BaseSpecification.equals("rc", rc);
    }

    /**
     * Filter by user ID.
     */
    public static Specification<Customer> withUserId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("user", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), userId);
        };
    }

    /**
     * Filter by client account ID.
     */
    public static Specification<Customer> withClientAccountId(Long clientAccountId) {
        return (root, query, criteriaBuilder) -> {
            if (clientAccountId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("clientAccount", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), clientAccountId);
        };
    }

    /**
     * Filter by city in any of the customer's addresses.
     */
    public static Specification<Customer> withCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (city == null || city.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("addressLists", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(join.get("city")), "%" + city.toLowerCase() + "%");
        };
    }

    /**
     * Filter by state/province in any of the customer's addresses.
     */
    public static Specification<Customer> withState(String state) {
        return (root, query, criteriaBuilder) -> {
            if (state == null || state.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("addressLists", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(join.get("state")), "%" + state.toLowerCase() + "%");
        };
    }

    /**
     * Filter by country in any of the customer's addresses.
     */
    public static Specification<Customer> withCountry(String country) {
        return (root, query, criteriaBuilder) -> {
            if (country == null || country.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("addressLists", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(join.get("country")), "%" + country.toLowerCase() + "%");
        };
    }

    /**
     * Filter by postal code in any of the customer's addresses.
     */
    public static Specification<Customer> withPostalCode(String postalCode) {
        return (root, query, criteriaBuilder) -> {
            if (postalCode == null || postalCode.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("addressLists", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(join.get("postalCode")), "%" + postalCode.toLowerCase() + "%");
        };
    }

    /**
     * Filter by address type.
     */
    public static Specification<Customer> withAddressType(AddressType addressType) {
        return (root, query, criteriaBuilder) -> {
            if (addressType == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("addressLists", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("addressType"), addressType);
        };
    }

    /**
     * Filter by customers with active carts.
     */
    public static Specification<Customer> withActiveCarts() {
        return (root, query, criteriaBuilder) -> {
            var join = root.join("carts", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("status"), com.adeem.stockflow.domain.enumeration.CartStatus.ACTIVE);
        };
    }
}
