package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.AdminAsserts.*;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.Admin;
import com.adeem.stockflow.repository.AdminRepository;
import com.adeem.stockflow.repository.UserRepository;
import com.adeem.stockflow.service.dto.AdminDTO;
import com.adeem.stockflow.service.mapper.AdminMapper;
import com.adeem.stockflow.web.rest.uncostomized.AdminResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AdminResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AdminResourceIT {

    private static final Instant DEFAULT_ASSIGNED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ASSIGNED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/admins";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAdminMockMvc;

    private Admin admin;

    private Admin insertedAdmin;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Admin createEntity() {
        return new Admin().assignedDate(DEFAULT_ASSIGNED_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Admin createUpdatedEntity() {
        return new Admin().assignedDate(UPDATED_ASSIGNED_DATE);
    }

    @BeforeEach
    void initTest() {
        admin = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAdmin != null) {
            adminRepository.delete(insertedAdmin);
            insertedAdmin = null;
        }
    }

    @Test
    @Transactional
    void createAdmin() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Admin
        AdminDTO adminDTO = adminMapper.toDto(admin);
        var returnedAdminDTO = om.readValue(
            restAdminMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(adminDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AdminDTO.class
        );

        // Validate the Admin in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAdmin = adminMapper.toEntity(returnedAdminDTO);
        assertAdminUpdatableFieldsEquals(returnedAdmin, getPersistedAdmin(returnedAdmin));

        insertedAdmin = returnedAdmin;
    }

    @Test
    @Transactional
    void createAdminWithExistingId() throws Exception {
        // Create the Admin with an existing ID
        admin.setId(1L);
        AdminDTO adminDTO = adminMapper.toDto(admin);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAdminMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(adminDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Admin in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAssignedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        admin.setAssignedDate(null);

        // Create the Admin, which fails.
        AdminDTO adminDTO = adminMapper.toDto(admin);

        restAdminMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(adminDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAdmins() throws Exception {
        // Initialize the database
        insertedAdmin = adminRepository.saveAndFlush(admin);

        // Get all the adminList
        restAdminMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(admin.getId().intValue())))
            .andExpect(jsonPath("$.[*].assignedDate").value(hasItem(DEFAULT_ASSIGNED_DATE.toString())));
    }

    @Test
    @Transactional
    void getAdmin() throws Exception {
        // Initialize the database
        insertedAdmin = adminRepository.saveAndFlush(admin);

        // Get the admin
        restAdminMockMvc
            .perform(get(ENTITY_API_URL_ID, admin.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(admin.getId().intValue()))
            .andExpect(jsonPath("$.assignedDate").value(DEFAULT_ASSIGNED_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAdmin() throws Exception {
        // Get the admin
        restAdminMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAdmin() throws Exception {
        // Initialize the database
        insertedAdmin = adminRepository.saveAndFlush(admin);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the admin
        Admin updatedAdmin = adminRepository.findById(admin.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAdmin are not directly saved in db
        em.detach(updatedAdmin);
        updatedAdmin.assignedDate(UPDATED_ASSIGNED_DATE);
        AdminDTO adminDTO = adminMapper.toDto(updatedAdmin);

        restAdminMockMvc
            .perform(
                put(ENTITY_API_URL_ID, adminDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(adminDTO))
            )
            .andExpect(status().isOk());

        // Validate the Admin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAdminToMatchAllProperties(updatedAdmin);
    }

    @Test
    @Transactional
    void putNonExistingAdmin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        admin.setId(longCount.incrementAndGet());

        // Create the Admin
        AdminDTO adminDTO = adminMapper.toDto(admin);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAdminMockMvc
            .perform(
                put(ENTITY_API_URL_ID, adminDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(adminDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Admin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAdmin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        admin.setId(longCount.incrementAndGet());

        // Create the Admin
        AdminDTO adminDTO = adminMapper.toDto(admin);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdminMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(adminDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Admin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAdmin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        admin.setId(longCount.incrementAndGet());

        // Create the Admin
        AdminDTO adminDTO = adminMapper.toDto(admin);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdminMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(adminDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Admin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAdminWithPatch() throws Exception {
        // Initialize the database
        insertedAdmin = adminRepository.saveAndFlush(admin);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the admin using partial update
        Admin partialUpdatedAdmin = new Admin();
        partialUpdatedAdmin.setId(admin.getId());

        partialUpdatedAdmin.assignedDate(UPDATED_ASSIGNED_DATE);

        restAdminMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAdmin.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAdmin))
            )
            .andExpect(status().isOk());

        // Validate the Admin in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAdminUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAdmin, admin), getPersistedAdmin(admin));
    }

    @Test
    @Transactional
    void fullUpdateAdminWithPatch() throws Exception {
        // Initialize the database
        insertedAdmin = adminRepository.saveAndFlush(admin);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the admin using partial update
        Admin partialUpdatedAdmin = new Admin();
        partialUpdatedAdmin.setId(admin.getId());

        partialUpdatedAdmin.assignedDate(UPDATED_ASSIGNED_DATE);

        restAdminMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAdmin.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAdmin))
            )
            .andExpect(status().isOk());

        // Validate the Admin in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAdminUpdatableFieldsEquals(partialUpdatedAdmin, getPersistedAdmin(partialUpdatedAdmin));
    }

    @Test
    @Transactional
    void patchNonExistingAdmin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        admin.setId(longCount.incrementAndGet());

        // Create the Admin
        AdminDTO adminDTO = adminMapper.toDto(admin);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAdminMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, adminDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(adminDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Admin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAdmin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        admin.setId(longCount.incrementAndGet());

        // Create the Admin
        AdminDTO adminDTO = adminMapper.toDto(admin);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdminMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(adminDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Admin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAdmin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        admin.setId(longCount.incrementAndGet());

        // Create the Admin
        AdminDTO adminDTO = adminMapper.toDto(admin);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdminMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(adminDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Admin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAdmin() throws Exception {
        // Initialize the database
        insertedAdmin = adminRepository.saveAndFlush(admin);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the admin
        restAdminMockMvc
            .perform(delete(ENTITY_API_URL_ID, admin.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return adminRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Admin getPersistedAdmin(Admin admin) {
        return adminRepository.findById(admin.getId()).orElseThrow();
    }

    protected void assertPersistedAdminToMatchAllProperties(Admin expectedAdmin) {
        assertAdminAllPropertiesEquals(expectedAdmin, getPersistedAdmin(expectedAdmin));
    }

    protected void assertPersistedAdminToMatchUpdatableProperties(Admin expectedAdmin) {
        assertAdminAllUpdatablePropertiesEquals(expectedAdmin, getPersistedAdmin(expectedAdmin));
    }
}
