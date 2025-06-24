package com.adeem.stockflow.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.security.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Test class for the custom security test annotations and utilities.
 */
@IntegrationTest
@AutoConfigureMockMvc
class SecurityTestAnnotationsIT {

    @AfterEach
    void cleanup() {
        // Always clear the security context after each test
        TestSecurityContextHelper.clearSecurityContext();
    }

    @Test
    @WithMockClientAccount(value = 123L)
    void testWithMockClientAccount() {
        // Verify that client account ID is properly set in the security context
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();
        assertThat(clientAccountId).isEqualTo(123L);
    }

    @Test
    @WithMockUser
    void testWithMockUserHasNoClientAccountId() {
        // Verify that with regular @WithMockUser, we get an exception
        try {
            SecurityUtils.getCurrentClientAccountId();
            // If we get here, the test has failed
            assertThat(false).isTrue();
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("User not associated with a client account");
        }
    }

    @Test
    void testProgrammaticSecurityContext() {
        // Programmatically set up security context
        Long testClientAccountId = 456L;
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(testClientAccountId);

        // Verify it works
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();
        assertThat(clientAccountId).isEqualTo(testClientAccountId);
    }

    @Test
    void testProgrammaticSecurityContextWithCustomUserId() {
        // Programmatically set up security context with custom user ID
        Long testClientAccountId = 789L;
        Long testUserId = 999L;
        TestSecurityContextHelper.setSecurityContextWithClientAccountId(testClientAccountId, testUserId, "testuser");

        // Verify client account ID
        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();
        assertThat(clientAccountId).isEqualTo(testClientAccountId);

        // Verify user ID
        Long userId = SecurityUtils.getCurrentUserId();
        assertThat(userId).isEqualTo(testUserId);
    }
}
