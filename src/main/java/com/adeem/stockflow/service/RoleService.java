package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.Role;
import com.adeem.stockflow.repository.RoleRepository;
import com.adeem.stockflow.service.dto.RoleDTO;
import com.adeem.stockflow.service.mapper.RoleMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.Role}.
 */
@Service
@Transactional
public class RoleService {

    private static final Logger LOG = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    /**
     * Save a role.
     *
     * @param roleDTO the entity to save.
     * @return the persisted entity.
     */
    public RoleDTO save(RoleDTO roleDTO) {
        LOG.debug("Request to save Role : {}", roleDTO);
        Role role = roleMapper.toEntity(roleDTO);
        role = roleRepository.save(role);
        return roleMapper.toDto(role);
    }

    /**
     * Update a role.
     *
     * @param roleDTO the entity to save.
     * @return the persisted entity.
     */
    public RoleDTO update(RoleDTO roleDTO) {
        LOG.debug("Request to update Role : {}", roleDTO);
        Role role = roleMapper.toEntity(roleDTO);
        role.setIsPersisted();
        role = roleRepository.save(role);
        return roleMapper.toDto(role);
    }

    /**
     * Partially update a role.
     *
     * @param roleDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RoleDTO> partialUpdate(RoleDTO roleDTO) {
        LOG.debug("Request to partially update Role : {}", roleDTO);

        return roleRepository
            .findById(roleDTO.getId())
            .map(existingRole -> {
                roleMapper.partialUpdate(existingRole, roleDTO);

                return existingRole;
            })
            .map(roleRepository::save)
            .map(roleMapper::toDto);
    }

    /**
     * Get all the roles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RoleDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Roles");
        return roleRepository.findAll(pageable).map(roleMapper::toDto);
    }

    /**
     * Get one role by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RoleDTO> findOne(Long id) {
        LOG.debug("Request to get Role : {}", id);
        return roleRepository.findById(id).map(roleMapper::toDto);
    }

    /**
     * Delete the role by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Role : {}", id);
        roleRepository.deleteById(id);
    }
}
