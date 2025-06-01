package com.adeem.stockflow.service;

import com.adeem.stockflow.config.Constants;
import com.adeem.stockflow.domain.Address;
import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.Supplier;
import com.adeem.stockflow.domain.enumeration.AddressType;
import com.adeem.stockflow.repository.AddressRepository;
import com.adeem.stockflow.repository.ClientAccountRepository;
import com.adeem.stockflow.repository.SupplierRepository;
import com.adeem.stockflow.repository.projection.SupplierActivityProjection;
import com.adeem.stockflow.repository.projection.SupplierStatsProjection;
import com.adeem.stockflow.repository.projection.TopSuppliersProjection;
import com.adeem.stockflow.service.dto.*;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import com.adeem.stockflow.service.exceptions.ErrorConstants;
import com.adeem.stockflow.service.mapper.AddressMapper;
import com.adeem.stockflow.service.mapper.SupplierMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Supplier}.
 * Enhanced with multi-tenant security and comprehensive supplier management.
 */
@Service
@Transactional
public class SupplierService {

    private static final Logger LOG = LoggerFactory.getLogger(SupplierService.class);

    private final SupplierRepository supplierRepository;
    private final ClientAccountRepository clientAccountRepository;
    private final SupplierMapper supplierMapper;
    private final AddressMapper addressMapper;
    private final AddressRepository addressRepository;

    public SupplierService(
        SupplierRepository supplierRepository,
        ClientAccountRepository clientAccountRepository,
        SupplierMapper supplierMapper,
        AddressMapper addressMapper,
        AddressRepository addressRepository
    ) {
        this.supplierRepository = supplierRepository;
        this.clientAccountRepository = clientAccountRepository;
        this.supplierMapper = supplierMapper;
        this.addressMapper = addressMapper;
        this.addressRepository = addressRepository;
    }

    /**
     * Create a new supplier with optional address.
     *
     * @param supplierDTO the supplier data
     * @param addressDTO the optional address data
     * @return the persisted supplier
     */
    @CacheEvict(value = "supplierStats", key = "#supplierDTO.clientAccountId")
    public SupplierDTO create(SupplierDTO supplierDTO, AddressDTO addressDTO) {
        LOG.debug("Request to create Supplier : {}", supplierDTO);

        validateSupplierData(supplierDTO, null);

        // Set default values
        if (supplierDTO.getActive() == null) {
            supplierDTO.setActive(true);
        }

        // Handle address if provided
        if (addressDTO != null) {
            validateAndPrepareAddress(addressDTO);
            supplierDTO.setAddress(addressDTO);
        }

        SupplierDTO savedSupplier = save(supplierDTO);

        LOG.info("Created supplier {} for client account {}", savedSupplier.getDisplayName(), savedSupplier.getClientAccountId());
        return savedSupplier;
    }

    /**
     * Update an existing supplier with optional address.
     *
     * @param supplierDTO the supplier data
     * @param addressDTO the optional address data
     * @return the updated supplier
     */
    @CacheEvict(value = "supplierStats", key = "#supplierDTO.clientAccountId")
    public SupplierDTO update(SupplierDTO supplierDTO, AddressDTO addressDTO) {
        LOG.debug("Request to update Supplier : {}", supplierDTO);

        validateSupplierData(supplierDTO, supplierDTO.getId());

        // Get existing supplier to preserve relationships
        Supplier existingSupplier = findEntityForClientAccount(supplierDTO.getId(), supplierDTO.getClientAccountId()).orElseThrow(() ->
            new AccessDeniedException(Constants.NOT_ALLOWED)
        );

        // Handle address update
        Address address = null;
        if (addressDTO != null && addressDTO.getId() == null) {
            validateAndPrepareAddress(addressDTO);
            address = addressMapper.toEntity(addressDTO);
        }

        //TODO think about deleting the existing address if new address is provided

        updateSupplierFields(existingSupplier, supplierDTO, address);

        existingSupplier.setIsPersisted();
        Supplier updatedSupplier = supplierRepository.save(existingSupplier);
        return supplierMapper.toDto(updatedSupplier);
    }

