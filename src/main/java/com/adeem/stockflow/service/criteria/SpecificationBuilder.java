package com.adeem.stockflow.service.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.jpa.domain.Specification;

/**
 * Utility class to help build complex specifications by combining multiple criteria.
 * @param <T> The entity type for the specification
 */
public class SpecificationBuilder<T> {

    private final List<Specification<T>> specifications = new ArrayList<>();

    /**
     * Add a specification if the condition is true.
     *
     * @param condition The condition to check
     * @param specificationFunction The function to create the specification
     * @return The builder instance for chaining
     */
    public SpecificationBuilder<T> with(boolean condition, Function<Boolean, Specification<T>> specificationFunction) {
        if (condition) {
            specifications.add(specificationFunction.apply(true));
        }
        return this;
    }

    /**
     * Add a specification if the value is not null.
     *
     * @param value The value to check for null
     * @param specificationFunction The function to create the specification using the value
     * @param <V> The type of the value
     * @return The builder instance for chaining
     */
    public <V> SpecificationBuilder<T> with(V value, Function<V, Specification<T>> specificationFunction) {
        if (value != null) {
            specifications.add(specificationFunction.apply(value));
        }
        return this;
    }

    /**
     * Add a specification for a string value if it's not null or empty.
     *
     * @param value The string value to check
     * @param specificationFunction The function to create the specification using the value
     * @return The builder instance for chaining
     */
    public SpecificationBuilder<T> withString(String value, Function<String, Specification<T>> specificationFunction) {
        if (value != null && !value.isEmpty()) {
            specifications.add(specificationFunction.apply(value));
        }
        return this;
    }

    /**
     * Add a specification unconditionally.
     *
     * @param specification The specification to add
     * @return The builder instance for chaining
     */
    public SpecificationBuilder<T> with(Specification<T> specification) {
        specifications.add(specification);
        return this;
    }

    /**
     * Build the final specification by combining all added specifications with AND logic.
     *
     * @return The combined specification, or null if no specifications were added
     */
    public Specification<T> build() {
        if (specifications.isEmpty()) {
            return null;
        }

        Specification<T> result = Specification.where(specifications.get(0));
        for (int i = 1; i < specifications.size(); i++) {
            result = result.and(specifications.get(i));
        }
        return result;
    }

    /**
     * Build the final specification by combining all added specifications with OR logic.
     *
     * @return The combined specification, or null if no specifications were added
     */
    public Specification<T> buildOr() {
        if (specifications.isEmpty()) {
            return null;
        }

        Specification<T> result = Specification.where(specifications.get(0));
        for (int i = 1; i < specifications.size(); i++) {
            result = result.or(specifications.get(i));
        }
        return result;
    }
}
