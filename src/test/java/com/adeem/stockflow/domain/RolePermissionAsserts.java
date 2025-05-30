package com.adeem.stockflow.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class RolePermissionAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertRolePermissionAllPropertiesEquals(RolePermission expected, RolePermission actual) {
        assertRolePermissionAutoGeneratedPropertiesEquals(expected, actual);
        assertRolePermissionAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertRolePermissionAllUpdatablePropertiesEquals(RolePermission expected, RolePermission actual) {
        assertRolePermissionUpdatableFieldsEquals(expected, actual);
        assertRolePermissionUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertRolePermissionAutoGeneratedPropertiesEquals(RolePermission expected, RolePermission actual) {
        assertThat(actual)
            .as("Verify RolePermission auto generated properties")
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
    public static void assertRolePermissionUpdatableFieldsEquals(RolePermission expected, RolePermission actual) {
        // empty method

    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertRolePermissionUpdatableRelationshipsEquals(RolePermission expected, RolePermission actual) {
        assertThat(actual)
            .as("Verify RolePermission relationships")
            .satisfies(a -> assertThat(a.getRole()).as("check role").isEqualTo(expected.getRole()))
            .satisfies(a -> assertThat(a.getPermission()).as("check permission").isEqualTo(expected.getPermission()));
    }
}