    private void updateSupplierFields(Supplier existingSupplier, SupplierDTO supplierDTO, Address address) {
        existingSupplier.setFirstName(supplierDTO.getFirstName());
        existingSupplier.setLastName(supplierDTO.getLastName());
        existingSupplier.setCompanyName(supplierDTO.getCompanyName());
        existingSupplier.setPhone(supplierDTO.getPhone());
        existingSupplier.setEmail(supplierDTO.getEmail());
        existingSupplier.setFax(supplierDTO.getFax());
        existingSupplier.setTaxId(supplierDTO.getTaxId());
        existingSupplier.setRegistrationArticle(supplierDTO.getRegistrationArticle());
        existingSupplier.setStatisticalId(supplierDTO.getStatisticalId());
        existingSupplier.setRc(supplierDTO.getRc());
        existingSupplier.setActive(supplierDTO.getActive());
        existingSupplier.setNotes(supplierDTO.getNotes());
        existingSupplier.setAddress(address);
    }

    /**
     * Save a supplier.
     *
     * @param supplierDTO the entity to save
     * @return the persisted entity
     */
    public SupplierDTO save(SupplierDTO supplierDTO) {
        LOG.debug("Request to save Supplier : {}", supplierDTO);

        Supplier supplier = supplierMapper.toEntity(supplierDTO);

        // Set the address type if address is provided
        if (supplier.getAddress() != null) {
            supplier.getAddress().setAddressType(AddressType.BUSINESS);
        }

        supplier = supplierRepository.save(supplier);
        return supplierMapper.toDto(supplier);
    }

    /**
     * Get all suppliers with specification and pagination.
     *
     * @param specification the filtering specification
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<SupplierDTO> findAll(Specification<Supplier> specification, Pageable pageable) {
        LOG.debug("Request to get all Suppliers with specification");
        return supplierRepository.findAll(specification, pageable).map(supplierMapper::toDto);
    }

    /**
     * Count suppliers matching the given specification.
     *
     * @param specification the specification to match
     * @return the count of matching suppliers
     */
    @Transactional(readOnly = true)
    public long countByCriteria(Specification<Supplier> specification) {
        LOG.debug("Request to count Suppliers by criteria");
        return supplierRepository.count(specification);
    }

    /**
     * Get one supplier by id for a specific client account.
     *
     * @param id the id of the entity
     * @param clientAccountId the client account id
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<SupplierDTO> findOneForClientAccount(Long id, Long clientAccountId) {
        LOG.debug("Request to get Supplier : {} for client account : {}", id, clientAccountId);
        return supplierRepository.findByIdAndClientAccountId(id, clientAccountId).map(supplierMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<Supplier> findEntityForClientAccount(Long id, Long clientAccountId) {
        LOG.debug("Request to get Supplier entity : {} for client account : {}", id, clientAccountId);
        return supplierRepository.findByIdAndClientAccountId(id, clientAccountId);
    }

    /**
     * Search suppliers by query string.
     *
     * @param query the search query
     * @param clientAccountId the client account ID
     * @param pageable the pagination information
     * @return the list of matching suppliers
     */
    @Transactional(readOnly = true)
    public Page<SupplierDTO> searchSuppliers(String query, Long clientAccountId, Pageable pageable) {
        LOG.debug("Request to search Suppliers with query : {} for client account : {}", query, clientAccountId);

        if (query == null || query.trim().isEmpty()) {
            // Return empty page for empty query
            return Page.empty(pageable);
        }

        return supplierRepository.searchSuppliers(query.trim(), clientAccountId, pageable).map(supplierMapper::toDto);
    }

    /**
     * Soft delete the supplier by id.
     *
     * @param id the id of the entity
     * @param clientAccountId the client account ID
     */
    @CacheEvict(value = "supplierStats", key = "#clientAccountId")
    public void softDelete(Long id, Long clientAccountId) {
        LOG.debug("Request to soft delete Supplier : {} for client account : {}", id, clientAccountId);

        // Check if supplier has active purchase orders
        if (supplierRepository.hasActivePurchaseOrders(id)) {
            throw new BadRequestAlertException(
                "Cannot delete supplier with active purchase orders. Please complete or cancel all purchase orders first.",
                "supplier",
                ErrorConstants.SUPPLIER_HAS_ACTIVE_ORDERS
            );
        }

        int updatedRows = supplierRepository.softDeleteSupplier(id, clientAccountId);
        if (updatedRows == 0) {
            throw new BadRequestAlertException("Supplier not found or access denied", "supplier", ErrorConstants.NOT_FOUND);
        }

        LOG.info("Soft deleted supplier {} for client account {}", id, clientAccountId);
    }

