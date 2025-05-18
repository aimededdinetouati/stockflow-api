package com.adeem.stockflow.web.rest;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.Admin;
import com.adeem.stockflow.domain.Authority;
import com.adeem.stockflow.domain.User;
import com.adeem.stockflow.repository.AdminRepository;
import com.adeem.stockflow.repository.AuthorityRepository;
import com.adeem.stockflow.repository.ClientAccountRepository;
import com.adeem.stockflow.repository.UserRepository;
import com.adeem.stockflow.security.AuthoritiesConstants;
import com.adeem.stockflow.web.rest.vm.LoginVM;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AuthenticateController} REST controller.
 */
@AutoConfigureMockMvc
@IntegrationTest
class AuthenticateControllerIT {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientAccountRepository clientAccountRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    void testAuthorize() throws Exception {
        User user = new User();
        user.setLogin("user-jwt-controller");
        user.setEmail("user-jwt-controller@example.com");
        user.setActivated(true);
        user.setPassword(passwordEncoder.encode("test"));
        userRepository.saveAndFlush(user);

        LoginVM login = new LoginVM();
        login.setUsername("user-jwt-controller");
        login.setPassword("test");
        mockMvc
            .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id_token").isString())
            .andExpect(jsonPath("$.id_token").isNotEmpty())
            .andExpect(header().string("Authorization", not(nullValue())))
            .andExpect(header().string("Authorization", not(is(emptyString()))));

        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void testAuthorizeWithClientAccountClaim() throws Exception {
        User user = new User();
        user.setLogin("user-jwt-controller2");
        user.setEmail("user-jwt-controller2@example.com");
        user.setActivated(true);
        user.setPassword(passwordEncoder.encode("test2"));
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authorityRepository.findByName(AuthoritiesConstants.USER_ADMIN).get());
        user.setAuthorities(authorities);
        userRepository.saveAndFlush(user);

        var clientAccount = clientAccountRepository.saveAndFlush(ClientAccountResourceIT.createEntity());

        Admin admin = new Admin();
        admin.setClientAccount(clientAccount);
        admin.setUser(user);
        admin.assignedDate(Instant.now());
        adminRepository.saveAndFlush(admin);

        LoginVM login = new LoginVM();
        login.setUsername("user-jwt-controller2");
        login.setPassword("test2");
        mockMvc
            .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id_token").isString())
            .andExpect(jsonPath("$.id_token").isNotEmpty())
            .andExpect(header().string("Authorization", not(nullValue())))
            .andExpect(header().string("Authorization", not(is(emptyString()))));

        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void testAuthorizeWithRememberMe() throws Exception {
        User user = new User();
        user.setLogin("user-jwt-controller-remember-me");
        user.setEmail("user-jwt-controller-remember-me@example.com");
        user.setActivated(true);
        user.setPassword(passwordEncoder.encode("test"));

        userRepository.saveAndFlush(user);

        LoginVM login = new LoginVM();
        login.setUsername("user-jwt-controller-remember-me");
        login.setPassword("test");
        login.setRememberMe(true);
        mockMvc
            .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id_token").isString())
            .andExpect(jsonPath("$.id_token").isNotEmpty())
            .andExpect(header().string("Authorization", not(nullValue())))
            .andExpect(header().string("Authorization", not(is(emptyString()))));

        userRepository.deleteAll();
    }

    @Test
    void testAuthorizeFails() throws Exception {
        LoginVM login = new LoginVM();
        login.setUsername("wrong-user");
        login.setPassword("wrong password");
        mockMvc
            .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(login)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.id_token").doesNotExist())
            .andExpect(header().doesNotExist("Authorization"));

        userRepository.deleteAll();
    }
}
