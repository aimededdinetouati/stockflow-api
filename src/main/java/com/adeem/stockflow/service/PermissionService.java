package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.Permission;
import com.adeem.stockflow.repository.PermissionRepository;
import com.adeem.stockflow.service.dto.PermissionDTO;
import com.adeem.stockflow.service.mapper.PermissionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.Permission}.
 */
@Service
@Transactional
public class PermissionService {

    private static final Logger LOG = LoggerFactory.getLogger(PermissionService.class);

    private final PermissionRepository permissionRepository;

    private final PermissionMapper permissionMapper;

    public PermissionService(PermissionRepository permissionRepository, PermissionMapper permissionMapper) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
    }

    /**
     * Save a permission.
     *
     * @param permissionDTO the entity to save.
     * @return the persisted entity.
     */
    public PermissionDTO save(PermissionDTO permissionDTO) {
        LOG.debug("Request to save Permission : {}", permissionDTO);
        Permission permission = permissionMapper.toEntity(permissionDTO);
        permission = permissionRepository.save(permission);
        return permissionMapper.toDto(permission);
    }

    /**
     * Update a permission.
     *
     * @param permissionDTO the entity to save.
     * @return the persisted entity.
     */
    public PermissionDTO update(PermissionDTO permissionDTO) {
        LOG.debug("Request to update Permission : {}", permissionDTO);
        Permission permission = permissionMapper.toEntity(permissionDTO);
        permission.setIsPersisted();
        permission = permissionRepository.save(permission);
        return permissionMapper.toDto(permission);
    }

    /**
     * Partially update a permission.
     *
     * @param permissionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PermissionDTO> partialUpdate(PermissionDTO permissionDTO) {
        LOG.debug("Request to partially update Permission : {}", permissionDTO);

        return permissionRepository
            .findById(permissionDTO.getId())
            .map(existingPermission -> {
                permissionMapper.partialUpdate(existingPermission, permissionDTO);

                return existingPermission;
            })
            .map(permissionRepository::save)
            .map(permissionMapper::toDto);
    }

    /**
     * Get all the permissions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PermissionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Permissions");
        return permissionRepository.findAll(pageable).map(permissionMapper::toDto);
    }

    /**
     * Get one permission by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PermissionDTO> findOne(Long id) {
        LOG.debug("Request to get Permission : {}", id);
        return permissionRepository.findById(id).map(permissionMapper::toDto);
    }

    /**
     * Delete the permission by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Permission : {}", id);
        permissionRepository.deleteById(id);
    }
}