    /**
     * Reactivate a soft-deleted supplier.
     *
     * @param id the id of the entity
     * @param clientAccountId the client account ID
     */
    @CacheEvict(value = "supplierStats", key = "#clientAccountId")
    public void reactivate(Long id, Long clientAccountId) {
        LOG.debug("Request to reactivate Supplier : {} for client account : {}", id, clientAccountId);

        int updatedRows = supplierRepository.reactivateSupplier(id, clientAccountId);
        if (updatedRows == 0) {
            throw new BadRequestAlertException("Supplier not found or access denied", "supplier", ErrorConstants.NOT_FOUND);
        }

        LOG.info("Reactivated supplier {} for client account {}", id, clientAccountId);
    }

    /**
     * Get comprehensive supplier statistics.
     * OPTIMIZED VERSION - Uses only 3 efficient queries instead of 14+ separate calls.
     *
     * @param clientAccountId the client account ID
     * @return the supplier statistics
     */
    @Cacheable(value = "supplierStats", key = "#clientAccountId")
    @Transactional(readOnly = true)
    public SupplierStatsDTO getSupplierStatistics(Long clientAccountId) {
        LOG.debug("Request to get Supplier statistics for client account : {}", clientAccountId);

        SupplierStatsDTO stats = new SupplierStatsDTO();

        // Query 1: Get comprehensive supplier overview statistics in one query
        Optional<SupplierStatsProjection> projectionOpt = supplierRepository.getComprehensiveSupplierStats(clientAccountId);
        if (projectionOpt.isPresent()) {
            SupplierStatsProjection projection = projectionOpt.get();

            // Set all basic statistics from single query
            stats.setTotalSuppliers(projection.getTotalSuppliers());
            stats.setActiveSuppliers(projection.getActiveSuppliers());
            stats.setInactiveSuppliers(projection.getInactiveSuppliers());
            stats.setSuppliersWithAddresses(projection.getSuppliersWithAddresses());
            stats.setSuppliersWithoutAddresses(projection.getSuppliersWithoutAddresses());
            stats.setSuppliersAddedThisWeek(projection.getSuppliersAddedThisWeek());
            stats.setSuppliersAddedThisMonth(projection.getSuppliersAddedThisMonth());
            stats.setSuppliersWithPurchaseOrders(projection.getSuppliersWithPurchaseOrders());
            stats.setTotalPurchaseOrderValue(projection.getTotalPurchaseOrderValue());
            stats.setLastSupplierCreated(projection.getLastSupplierCreated());
            stats.setLastSupplierModified(projection.getLastSupplierModified());
        } else {
            // Set default values if no data found
            stats.setTotalSuppliers(0L);
            stats.setActiveSuppliers(0L);
            stats.setInactiveSuppliers(0L);
            stats.setSuppliersWithAddresses(0L);
            stats.setSuppliersWithoutAddresses(0L);
            stats.setSuppliersAddedThisWeek(0L);
            stats.setSuppliersAddedThisMonth(0L);
            stats.setSuppliersWithPurchaseOrders(0L);
            stats.setTotalPurchaseOrderValue(BigDecimal.ZERO);
        }

        // Query 2: Get top suppliers data for both rankings in one query
        List<TopSuppliersProjection> topSuppliersProjections = supplierRepository.getTopSuppliersStats(clientAccountId, 5);

        // Convert to DTOs and sort by order count (descending) for top by purchase orders
        List<SupplierStatsDTO.TopSupplierDTO> topSuppliersByOrders = topSuppliersProjections
            .stream()
            .sorted((a, b) -> b.getOrderCount().compareTo(a.getOrderCount()))
            .limit(5)
            .map(proj ->
                new SupplierStatsDTO.TopSupplierDTO(proj.getSupplierId(), proj.getDisplayName(), proj.getOrderCount(), proj.getTotalValue())
            )
            .collect(Collectors.toList());
        stats.setTopSuppliersByPurchaseOrders(topSuppliersByOrders);

        // Convert to DTOs and sort by value (descending) for top by value
        List<SupplierStatsDTO.TopSupplierDTO> topSuppliersByValue = topSuppliersProjections
            .stream()
            .sorted((a, b) -> b.getTotalValue().compareTo(a.getTotalValue()))
            .limit(5)
            .map(proj ->
                new SupplierStatsDTO.TopSupplierDTO(proj.getSupplierId(), proj.getDisplayName(), proj.getOrderCount(), proj.getTotalValue())
            )
            .collect(Collectors.toList());
        stats.setTopSuppliersByValue(topSuppliersByValue);

        // Query 3: Get recent activities efficiently
        List<SupplierActivityProjection> recentActivitiesProjections = supplierRepository.getRecentActivitiesOptimized(clientAccountId, 10);

        List<SupplierStatsDTO.SupplierActivityDTO> recentActivities = recentActivitiesProjections
            .stream()
            .map(proj ->
                new SupplierStatsDTO.SupplierActivityDTO(proj.getAction(), proj.getDisplayName(), proj.getActivityDate(), proj.getNotes())
            )
            .collect(Collectors.toList());
        stats.setRecentActivities(recentActivities);

        LOG.debug("Successfully retrieved supplier statistics for client account: {}", clientAccountId);
        return stats;
    }

