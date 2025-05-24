package com.adeem.stockflow.service.criteria;

import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.enumeration.AccountStatus;
import com.adeem.stockflow.domain.enumeration.AddressType;
import com.adeem.stockflow.domain.enumeration.SubscriptionStatus;
import java.time.Instant;
import java.time.ZonedDateTime;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for filtering ClientAccount entities.
 */
public class ClientAccountSpecification {

    /**
     * Filter by company name (case-insensitive, partial match).
     */
    public static Specification<ClientAccount> withCompanyName(String companyName) {
        return BaseSpecification.contains("companyName", companyName);
    }

    /**
     * Filter by contact person name (case-insensitive, partial match).
     */
    public static Specification<ClientAccount> withContactPerson(String contactPerson) {
        return BaseSpecification.contains("contactPerson", contactPerson);
    }

    /**
     * Filter by phone number (partial match).
     */
    public static Specification<ClientAccount> withPhone(String phone) {
        return BaseSpecification.contains("phone", phone);
    }

    /**
     * Filter by email (case-insensitive, partial match).
     */
    public static Specification<ClientAccount> withEmail(String email) {
        return BaseSpecification.contains("email", email);
    }

    /**
     * Filter by account status.
     */
    public static Specification<ClientAccount> withStatus(AccountStatus status) {
        return BaseSpecification.equals("status", status);
    }

    /**
     * Filter by city (from address).
     */
    public static Specification<ClientAccount> withCity(String city) {
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
    public static Specification<ClientAccount> withState(String state) {
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
    public static Specification<ClientAccount> withCountry(String country) {
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
    public static Specification<ClientAccount> withPostalCode(String postalCode) {
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
    public static Specification<ClientAccount> withAddressType(AddressType addressType) {
        return (root, query, criteriaBuilder) -> {
            if (addressType == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("address", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("addressType"), addressType);
        };
    }

    /**
     * Filter client accounts with active subscriptions.
     */
    public static Specification<ClientAccount> withActiveSubscription() {
        return (root, query, criteriaBuilder) -> {
            var subscriptionsJoin = root.join("subscriptions", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(subscriptionsJoin.get("status"), SubscriptionStatus.ACTIVE);
        };
    }

    /**
     * Filter client accounts with subscriptions of a particular status.
     */
    public static Specification<ClientAccount> withSubscriptionStatus(SubscriptionStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            var subscriptionsJoin = root.join("subscriptions", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(subscriptionsJoin.get("status"), status);
        };
    }

    /**
     * Filter client accounts with subscriptions ending before a specific date.
     */
    public static Specification<ClientAccount> withSubscriptionEndingBefore(ZonedDateTime date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.conjunction();
            }
            var subscriptionsJoin = root.join("subscriptions", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.lessThan(subscriptionsJoin.get("endDate"), date);
        };
    }

    /**
     * Filter client accounts by quota users count greater than or equal to a specific number.
     */
    public static Specification<ClientAccount> withQuotaUsersGreaterThanOrEqual(Integer count) {
        return (root, query, criteriaBuilder) -> {
            if (count == null) {
                return criteriaBuilder.conjunction();
            }
            var quotaJoin = root.join("quota", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.greaterThanOrEqualTo(quotaJoin.get("users"), count);
        };
    }

    /**
     * Filter by created by user.
     */
    public static Specification<ClientAccount> withCreatedBy(String createdBy) {
        return BaseSpecification.equals("createdBy", createdBy);
    }

    /**
     * Filter by created date between two dates.
     */
    public static Specification<ClientAccount> withCreatedDateBetween(Instant start, Instant end) {
        return BaseSpecification.between("createdDate", start, end);
    }

    /**
     * Filter by last modified by user.
     */
    public static Specification<ClientAccount> withLastModifiedBy(String lastModifiedBy) {
        return BaseSpecification.equals("lastModifiedBy", lastModifiedBy);
    }

    /**
     * Filter by last modified date between two dates.
     */
    public static Specification<ClientAccount> withLastModifiedDateBetween(Instant start, Instant end) {
        return BaseSpecification.between("lastModifiedDate", start, end);
    }
}
