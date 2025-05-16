package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.RolePermission;
import com.adeem.stockflow.repository.RolePermissionRepository;
import com.adeem.stockflow.service.dto.RolePermissionDTO;
import com.adeem.stockflow.service.mapper.RolePermissionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.RolePermission}.
 */
@Service
@Transactional
public class RolePermissionService {

    private static final Logger LOG = LoggerFactory.getLogger(RolePermissionService.class);

    private final RolePermissionRepository rolePermissionRepository;

    private final RolePermissionMapper rolePermissionMapper;

    public RolePermissionService(RolePermissionRepository rolePermissionRepository, RolePermissionMapper rolePermissionMapper) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.rolePermissionMapper = rolePermissionMapper;
    }

    /**
     * Save a rolePermission.
     *
     * @param rolePermissionDTO the entity to save.
     * @return the persisted entity.
     */
    public RolePermissionDTO save(RolePermissionDTO rolePermissionDTO) {
        LOG.debug("Request to save RolePermission : {}", rolePermissionDTO);
        RolePermission rolePermission = rolePermissionMapper.toEntity(rolePermissionDTO);
        rolePermission = rolePermissionRepository.save(rolePermission);
        return rolePermissionMapper.toDto(rolePermission);
    }

    /**
     * Update a rolePermission.
     *
     * @param rolePermissionDTO the entity to save.
     * @return the persisted entity.
     */
    public RolePermissionDTO update(RolePermissionDTO rolePermissionDTO) {
        LOG.debug("Request to update RolePermission : {}", rolePermissionDTO);
        RolePermission rolePermission = rolePermissionMapper.toEntity(rolePermissionDTO);
        rolePermission.setIsPersisted();
        rolePermission = rolePermissionRepository.save(rolePermission);
        return rolePermissionMapper.toDto(rolePermission);
    }

    /**
     * Partially update a rolePermission.
     *
     * @param rolePermissionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RolePermissionDTO> partialUpdate(RolePermissionDTO rolePermissionDTO) {
        LOG.debug("Request to partially update RolePermission : {}", rolePermissionDTO);

        return rolePermissionRepository
            .findById(rolePermissionDTO.getId())
            .map(existingRolePermission -> {
                rolePermissionMapper.partialUpdate(existingRolePermission, rolePermissionDTO);

                return existingRolePermission;
            })
            .map(rolePermissionRepository::save)
            .map(rolePermissionMapper::toDto);
    }

    /**
     * Get all the rolePermissions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RolePermissionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all RolePermissions");
        return rolePermissionRepository.findAll(pageable).map(rolePermissionMapper::toDto);
    }

    /**
     * Get one rolePermission by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RolePermissionDTO> findOne(Long id) {
        LOG.debug("Request to get RolePermission : {}", id);
        return rolePermissionRepository.findById(id).map(rolePermissionMapper::toDto);
    }

    /**
     * Delete the rolePermission by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete RolePermission : {}", id);
        rolePermissionRepository.deleteById(id);
    }
}
