package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.AdminTestSamples.*;
import static com.adeem.stockflow.domain.RoleTestSamples.*;
import static com.adeem.stockflow.domain.UserRoleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserRoleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserRole.class);
        UserRole userRole1 = getUserRoleSample1();
        UserRole userRole2 = new UserRole();
        assertThat(userRole1).isNotEqualTo(userRole2);

        userRole2.setId(userRole1.getId());
        assertThat(userRole1).isEqualTo(userRole2);

        userRole2 = getUserRoleSample2();
        assertThat(userRole1).isNotEqualTo(userRole2);
    }

    @Test
    void adminTest() {
        UserRole userRole = getUserRoleRandomSampleGenerator();
        Admin adminBack = getAdminRandomSampleGenerator();

        userRole.setAdmin(adminBack);
        assertThat(userRole.getAdmin()).isEqualTo(adminBack);

        userRole.admin(null);
        assertThat(userRole.getAdmin()).isNull();
    }

    @Test
    void roleTest() {
        UserRole userRole = getUserRoleRandomSampleGenerator();
        Role roleBack = getRoleRandomSampleGenerator();

        userRole.setRole(roleBack);
        assertThat(userRole.getRole()).isEqualTo(roleBack);

        userRole.role(null);
        assertThat(userRole.getRole()).isNull();
    }
}
