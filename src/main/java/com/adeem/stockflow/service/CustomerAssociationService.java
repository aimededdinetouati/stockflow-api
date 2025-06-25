package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.Customer;
import com.adeem.stockflow.domain.CustomerClientAssociation;
import com.adeem.stockflow.domain.enumeration.AssociationStatus;
import com.adeem.stockflow.repository.ClientAccountRepository;
import com.adeem.stockflow.repository.CustomerClientAssociationRepository;
import com.adeem.stockflow.repository.CustomerRepository;
import com.adeem.stockflow.repository.projection.AssociationStatsProjection;
import com.adeem.stockflow.security.SecurityUtils;
import com.adeem.stockflow.service.dto.CustomerAssociationDTO;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import com.adeem.stockflow.service.mapper.CustomerAssociationMapper;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link CustomerClientAssociation}.
 * Handles customer-company association relationships.
 */
@Service
@Transactional
public class CustomerAssociationService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerAssociationService.class);

    private final CustomerClientAssociationRepository associationRepository;
    private final CustomerRepository customerRepository;
    private final ClientAccountRepository clientAccountRepository;
    private final CustomerAssociationMapper associationMapper;

    public CustomerAssociationService(
        CustomerClientAssociationRepository associationRepository,
        CustomerRepository customerRepository,
        ClientAccountRepository clientAccountRepository,
        CustomerAssociationMapper associationMapper
    ) {
        this.associationRepository = associationRepository;
        this.customerRepository = customerRepository;
        this.clientAccountRepository = clientAccountRepository;
        this.associationMapper = associationMapper;
    }

    /**
     * Create a new customer-company association.
     *
     * @param associationDTO the entity to save.
     * @return the persisted entity.
     */
    public CustomerAssociationDTO create(CustomerAssociationDTO associationDTO) {
        LOG.debug("Request to save CustomerAssociation : {}", associationDTO);

        // Validate that customer and client account exist
        Customer customer = customerRepository
            .findById(associationDTO.getCustomerId())
            .orElseThrow(() -> new BadRequestAlertException("Customer not found", "customerAssociation", "customernotfound"));

        ClientAccount clientAccount = clientAccountRepository
            .findById(associationDTO.getClientAccountId())
            .orElseThrow(() -> new BadRequestAlertException("Client account not found", "customerAssociation", "clientaccountnotfound"));

        // Check if association already exists
        if (
            associationRepository.existsByCustomerIdAndClientAccountIdAndAssociationType(
                associationDTO.getCustomerId(),
                associationDTO.getClientAccountId(),
                associationDTO.getAssociationType()
            )
        ) {
            throw new BadRequestAlertException("Association already exists", "customerAssociation", "associationexists");
        }

        // Security check: either customer owns this association or company admin is creating it
        validateAssociationAccess(associationDTO.getCustomerId(), associationDTO.getClientAccountId());

        CustomerClientAssociation association = associationMapper.toEntity(associationDTO);
        association.setCustomer(customer);
        association.setClientAccount(clientAccount);

        // Set default status if not provided
        if (association.getStatus() == null) {
            association.setStatus(AssociationStatus.ACTIVE);
        }

        association = associationRepository.save(association);
        LOG.debug("Created CustomerAssociation : {}", association);

        return associationMapper.toDto(association);
    }

    /**
     * Update a customer-company association.
     *
     * @param associationDTO the entity to save.
     * @return the persisted entity.
     */
    public CustomerAssociationDTO update(CustomerAssociationDTO associationDTO) {
        LOG.debug("Request to update CustomerAssociation : {}", associationDTO);

        if (associationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", "customerAssociation", "idnull");
        }

        CustomerClientAssociation existingAssociation = associationRepository
            .findById(associationDTO.getId())
            .orElseThrow(() -> new BadRequestAlertException("Association not found", "customerAssociation", "idnotfound"));

        // Security check
        validateAssociationAccess(existingAssociation.getCustomer().getId(), existingAssociation.getClientAccount().getId());

        CustomerClientAssociation association = associationMapper.toEntity(associationDTO);
        association.setIsPersisted();
        association = associationRepository.save(association);

        return associationMapper.toDto(association);
    }

    /**
     * Partially update a customer-company association.
     *
     * @param associationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CustomerAssociationDTO> partialUpdate(CustomerAssociationDTO associationDTO) {
        LOG.debug("Request to partially update CustomerAssociation : {}", associationDTO);

        return associationRepository
            .findById(associationDTO.getId())
            .map(existingAssociation -> {
                // Security check
                validateAssociationAccess(existingAssociation.getCustomer().getId(), existingAssociation.getClientAccount().getId());

                associationMapper.partialUpdate(existingAssociation, associationDTO);
                return existingAssociation;
            })
            .map(associationRepository::save)
            .map(associationMapper::toDto);
    }

    /**
     * Get all associations for the current context (customer or company).
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CustomerAssociationDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all CustomerAssociations");

        return SecurityUtils.getCurrentOptUserId()
            .flatMap(customerRepository::findByUserId)
            .map(customer -> findAllByCustomerId(customer.getId(), pageable))
            .orElseGet(() -> {
                Long clientAccountId = SecurityUtils.getCurrentClientAccountId();
                return findAllByClientAccountId(clientAccountId, pageable);
            });
    }

    /**
     * Get all associations for a specific customer.
     *
     * @param customerId the customer ID.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CustomerAssociationDTO> findAllByCustomerId(Long customerId, Pageable pageable) {
        LOG.debug("Request to get all CustomerAssociations for customer: {}", customerId);

        // Security check: only customer themselves or companies they are associated with can view
        validateCustomerAssociationAccess(customerId);

        return associationRepository.findAllByCustomerId(customerId, pageable).map(associationMapper::toDto);
    }

    /**
     * Get all associations for a specific client account.
     *
     * @param clientAccountId the client account ID.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CustomerAssociationDTO> findAllByClientAccountId(Long clientAccountId, Pageable pageable) {
        LOG.debug("Request to get all CustomerAssociations for client account: {}", clientAccountId);

        // Security check: only the company itself can view its associations
        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();
        if (!currentClientAccountId.equals(clientAccountId)) {
            throw new AccessDeniedException("Cannot view associations for other companies");
        }

        return associationRepository.findAllByClientAccountId(clientAccountId, pageable).map(associationMapper::toDto);
    }

    /**
     * Get association between customer and company.
     *
     * @param customerId the customer ID.
     * @param clientAccountId the client account ID.
     * @return the association if exists.
     */
    @Transactional(readOnly = true)
    public Optional<CustomerAssociationDTO> findByCustomerAndClientAccount(Long customerId, Long clientAccountId) {
        LOG.debug("Request to get CustomerAssociation for customer: {} and client account: {}", customerId, clientAccountId);

        validateAssociationAccess(customerId, clientAccountId);

        return associationRepository.findByCustomerIdAndClientAccountId(customerId, clientAccountId).map(associationMapper::toDto);
    }

    /**
     * Get one association by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CustomerAssociationDTO> findOne(Long id) {
        LOG.debug("Request to get CustomerAssociation : {}", id);

        return associationRepository
            .findById(id)
            .map(association -> {
                validateAssociationAccess(association.getCustomer().getId(), association.getClientAccount().getId());
                return associationMapper.toDto(association);
            });
    }

    /**
     * Delete the association by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CustomerAssociation : {}", id);

        CustomerClientAssociation association = associationRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Association not found", "customerAssociation", "idnotfound"));

        validateAssociationAccess(association.getCustomer().getId(), association.getClientAccount().getId());

        associationRepository.delete(association);
        LOG.debug("Deleted CustomerAssociation : {}", id);
    }

    /**
     * Search associations by customer name for current company.
     *
     * @param query the search query.
     * @param pageable the pagination information.
     * @return the search results.
     */
    @Transactional(readOnly = true)
    public Page<CustomerAssociationDTO> searchByCustomerName(String query, Pageable pageable) {
        LOG.debug("Request to search CustomerAssociations by customer name: {}", query);

        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        if (query == null || query.trim().isEmpty()) {
            return findAllByClientAccountId(clientAccountId, pageable);
        }

        return associationRepository
            .searchByCustomerNameAndClientAccount(query.trim(), clientAccountId, pageable)
            .map(associationMapper::toDto);
    }

    /**
     * Search associations by company name for current customer.
     *
     * @param query the search query.
     * @param pageable the pagination information.
     * @return the search results.
     */
    @Transactional(readOnly = true)
    public Page<CustomerAssociationDTO> searchByCompanyName(String query, Pageable pageable) {
        LOG.debug("Request to search CustomerAssociations by company name: {}", query);

        Long customerId = getCurrentCustomerId();

        if (query == null || query.trim().isEmpty()) {
            return findAllByCustomerId(customerId, pageable);
        }

        return associationRepository.searchByCompanyNameAndCustomer(query.trim(), customerId, pageable).map(associationMapper::toDto);
    }

    /**
     * Get association statistics for current company.
     *
     * @return the statistics map.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAssociationStatistics() {
        LOG.debug("Request to get association statistics");

        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        Map<String, Object> stats = new HashMap<>();

        // Total counts
        Long totalAssociations = associationRepository.countAllAssociationsByClientAccount(clientAccountId);
        Long activeAssociations = associationRepository.countActiveAssociationsByClientAccount(clientAccountId);

        stats.put("totalAssociations", totalAssociations);
        stats.put("activeAssociations", activeAssociations);
        stats.put("inactiveAssociations", totalAssociations - activeAssociations);

        // By type breakdown
        List<AssociationStatsProjection> typeStats = associationRepository.countAssociationsByTypeAndClientAccount(
            clientAccountId,
            AssociationStatus.ACTIVE
        );

        Map<String, Long> byType = new HashMap<>();
        for (AssociationStatsProjection stat : typeStats) {
            byType.put(stat.getAssociationType().name(), stat.getCount());
        }
        stats.put("byType", byType);

        // By status breakdown
        List<Object[]> statusStats = associationRepository.countAssociationsByStatusAndClientAccount(clientAccountId);
        Map<String, Long> byStatus = new HashMap<>();
        for (Object[] stat : statusStats) {
            AssociationStatus status = (AssociationStatus) stat[0];
            Long count = (Long) stat[1];
            byStatus.put(status.name(), count);
        }
        stats.put("byStatus", byStatus);

        return stats;
    }

    /**
     * Get recent associations for current company.
     *
     * @param pageable the pagination information.
     * @return the recent associations.
     */
    @Transactional(readOnly = true)
    public Page<CustomerAssociationDTO> getRecentAssociations(Pageable pageable) {
        LOG.debug("Request to get recent associations");

        Long clientAccountId = SecurityUtils.getCurrentClientAccountId();

        return associationRepository.findRecentAssociationsByClientAccount(clientAccountId, pageable).map(associationMapper::toDto);
    }

    /**
     * Toggle association status between ACTIVE and INACTIVE.
     *
     * @param id the association ID.
     * @return the updated association.
     */
    public CustomerAssociationDTO toggleStatus(Long id) {
        LOG.debug("Request to toggle status of CustomerAssociation : {}", id);

        CustomerClientAssociation association = associationRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Association not found", "customerAssociation", "idnotfound"));

        validateAssociationAccess(association.getCustomer().getId(), association.getClientAccount().getId());

        // Toggle status
        if (AssociationStatus.ACTIVE.equals(association.getStatus())) {
            association.setStatus(AssociationStatus.INACTIVE);
        } else {
            association.setStatus(AssociationStatus.ACTIVE);
        }

        association = associationRepository.save(association);
        LOG.debug("Toggled status of CustomerAssociation : {}", id);

        return associationMapper.toDto(association);
    }

    /**
     * Validate access to association operations.
     */
    private void validateAssociationAccess(Long customerId, Long clientAccountId) {
        Long currentUserId = SecurityUtils.getCurrentUserId(); // throws if not authenticated
        Optional<Long> currentClientAccountId = SecurityUtils.getCurrentOptClientAccountId();

        boolean hasAccess = customerRepository
            .findByUserId(currentUserId)
            .map(customer -> Objects.equals(customer.getId(), customerId))
            .orElse(false);

        hasAccess = hasAccess || currentClientAccountId.map(id -> Objects.equals(id, clientAccountId)).orElse(false);

        if (!hasAccess) {
            throw new AccessDeniedException("Cannot access this association");
        }
    }

    /**
     * Validate access to customer association operations.
     */
    private void validateCustomerAssociationAccess(Long customerId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Optional<Long> currentClientAccountId = SecurityUtils.getCurrentOptClientAccountId();

        boolean isCustomerSelf = customerRepository
            .findByUserId(currentUserId)
            .map(customer -> Objects.equals(customer.getId(), customerId))
            .orElse(false);

        boolean isAdminWithAssociation = currentClientAccountId
            .map(clientAccountId ->
                associationRepository.existsByCustomerIdAndClientAccountIdAndStatus(customerId, clientAccountId, AssociationStatus.ACTIVE)
            )
            .orElse(false);

        if (!isCustomerSelf && !isAdminWithAssociation) {
            throw new AccessDeniedException("Cannot access associations for this customer");
        }
    }

    /**
     * Get current customer ID from security context.
     */
    private Long getCurrentCustomerId() {
        Long userId = SecurityUtils.getCurrentUserId();
        return customerRepository
            .findByUserId(userId)
            .map(Customer::getId)
            .orElseThrow(() -> new BadRequestAlertException("Current user is not a customer", "customerAssociation", "notcustomer"));
    }
}
