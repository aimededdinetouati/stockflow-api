package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.ClientAccountTestSamples.*;
import static com.adeem.stockflow.domain.RolePermissionTestSamples.*;
import static com.adeem.stockflow.domain.RoleTestSamples.*;
import static com.adeem.stockflow.domain.UserRoleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RoleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Role.class);
        Role role1 = getRoleSample1();
        Role role2 = new Role();
        assertThat(role1).isNotEqualTo(role2);

        role2.setId(role1.getId());
        assertThat(role1).isEqualTo(role2);

        role2 = getRoleSample2();
        assertThat(role1).isNotEqualTo(role2);
    }

    @Test
    void userRolesTest() {
        Role role = getRoleRandomSampleGenerator();
        UserRole userRoleBack = getUserRoleRandomSampleGenerator();

        role.addUserRoles(userRoleBack);
        assertThat(role.getUserRoles()).containsOnly(userRoleBack);
        assertThat(userRoleBack.getRole()).isEqualTo(role);

        role.removeUserRoles(userRoleBack);
        assertThat(role.getUserRoles()).doesNotContain(userRoleBack);
        assertThat(userRoleBack.getRole()).isNull();

        role.userRoles(new HashSet<>(Set.of(userRoleBack)));
        assertThat(role.getUserRoles()).containsOnly(userRoleBack);
        assertThat(userRoleBack.getRole()).isEqualTo(role);

        role.setUserRoles(new HashSet<>());
        assertThat(role.getUserRoles()).doesNotContain(userRoleBack);
        assertThat(userRoleBack.getRole()).isNull();
    }

    @Test
    void rolePermissionsTest() {
        Role role = getRoleRandomSampleGenerator();
        RolePermission rolePermissionBack = getRolePermissionRandomSampleGenerator();

        role.addRolePermissions(rolePermissionBack);
        assertThat(role.getRolePermissions()).containsOnly(rolePermissionBack);
        assertThat(rolePermissionBack.getRole()).isEqualTo(role);

        role.removeRolePermissions(rolePermissionBack);
        assertThat(role.getRolePermissions()).doesNotContain(rolePermissionBack);
        assertThat(rolePermissionBack.getRole()).isNull();

        role.rolePermissions(new HashSet<>(Set.of(rolePermissionBack)));
        assertThat(role.getRolePermissions()).containsOnly(rolePermissionBack);
        assertThat(rolePermissionBack.getRole()).isEqualTo(role);

        role.setRolePermissions(new HashSet<>());
        assertThat(role.getRolePermissions()).doesNotContain(rolePermissionBack);
        assertThat(rolePermissionBack.getRole()).isNull();
    }

    @Test
    void clientAccountTest() {
        Role role = getRoleRandomSampleGenerator();
        ClientAccount clientAccountBack = getClientAccountRandomSampleGenerator();

        role.setClientAccount(clientAccountBack);
        assertThat(role.getClientAccount()).isEqualTo(clientAccountBack);

        role.clientAccount(null);
        assertThat(role.getClientAccount()).isNull();
    }
}
