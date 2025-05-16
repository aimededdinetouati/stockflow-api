package com.adeem.stockflow.web.rest;

import static com.adeem.stockflow.domain.RolePermissionAsserts.*;
import static com.adeem.stockflow.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adeem.stockflow.IntegrationTest;
import com.adeem.stockflow.domain.RolePermission;
import com.adeem.stockflow.repository.RolePermissionRepository;
import com.adeem.stockflow.service.dto.RolePermissionDTO;
import com.adeem.stockflow.service.mapper.RolePermissionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link RolePermissionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RolePermissionResourceIT {

    private static final String ENTITY_API_URL = "/api/role-permissions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRolePermissionMockMvc;

    private RolePermission rolePermission;

    private RolePermission insertedRolePermission;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RolePermission createEntity() {
        return new RolePermission();
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RolePermission createUpdatedEntity() {
        return new RolePermission();
    }

    @BeforeEach
    void initTest() {
        rolePermission = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRolePermission != null) {
            rolePermissionRepository.delete(insertedRolePermission);
            insertedRolePermission = null;
        }
    }

    @Test
    @Transactional
    void createRolePermission() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RolePermission
        RolePermissionDTO rolePermissionDTO = rolePermissionMapper.toDto(rolePermission);
        var returnedRolePermissionDTO = om.readValue(
            restRolePermissionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rolePermissionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RolePermissionDTO.class
        );

        // Validate the RolePermission in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRolePermission = rolePermissionMapper.toEntity(returnedRolePermissionDTO);
        assertRolePermissionUpdatableFieldsEquals(returnedRolePermission, getPersistedRolePermission(returnedRolePermission));

        insertedRolePermission = returnedRolePermission;
    }

    @Test
    @Transactional
    void createRolePermissionWithExistingId() throws Exception {
        // Create the RolePermission with an existing ID
        rolePermission.setId(1L);
        RolePermissionDTO rolePermissionDTO = rolePermissionMapper.toDto(rolePermission);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRolePermissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rolePermissionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RolePermission in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRolePermissions() throws Exception {
        // Initialize the database
        insertedRolePermission = rolePermissionRepository.saveAndFlush(rolePermission);

        // Get all the rolePermissionList
        restRolePermissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rolePermission.getId().intValue())));
    }

    @Test
    @Transactional
    void getRolePermission() throws Exception {
        // Initialize the database
        insertedRolePermission = rolePermissionRepository.saveAndFlush(rolePermission);

        // Get the rolePermission
        restRolePermissionMockMvc
            .perform(get(ENTITY_API_URL_ID, rolePermission.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rolePermission.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingRolePermission() throws Exception {
        // Get the rolePermission
        restRolePermissionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRolePermission() throws Exception {
        // Initialize the database
        insertedRolePermission = rolePermissionRepository.saveAndFlush(rolePermission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rolePermission
        RolePermission updatedRolePermission = rolePermissionRepository.findById(rolePermission.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRolePermission are not directly saved in db
        em.detach(updatedRolePermission);
        RolePermissionDTO rolePermissionDTO = rolePermissionMapper.toDto(updatedRolePermission);

        restRolePermissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rolePermissionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(rolePermissionDTO))
            )
            .andExpect(status().isOk());

        // Validate the RolePermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRolePermissionToMatchAllProperties(updatedRolePermission);
    }

    @Test
    @Transactional
    void putNonExistingRolePermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rolePermission.setId(longCount.incrementAndGet());

        // Create the RolePermission
        RolePermissionDTO rolePermissionDTO = rolePermissionMapper.toDto(rolePermission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRolePermissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rolePermissionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(rolePermissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RolePermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRolePermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rolePermission.setId(longCount.incrementAndGet());

        // Create the RolePermission
        RolePermissionDTO rolePermissionDTO = rolePermissionMapper.toDto(rolePermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRolePermissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(rolePermissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RolePermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRolePermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rolePermission.setId(longCount.incrementAndGet());

        // Create the RolePermission
        RolePermissionDTO rolePermissionDTO = rolePermissionMapper.toDto(rolePermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRolePermissionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rolePermissionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RolePermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRolePermissionWithPatch() throws Exception {
        // Initialize the database
        insertedRolePermission = rolePermissionRepository.saveAndFlush(rolePermission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rolePermission using partial update
        RolePermission partialUpdatedRolePermission = new RolePermission();
        partialUpdatedRolePermission.setId(rolePermission.getId());

        restRolePermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRolePermission.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRolePermission))
            )
            .andExpect(status().isOk());

        // Validate the RolePermission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRolePermissionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRolePermission, rolePermission),
            getPersistedRolePermission(rolePermission)
        );
    }

    @Test
    @Transactional
    void fullUpdateRolePermissionWithPatch() throws Exception {
        // Initialize the database
        insertedRolePermission = rolePermissionRepository.saveAndFlush(rolePermission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rolePermission using partial update
        RolePermission partialUpdatedRolePermission = new RolePermission();
        partialUpdatedRolePermission.setId(rolePermission.getId());

        restRolePermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRolePermission.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRolePermission))
            )
            .andExpect(status().isOk());

        // Validate the RolePermission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRolePermissionUpdatableFieldsEquals(partialUpdatedRolePermission, getPersistedRolePermission(partialUpdatedRolePermission));
    }

    @Test
    @Transactional
    void patchNonExistingRolePermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rolePermission.setId(longCount.incrementAndGet());

        // Create the RolePermission
        RolePermissionDTO rolePermissionDTO = rolePermissionMapper.toDto(rolePermission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRolePermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, rolePermissionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(rolePermissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RolePermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRolePermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rolePermission.setId(longCount.incrementAndGet());

        // Create the RolePermission
        RolePermissionDTO rolePermissionDTO = rolePermissionMapper.toDto(rolePermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRolePermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(rolePermissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RolePermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRolePermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rolePermission.setId(longCount.incrementAndGet());

        // Create the RolePermission
        RolePermissionDTO rolePermissionDTO = rolePermissionMapper.toDto(rolePermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRolePermissionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(rolePermissionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RolePermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRolePermission() throws Exception {
        // Initialize the database
        insertedRolePermission = rolePermissionRepository.saveAndFlush(rolePermission);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the rolePermission
        restRolePermissionMockMvc
            .perform(delete(ENTITY_API_URL_ID, rolePermission.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return rolePermissionRepository.count();
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

    protected RolePermission getPersistedRolePermission(RolePermission rolePermission) {
        return rolePermissionRepository.findById(rolePermission.getId()).orElseThrow();
    }

    protected void assertPersistedRolePermissionToMatchAllProperties(RolePermission expectedRolePermission) {
        assertRolePermissionAllPropertiesEquals(expectedRolePermission, getPersistedRolePermission(expectedRolePermission));
    }

    protected void assertPersistedRolePermissionToMatchUpdatableProperties(RolePermission expectedRolePermission) {
        assertRolePermissionAllUpdatablePropertiesEquals(expectedRolePermission, getPersistedRolePermission(expectedRolePermission));
    }
}
