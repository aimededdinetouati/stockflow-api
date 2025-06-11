package com.adeem.stockflow.service.criteria;

import com.adeem.stockflow.domain.*;
import com.adeem.stockflow.service.criteria.filter.CustomerCriteria;
import jakarta.persistence.criteria.JoinType;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;
import tech.jhipster.service.QueryService;

/**
 * Specifications for filtering Customer entities.
 */
public class CustomerSpecification extends QueryService<Customer> {

    /**
     * Return a {@link Specification} based on the given criteria.
     *
     * @param criteria the criteria to create the specification from.
     * @return the specification.
     */
    public static Specification<Customer> createSpecification(CustomerCriteria criteria) {
        CustomerSpecification helper = new CustomerSpecification();
        Specification<Customer> specification = Specification.where(null);

        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(helper.buildRangeSpecification(criteria.getId(), Customer_.id));
            }
            if (criteria.getFirstName() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getFirstName(), Customer_.firstName));
            }
            if (criteria.getLastName() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getLastName(), Customer_.lastName));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getPhone(), Customer_.phone));
            }
            if (criteria.getFax() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getFax(), Customer_.fax));
            }
            if (criteria.getTaxId() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getTaxId(), Customer_.taxId));
            }
            if (criteria.getRegistrationArticle() != null) {
                specification = specification.and(
                    helper.buildStringSpecification(criteria.getRegistrationArticle(), Customer_.registrationArticle)
                );
            }
            if (criteria.getStatisticalId() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getStatisticalId(), Customer_.statisticalId));
            }
            if (criteria.getRc() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getRc(), Customer_.rc));
            }
            if (criteria.getEnabled() != null) {
                specification = specification.and(helper.buildSpecification(criteria.getEnabled(), Customer_.enabled));
            }
            if (criteria.getCreatedByClientAccountId() != null) {
                specification = specification.and(
                    helper.buildSpecification(criteria.getCreatedByClientAccountId(), root ->
                        root.get(Customer_.createdByClientAccount).get(ClientAccount_.id)
                    )
                );
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(
                    helper.buildSpecification(criteria.getUserId(), root -> root.get(Customer_.user).get(User_.id))
                );
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getCreatedBy(), Customer_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(helper.buildRangeSpecification(criteria.getCreatedDate(), Customer_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(helper.buildStringSpecification(criteria.getLastModifiedBy(), Customer_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(
                    helper.buildRangeSpecification(criteria.getLastModifiedDate(), Customer_.lastModifiedDate)
                );
            }
        }

        return specification;
    }

    public static Specification<Customer> withId(Long id) {
        return BaseSpecification.equals("id", id);
    }

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
     * Filter by full name (first name + last name, case-insensitive, partial match).
     */
    public static Specification<Customer> withFullName(String fullName) {
        return (root, query, criteriaBuilder) -> {
            if (fullName == null || fullName.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String searchTerm = "%" + fullName.toLowerCase().trim() + "%";
            var firstName = criteriaBuilder.lower(root.get("firstName"));
            var lastName = criteriaBuilder.lower(root.get("lastName"));
            var fullNameConcat = criteriaBuilder.concat(criteriaBuilder.concat(firstName, " "), lastName);
            return criteriaBuilder.like(fullNameConcat, searchTerm);
        };
    }

    /**
     * Filter by phone number (exact match).
     */
    public static Specification<Customer> withPhone(String phone) {
        return BaseSpecification.equals("phone", phone);
    }

    /**
     * Filter by phone number (partial match).
     */
    public static Specification<Customer> withPhoneContaining(String phone) {
        return BaseSpecification.contains("phone", phone);
    }

    /**
     * Filter by fax number (exact match).
     */
    public static Specification<Customer> withFax(String fax) {
        return BaseSpecification.equals("fax", fax);
    }

    /**
     * Filter by tax ID (exact match).
     */
    public static Specification<Customer> withTaxId(String taxId) {
        return BaseSpecification.equals("taxId", taxId);
    }

    /**
     * Filter by registration article (exact match).
     */
    public static Specification<Customer> withRegistrationArticle(String registrationArticle) {
        return BaseSpecification.equals("registrationArticle", registrationArticle);
    }

    /**
     * Filter by statistical ID (exact match).
     */
    public static Specification<Customer> withStatisticalId(String statisticalId) {
        return BaseSpecification.equals("statisticalId", statisticalId);
    }

    /**
     * Filter by RC (exact match).
     */
    public static Specification<Customer> withRc(String rc) {
        return BaseSpecification.equals("rc", rc);
    }

    /**
     * Filter by enabled status.
     */
    public static Specification<Customer> withEnabled(Boolean enabled) {
        return enabled ? BaseSpecification.isTrue("enabled") : BaseSpecification.isFalse("enabled");
    }

    /**
     * Filter by enabled customers only.
     */
    public static Specification<Customer> onlyEnabled() {
        return BaseSpecification.isTrue("enabled");
    }

    /**
     * Filter by disabled customers only.
     */
    public static Specification<Customer> onlyDisabled() {
        return BaseSpecification.isFalse("enabled");
    }

    /**
     * Filter by created by client account ID.
     */
    public static Specification<Customer> withCreatedByClientAccountId(Long clientAccountId) {
        return withClientAccountId(clientAccountId);
    }

    /**
     * Filter by user ID.
     */
    public static Specification<Customer> withUserId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("user", JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), userId);
        };
    }

    /**
     * Filter by user login/username.
     */
    public static Specification<Customer> withUserLogin(String login) {
        return (root, query, criteriaBuilder) -> {
            if (login == null || login.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("user", JoinType.LEFT);
            return criteriaBuilder.equal(join.get("login"), login);
        };
    }

    /**
     * Filter by user email.
     */
    public static Specification<Customer> withUserEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null || email.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("user", JoinType.LEFT);
            return criteriaBuilder.equal(join.get("email"), email);
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
            var join = root.join("createdByClientAccount", JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), clientAccountId);
        };
    }

    /**
     * Filter by client account name.
     */
    public static Specification<Customer> withClientAccountName(String accountName) {
        return (root, query, criteriaBuilder) -> {
            if (accountName == null || accountName.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("createdByClientAccount", JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(join.get("name")), "%" + accountName.toLowerCase().trim() + "%");
        };
    }

    /**
     * Filter customers that have no associated user.
     */
    public static Specification<Customer> withoutUser() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("user"));
    }

    /**
     * Filter customers that have an associated user.
     */
    public static Specification<Customer> withUser() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("user"));
    }

    /**
     * Filter by created by username.
     */
    public static Specification<Customer> withCreatedBy(String createdBy) {
        return BaseSpecification.equals("createdBy", createdBy);
    }

    /**
     * Filter by creation date after the specified date.
     */
    public static Specification<Customer> withCreatedDateAfter(Instant date) {
        return BaseSpecification.greaterThan("createdDate", date);
    }

    /**
     * Filter by creation date before the specified date.
     */
    public static Specification<Customer> withCreatedDateBefore(Instant date) {
        return BaseSpecification.lessThan("createdDate", date);
    }

    /**
     * Filter by creation date between the specified dates.
     */
    public static Specification<Customer> withCreatedDateBetween(Instant from, Instant to) {
        return BaseSpecification.between("createdDate", from, to);
    }

    /**
     * Filter by last modified by username.
     */
    public static Specification<Customer> withLastModifiedBy(String lastModifiedBy) {
        return BaseSpecification.equals("lastModifiedBy", lastModifiedBy);
    }

    /**
     * Filter by last modification date after the specified date.
     */
    public static Specification<Customer> withLastModifiedDateAfter(Instant date) {
        return BaseSpecification.greaterThan("lastModifiedDate", date);
    }

    /**
     * Filter by last modification date before the specified date.
     */
    public static Specification<Customer> withLastModifiedDateBefore(Instant date) {
        return BaseSpecification.lessThan("lastModifiedDate", date);
    }

    /**
     * Filter by last modification date between the specified dates.
     */
    public static Specification<Customer> withLastModifiedDateBetween(Instant from, Instant to) {
        return BaseSpecification.between("lastModifiedDate", from, to);
    }

    /**
     * Filter customers created recently (within the last specified days).
     */
    public static Specification<Customer> withRecentlyCreated(int days) {
        return (root, query, criteriaBuilder) -> {
            Instant cutoffDate = Instant.now().minus(java.time.Duration.ofDays(days));
            return criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), cutoffDate);
        };
    }

    /**
     * Filter customers modified recently (within the last specified days).
     */
    public static Specification<Customer> withRecentlyModified(int days) {
        return (root, query, criteriaBuilder) -> {
            Instant cutoffDate = Instant.now().minus(java.time.Duration.ofDays(days));
            return criteriaBuilder.greaterThanOrEqualTo(root.get("lastModifiedDate"), cutoffDate);
        };
    }

    /**
     * Search customers by a general search term that matches first name, last name, phone, tax ID, or user email.
     */
    public static Specification<Customer> withGeneralSearch(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String likeTerm = "%" + searchTerm.toLowerCase().trim() + "%";
            var userJoin = root.join("user", JoinType.LEFT);

            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), likeTerm),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), likeTerm),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), likeTerm),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("taxId")), likeTerm),
                criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("email")), likeTerm)
            );
        };
    }
}
