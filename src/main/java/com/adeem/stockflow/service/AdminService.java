package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.Admin;
import com.adeem.stockflow.repository.AdminRepository;
import com.adeem.stockflow.service.dto.AdminDTO;
import com.adeem.stockflow.service.mapper.AdminMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.Admin}.
 */
@Service
@Transactional
public class AdminService {

    private static final Logger LOG = LoggerFactory.getLogger(AdminService.class);

    private final AdminRepository adminRepository;

    private final AdminMapper adminMapper;

    public AdminService(AdminRepository adminRepository, AdminMapper adminMapper) {
        this.adminRepository = adminRepository;
        this.adminMapper = adminMapper;
    }

    /**
     * Save a admin.
     *
     * @param adminDTO the entity to save.
     * @return the persisted entity.
     */
    public AdminDTO save(AdminDTO adminDTO) {
        LOG.debug("Request to save Admin : {}", adminDTO);
        Admin admin = adminMapper.toEntity(adminDTO);
        admin = adminRepository.save(admin);
        return adminMapper.toDto(admin);
    }

    /**
     * Update a admin.
     *
     * @param adminDTO the entity to save.
     * @return the persisted entity.
     */
    public AdminDTO update(AdminDTO adminDTO) {
        LOG.debug("Request to update Admin : {}", adminDTO);
        Admin admin = adminMapper.toEntity(adminDTO);
        admin.setIsPersisted();
        admin = adminRepository.save(admin);
        return adminMapper.toDto(admin);
    }

    /**
     * Partially update a admin.
     *
     * @param adminDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AdminDTO> partialUpdate(AdminDTO adminDTO) {
        LOG.debug("Request to partially update Admin : {}", adminDTO);

        return adminRepository
            .findById(adminDTO.getId())
            .map(existingAdmin -> {
                adminMapper.partialUpdate(existingAdmin, adminDTO);

                return existingAdmin;
            })
            .map(adminRepository::save)
            .map(adminMapper::toDto);
    }

    /**
     * Get all the admins.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AdminDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Admins");
        return adminRepository.findAll(pageable).map(adminMapper::toDto);
    }

    /**
     * Get one admin by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AdminDTO> findOne(Long id) {
        LOG.debug("Request to get Admin : {}", id);
        return adminRepository.findById(id).map(adminMapper::toDto);
    }

    /**
     * Delete the admin by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Admin : {}", id);
        adminRepository.deleteById(id);
    }
}