    /**
     * Check if supplier exists by email.
     *
     * @param email the email to check
     * @param clientAccountId the client account ID
     * @return true if exists
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email, Long clientAccountId) {
        return supplierRepository.existsByEmailAndClientAccountId(email, clientAccountId);
    }

    /**
     * Check if supplier exists by phone.
     *
     * @param phone the phone to check
     * @param clientAccountId the client account ID
     * @return true if exists
     */
    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone, Long clientAccountId) {
        return supplierRepository.existsByPhoneAndClientAccountId(phone, clientAccountId);
    }

    /**
     * Check if supplier exists by taxId.
     *
     * @param taxId the taxId to check
     * @param clientAccountId the client account ID
     * @return true if exists
     */
    @Transactional(readOnly = true)
    public boolean existsByTaxId(String taxId, Long clientAccountId) {
        return supplierRepository.existsByTaxIdAndClientAccountId(taxId, clientAccountId);
    }

    // Private helper methods

    /**
     * Validate supplier data.
     */
    private void validateSupplierData(SupplierDTO supplierDTO, Long excludeId) {
        Long clientAccountId = supplierDTO.getClientAccountId();

        // Validate client account exists
        if (!clientAccountRepository.existsById(clientAccountId)) {
            throw new AccessDeniedException(Constants.NOT_ALLOWED);
        }

        // Validate email uniqueness
        if (supplierDTO.getEmail() != null && !supplierDTO.getEmail().trim().isEmpty()) {
            Optional<Supplier> existingByEmail = supplierRepository.findByEmailAndClientAccountId(supplierDTO.getEmail(), clientAccountId);
            if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(excludeId)) {
                throw new BadRequestAlertException("Email already exists", "supplier", ErrorConstants.EMAIL_ALREADY_EXISTS);
            }
        }

        // Validate required fields
        if (supplierDTO.getFirstName() == null || supplierDTO.getFirstName().trim().isEmpty()) {
            throw new BadRequestAlertException("First name is required", "supplier", ErrorConstants.FIRST_NAME_REQUIRED);
        }

        if (supplierDTO.getLastName() == null || supplierDTO.getLastName().trim().isEmpty()) {
            throw new BadRequestAlertException("Last name is required", "supplier", ErrorConstants.LAST_NAME_REQUIRED);
        }

        if (supplierDTO.getPhone() == null || supplierDTO.getPhone().trim().isEmpty()) {
            throw new BadRequestAlertException("Phone is required", "supplier", ErrorConstants.PHONE_REQUIRED);
        }
    }

    /**
     * Validate and prepare address data.
     */
    private void validateAndPrepareAddress(AddressDTO addressDTO) {
        if (addressDTO.getAddressType() == null) {
            addressDTO.setAddressType(AddressType.BUSINESS);
        }

        if (addressDTO.getIsDefault() == null) {
            addressDTO.setIsDefault(true);
        }

        // Validate required address fields
        if (addressDTO.getStreetAddress() == null || addressDTO.getStreetAddress().trim().isEmpty()) {
            throw new BadRequestAlertException("Street address is required", "address", ErrorConstants.STREET_ADDRESS_REQUIRED);
        }

        if (addressDTO.getCity() == null || addressDTO.getCity().trim().isEmpty()) {
            throw new BadRequestAlertException("City is required", "address", ErrorConstants.CITY_REQUIRED);
        }

        if (addressDTO.getCountry() == null || addressDTO.getCountry().trim().isEmpty()) {
            throw new BadRequestAlertException("Country is required", "address", ErrorConstants.COUNTRY_REQUIRED);
        }
    }
}
