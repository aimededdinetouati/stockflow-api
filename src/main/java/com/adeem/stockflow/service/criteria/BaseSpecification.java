package com.adeem.stockflow.service.criteria;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/**
 * Base class for specifications with common filtering operations.
 */
public class BaseSpecification {

    /**
     * Creates a specification for equality comparison.
     *
     * @param field The field to compare
     * @param value The value to compare with
     * @return A specification for equality comparison
     */
    public static <T, U> Specification<T> equals(String field, U value) {
        return (root, query, criteriaBuilder) -> {
            if (value == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(field), value);
        };
    }

    /**
     * Creates a specification for a string containing the given value (case-insensitive).
     *
     * @param field The field to search in
     * @param value The value to search for
     * @return A specification for contains comparison
     */
    public static <T> Specification<T> contains(String field, String value) {
        return (root, query, criteriaBuilder) -> {
            if (value == null || value.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get(field)), "%" + value.toLowerCase() + "%");
        };
    }

    /**
     * Creates a specification for a value greater than the given value.
     *
     * @param field The field to compare
     * @param value The value to compare with
     * @return A specification for greater than comparison
     */
    public static <T> Specification<T> greaterThan(String field, Comparable value) {
        return (root, query, criteriaBuilder) -> {
            if (value == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThan(root.get(field), value);
        };
    }

    /**
     * Creates a specification for a value greater than or equal to the given value.
     *
     * @param field The field to compare
     * @param value The value to compare with
     * @return A specification for greater than or equal comparison
     */
    public static <T> Specification<T> greaterThanOrEqual(String field, Comparable value) {
        return (root, query, criteriaBuilder) -> {
            if (value == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get(field), value);
        };
    }

    /**
     * Creates a specification for a value less than the given value.
     *
     * @param field The field to compare
     * @param value The value to compare with
     * @return A specification for less than comparison
     */
    public static <T> Specification<T> lessThan(String field, Comparable value) {
        return (root, query, criteriaBuilder) -> {
            if (value == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThan(root.get(field), value);
        };
    }

    /**
     * Creates a specification for a value less than or equal to the given value.
     *
     * @param field The field to compare
     * @param value The value to compare with
     * @return A specification for less than or equal comparison
     */
    public static <T> Specification<T> lessThanOrEqual(String field, Comparable value) {
        return (root, query, criteriaBuilder) -> {
            if (value == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get(field), value);
        };
    }

    /**
     * Creates a specification for a value between the given values.
     *
     * @param field The field to compare
     * @param start The start value
     * @param end The end value
     * @return A specification for between comparison
     */
    public static <T> Specification<T> between(String field, Comparable start, Comparable end) {
        return (root, query, criteriaBuilder) -> {
            if (start == null && end == null) {
                return criteriaBuilder.conjunction();
            } else if (start == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(field), end);
            } else if (end == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(field), start);
            }
            return criteriaBuilder.between(root.get(field), start, end);
        };
    }

    /**
     * Creates a specification for a value in the given list of values.
     *
     * @param field The field to compare
     * @param values The list of values
     * @return A specification for in comparison
     */
    public static <T> Specification<T> in(String field, List<?> values) {
        return (root, query, criteriaBuilder) -> {
            if (values == null || values.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            var inClause = criteriaBuilder.in(root.get(field));
            for (Object value : values) {
                inClause.value(value);
            }
            return inClause;
        };
    }

    /**
     * Creates a specification for a boolean field to be true.
     *
     * @param field The field to check
     * @return A specification for isTrue comparison
     */
    public static <T> Specification<T> isTrue(String field) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get(field));
    }

    /**
     * Creates a specification for a boolean field to be false.
     *
     * @param field The field to check
     * @return A specification for isFalse comparison
     */
    public static <T> Specification<T> isFalse(String field) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get(field));
    }

    /**
     * Creates a specification for a join table's field equality comparison.
     *
     * @param joinField The join field
     * @param field The field to compare
     * @param value The value to compare with
     * @return A specification for join equals comparison
     */
    public static <T, J> Specification<T> joinEquals(String joinField, String field, Object value) {
        return (root, query, criteriaBuilder) -> {
            if (value == null) {
                return criteriaBuilder.conjunction();
            }
            Join<T, J> join = root.join(joinField, JoinType.LEFT);
            return criteriaBuilder.equal(join.get(field), value);
        };
    }

    /**
     * Creates a specification for a join table's field contains comparison.
     *
     * @param joinField The join field
     * @param field The field to compare
     * @param value The value to compare with
     * @return A specification for join contains comparison
     */
    public static <T, J> Specification<T> joinContains(String joinField, String field, String value) {
        return (root, query, criteriaBuilder) -> {
            if (value == null || value.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<T, J> join = root.join(joinField, JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(join.get(field)), "%" + value.toLowerCase() + "%");
        };
    }
}
