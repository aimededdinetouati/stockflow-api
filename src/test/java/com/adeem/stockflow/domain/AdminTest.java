package com.adeem.stockflow.domain;

import static com.adeem.stockflow.domain.AdminTestSamples.*;
import static com.adeem.stockflow.domain.ClientAccountTestSamples.*;
import static com.adeem.stockflow.domain.UserRoleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AdminTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Admin.class);
        Admin admin1 = getAdminSample1();
        Admin admin2 = new Admin();
        assertThat(admin1).isNotEqualTo(admin2);

        admin2.setId(admin1.getId());
        assertThat(admin1).isEqualTo(admin2);

        admin2 = getAdminSample2();
        assertThat(admin1).isNotEqualTo(admin2);
    }

    @Test
    void userRolesTest() {
        Admin admin = getAdminRandomSampleGenerator();
        UserRole userRoleBack = getUserRoleRandomSampleGenerator();

        admin.addUserRoles(userRoleBack);
        assertThat(admin.getUserRoles()).containsOnly(userRoleBack);
        assertThat(userRoleBack.getAdmin()).isEqualTo(admin);

        admin.removeUserRoles(userRoleBack);
        assertThat(admin.getUserRoles()).doesNotContain(userRoleBack);
        assertThat(userRoleBack.getAdmin()).isNull();

        admin.userRoles(new HashSet<>(Set.of(userRoleBack)));
        assertThat(admin.getUserRoles()).containsOnly(userRoleBack);
        assertThat(userRoleBack.getAdmin()).isEqualTo(admin);

        admin.setUserRoles(new HashSet<>());
        assertThat(admin.getUserRoles()).doesNotContain(userRoleBack);
        assertThat(userRoleBack.getAdmin()).isNull();
    }

    @Test
    void clientAccountTest() {
        Admin admin = getAdminRandomSampleGenerator();
        ClientAccount clientAccountBack = getClientAccountRandomSampleGenerator();

        admin.setClientAccount(clientAccountBack);
        assertThat(admin.getClientAccount()).isEqualTo(clientAccountBack);

        admin.clientAccount(null);
        assertThat(admin.getClientAccount()).isNull();
    }
}
