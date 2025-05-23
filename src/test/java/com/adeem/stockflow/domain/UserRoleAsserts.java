package com.adeem.stockflow.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRoleAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertUserRoleAllPropertiesEquals(UserRole expected, UserRole actual) {
        assertUserRoleAutoGeneratedPropertiesEquals(expected, actual);
        assertUserRoleAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertUserRoleAllUpdatablePropertiesEquals(UserRole expected, UserRole actual) {
        assertUserRoleUpdatableFieldsEquals(expected, actual);
        assertUserRoleUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertUserRoleAutoGeneratedPropertiesEquals(UserRole expected, UserRole actual) {
        assertThat(actual)
            .as("Verify UserRole auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()))
            .satisfies(a -> assertThat(a.getCreatedBy()).as("check createdBy").isEqualTo(expected.getCreatedBy()))
            .satisfies(a -> assertThat(a.getCreatedDate()).as("check createdDate").isEqualTo(expected.getCreatedDate()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertUserRoleUpdatableFieldsEquals(UserRole expected, UserRole actual) {
        // empty method

    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertUserRoleUpdatableRelationshipsEquals(UserRole expected, UserRole actual) {
        assertThat(actual)
            .as("Verify UserRole relationships")
            .satisfies(a -> assertThat(a.getAdmin()).as("check admin").isEqualTo(expected.getAdmin()))
            .satisfies(a -> assertThat(a.getRole()).as("check role").isEqualTo(expected.getRole()));
    }
}
