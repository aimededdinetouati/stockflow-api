package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.UserRole;
import com.adeem.stockflow.repository.UserRoleRepository;
import com.adeem.stockflow.service.dto.UserRoleDTO;
import com.adeem.stockflow.service.mapper.UserRoleMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.UserRole}.
 */
@Service
@Transactional
public class UserRoleService {

    private static final Logger LOG = LoggerFactory.getLogger(UserRoleService.class);

    private final UserRoleRepository userRoleRepository;

    private final UserRoleMapper userRoleMapper;

    public UserRoleService(UserRoleRepository userRoleRepository, UserRoleMapper userRoleMapper) {
        this.userRoleRepository = userRoleRepository;
        this.userRoleMapper = userRoleMapper;
    }

    /**
     * Save a userRole.
     *
     * @param userRoleDTO the entity to save.
     * @return the persisted entity.
     */
    public UserRoleDTO save(UserRoleDTO userRoleDTO) {
        LOG.debug("Request to save UserRole : {}", userRoleDTO);
        UserRole userRole = userRoleMapper.toEntity(userRoleDTO);
        userRole = userRoleRepository.save(userRole);
        return userRoleMapper.toDto(userRole);
    }

    /**
     * Update a userRole.
     *
     * @param userRoleDTO the entity to save.
     * @return the persisted entity.
     */
    public UserRoleDTO update(UserRoleDTO userRoleDTO) {
        LOG.debug("Request to update UserRole : {}", userRoleDTO);
        UserRole userRole = userRoleMapper.toEntity(userRoleDTO);
        userRole.setIsPersisted();
        userRole = userRoleRepository.save(userRole);
        return userRoleMapper.toDto(userRole);
    }

    /**
     * Partially update a userRole.
     *
     * @param userRoleDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserRoleDTO> partialUpdate(UserRoleDTO userRoleDTO) {
        LOG.debug("Request to partially update UserRole : {}", userRoleDTO);

        return userRoleRepository
            .findById(userRoleDTO.getId())
            .map(existingUserRole -> {
                userRoleMapper.partialUpdate(existingUserRole, userRoleDTO);

                return existingUserRole;
            })
            .map(userRoleRepository::save)
            .map(userRoleMapper::toDto);
    }

    /**
     * Get all the userRoles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UserRoleDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all UserRoles");
        return userRoleRepository.findAll(pageable).map(userRoleMapper::toDto);
    }

    /**
     * Get one userRole by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserRoleDTO> findOne(Long id) {
        LOG.debug("Request to get UserRole : {}", id);
        return userRoleRepository.findById(id).map(userRoleMapper::toDto);
    }

    /**
     * Delete the userRole by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete UserRole : {}", id);
        userRoleRepository.deleteById(id);
    }
}
