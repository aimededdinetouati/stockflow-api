package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.Customer;
import com.adeem.stockflow.domain.User;
import com.adeem.stockflow.repository.ClientAccountRepository;
import com.adeem.stockflow.repository.CustomerClientAssociationRepository;
import com.adeem.stockflow.repository.CustomerRepository;
import com.adeem.stockflow.repository.UserRepository;
import com.adeem.stockflow.repository.projection.AssociationStatsProjection;
import com.adeem.stockflow.security.AuthoritiesConstants;
import com.adeem.stockflow.security.SecurityUtils;
import com.adeem.stockflow.service.dto.AdminUserDTO;
import com.adeem.stockflow.service.dto.CreateAccountRequestDTO;
import com.adeem.stockflow.service.dto.CustomerDTO;
import com.adeem.stockflow.service.dto.CustomerStatsDTO;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import com.adeem.stockflow.service.mapper.CustomerMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Customer}.
 * Enhanced with multi-tenant security and comprehensive customer management.
 */
@Service
@Transactional
public class CustomerService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final CustomerClientAssociationRepository associationRepository;
    private final ClientAccountRepository clientAccountRepository;
    private final UserRepository userRepository;
    private final CustomerMapper customerMapper;
    private final UserService userService;
    private final MailService mailService;

    public CustomerService(
        CustomerRepository customerRepository,
        CustomerClientAssociationRepository associationRepository,
        ClientAccountRepository clientAccountRepository,
        UserRepository userRepository,
        CustomerMapper customerMapper,
        UserService userService,
        MailService mailService
    ) {
        this.customerRepository = customerRepository;
        this.associationRepository = associationRepository;
        this.clientAccountRepository = clientAccountRepository;
        this.userRepository = userRepository;
        this.customerMapper = customerMapper;
        this.userService = userService;
        this.mailService = mailService;
    }

    /**
     * Create a new customer for the current company.
     *
     * @param customerDTO the entity to save.
     * @return the persisted entity.
     */
    //@CacheEvict(value = "customerStats", key = "#root.target.getCurrentClientAccountId()")
    public CustomerDTO create(CustomerDTO customerDTO) {
        LOG.debug("Request to save Customer : {}", customerDTO);

        Long clientAccountId = getCurrentClientAccountId();

        // Validate phone uniqueness within company
        if (customerDTO.getPhone() != null && phoneExistsInCompany(customerDTO.getPhone(), clientAccountId, null)) {
            throw new BadRequestAlertException("Phone number already exists in this company", "customer", "phoneexists");
        }

        // Validate tax ID uniqueness within company
        if (customerDTO.getTaxId() != null && TaxIdExistsInCompany(customerDTO.getTaxId(), clientAccountId, null)) {
            throw new BadRequestAlertException("Tax ID already exists in this company", "customer", "taxidexists");
        }

        Customer customer = customerMapper.toEntity(customerDTO);

        // Set the creating client account
        ClientAccount clientAccount = clientAccountRepository
            .findById(clientAccountId)
            .orElseThrow(() -> new BadRequestAlertException("Client account not found", "customer", "clientaccountnotfound"));
        customer.setCreatedByClientAccount(clientAccount);

        // Ensure enabled by default
        if (customer.getEnabled() == null) {
            customer.setEnabled(true);
        }

        customer = customerRepository.save(customer);
        LOG.debug("Created Customer : {}", customer);

        return customerMapper.toDto(customer);
    }

    /**
     * Update a customer.
     *
     * @param customerDTO the entity to save.
     * @return the persisted entity.
     */
    //@CacheEvict(value = "customerStats", key = "#root.target.getCurrentClientAccountId()")
    public CustomerDTO update(CustomerDTO customerDTO) {
        LOG.debug("Request to update Customer : {}", customerDTO);

        if (customerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", "customer", "idnull");
        }

        Long clientAccountId = getCurrentClientAccountId();

        // Check if customer can be managed by current company
        if (!canManageCustomer(customerDTO.getId(), clientAccountId)) {
            throw new AccessDeniedException("Cannot manage this customer");
        }

        // Validate phone uniqueness within company (excluding current customer)
        if (customerDTO.getPhone() != null && !phoneExistsInCompany(customerDTO.getPhone(), clientAccountId, customerDTO.getId())) {
            throw new BadRequestAlertException("Phone number already exists in this company", "customer", "phoneexists");
        }

        // Validate tax ID uniqueness within company (excluding current customer)
        if (customerDTO.getTaxId() != null && !TaxIdExistsInCompany(customerDTO.getTaxId(), clientAccountId, customerDTO.getId())) {
            throw new BadRequestAlertException("Tax ID already exists in this company", "customer", "taxidexists");
        }

        Customer customer = customerMapper.toEntity(customerDTO);
        customer.setIsPersisted();
        customer = customerRepository.save(customer);

        return customerMapper.toDto(customer);
    }

    /**
     * Partially update a customer.
     *
     * @param customerDTO the entity to update partially.
     * @return the persisted entity.
     */
    //@CacheEvict(value = "customerStats", key = "#root.target.getCurrentClientAccountId()")
    public Optional<CustomerDTO> partialUpdate(CustomerDTO customerDTO) {
        LOG.debug("Request to partially update Customer : {}", customerDTO);

        Long clientAccountId = getCurrentClientAccountId();

        if (!canManageCustomer(customerDTO.getId(), clientAccountId)) {
            throw new AccessDeniedException("Cannot manage this customer");
        }

        return customerRepository
            .findByIdAndCreatedByClientAccountId(customerDTO.getId(), clientAccountId)
            .map(existingCustomer -> {
                customerMapper.partialUpdate(existingCustomer, customerDTO);
                return existingCustomer;
            })
            .map(customerRepository::save)
            .map(customerMapper::toDto);
    }

    /**
     * Get all customers for the current company.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Customers");
        Long clientAccountId = getCurrentClientAccountId();
        return customerRepository.findAllByCreatedByClientAccountIdAndEnabledTrue(clientAccountId, pageable).map(customerMapper::toDto);
    }

    /**
     * Get all customers including soft deleted for the current company.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findAllIncludingDeleted(Pageable pageable) {
        LOG.debug("Request to get all Customers including deleted");
        Long clientAccountId = getCurrentClientAccountId();
        return customerRepository
            .findAllByCreatedByClientAccountIdOrderByEnabledDescCreatedDateDesc(clientAccountId, pageable)
            .map(customerMapper::toDto);
    }

    /**
     * Get all customers with specification.
     *
     * @param specification the specification to filter by.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findAll(Specification<Customer> specification, Pageable pageable) {
        LOG.debug("Request to get all Customers with specification");
        return customerRepository.findAll(specification, pageable).map(customerMapper::toDto);
    }

    /**
     * Get one customer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CustomerDTO> findOne(Long id) {
        LOG.debug("Request to get Customer : {}", id);
        Long clientAccountId = getCurrentClientAccountId();

        if (!canViewCustomer(id, clientAccountId)) {
            return Optional.empty();
        }

        return customerRepository.findByIdAndViewableByClientAccount(id, clientAccountId).map(customerMapper::toDto);
    }

    /**
     * Soft delete the customer by id.
     *
     * @param id the id of the entity.
     */
    //@CacheEvict(value = "customerStats", key = "#root.target.getCurrentClientAccountId()")
    public void delete(Long id) {
        LOG.debug("Request to delete Customer : {}", id);

        Long clientAccountId = getCurrentClientAccountId();

        if (!canManageCustomer(id, clientAccountId)) {
            throw new AccessDeniedException("Cannot delete this customer");
        }

        Customer customer = customerRepository
            .findByIdAndCreatedByClientAccountId(id, clientAccountId)
            .orElseThrow(() -> new BadRequestAlertException("Customer not found", "customer", "idnotfound"));

        // Soft delete
        customer.setEnabled(false);
        customerRepository.save(customer);

        LOG.debug("Soft deleted Customer : {}", id);
    }

    /**
     * Reactivate a soft deleted customer.
     *
     * @param id the id of the entity.
     */
    //@CacheEvict(value = "customerStats", key = "#root.target.getCurrentClientAccountId()")
    public void reactivate(Long id) {
        LOG.debug("Request to reactivate Customer : {}", id);

        Long clientAccountId = getCurrentClientAccountId();

        Customer customer = customerRepository
            .findByIdAndCreatedByClientAccountId(id, clientAccountId)
            .orElseThrow(() -> new BadRequestAlertException("Customer not found", "customer", "idnotfound"));

        customer.setEnabled(true);
        customerRepository.save(customer);

        LOG.debug("Reactivated Customer : {}", id);
    }

    /**
     * Search customers within company scope.
     *
     * @param query the search query.
     * @param pageable the pagination information.
     * @return the search results.
     */
    @Transactional(readOnly = true)
    public Page<CustomerDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search Customers : {}", query);
        Long clientAccountId = getCurrentClientAccountId();

        if (query == null || query.trim().isEmpty()) {
            return findAll(pageable);
        }

        return customerRepository.searchByClientAccount(query.trim(), clientAccountId, pageable).map(customerMapper::toDto);
    }

    /**
     * Get customer statistics for current company.
     *
     * @return the statistics.
     */
    @Transactional(readOnly = true)
    //@Cacheable(value = "customerStats", key = "#root.target.getCurrentClientAccountId()")
    public CustomerStatsDTO getStatistics() {
        LOG.debug("Request to get Customer statistics");
        Long clientAccountId = getCurrentClientAccountId();

        Long totalCustomers = customerRepository.countByClientAccount(clientAccountId);
        Long enabledCustomers = customerRepository.countEnabledByClientAccount(clientAccountId);
        Long disabledCustomers = customerRepository.countDisabledByClientAccount(clientAccountId);
        Long managedCustomers = customerRepository.countManagedCustomersByClientAccount(clientAccountId);
        Long independentCustomers = customerRepository.countIndependentCustomersByClientAccount(clientAccountId);
        Long customersWithAccounts = customerRepository.countWithUserAccountByClientAccount(clientAccountId);
        Long customersWithoutAccounts = customerRepository.countWithoutUserAccountByClientAccount(clientAccountId);
        Long totalAssociations = associationRepository.countActiveAssociationsByClientAccount(clientAccountId);

        CustomerStatsDTO stats = new CustomerStatsDTO(
            totalCustomers,
            managedCustomers,
            independentCustomers,
            enabledCustomers,
            disabledCustomers,
            customersWithAccounts,
            customersWithoutAccounts,
            totalAssociations
        );

        // Add association type breakdown
        List<AssociationStatsProjection> associationStats = associationRepository.countAssociationsByTypeAndClientAccount(
            clientAccountId,
            com.adeem.stockflow.domain.enumeration.AssociationStatus.ACTIVE
        );

        for (AssociationStatsProjection stat : associationStats) {
            stats.addAssociationType(stat.getAssociationType(), stat.getCount());
        }

        return stats;
    }

    /**
     * Count customers for current company.
     *
     * @return the count.
     */
    @Transactional(readOnly = true)
    public Long count() {
        LOG.debug("Request to count Customers");
        Long clientAccountId = getCurrentClientAccountId();
        return customerRepository.countEnabledByClientAccount(clientAccountId);
    }

    /**
     * Check if phone is unique within company scope.
     *
     * @param phone the phone number.
     * @param clientAccountId the client account ID.
     * @param excludeCustomerId customer ID to exclude from check.
     * @return true if unique, false otherwise.
     */
    @Transactional(readOnly = true)
    public boolean phoneExistsInCompany(String phone, Long clientAccountId, Long excludeCustomerId) {
        if (excludeCustomerId != null) {
            return !customerRepository.existsByPhoneAndCreatedByClientAccountIdAndIdNot(phone, clientAccountId, excludeCustomerId);
        }
        return customerRepository.existsByPhoneAndCreatedByClientAccountId(phone, clientAccountId);
    }

    /**
     * Check if tax ID is unique within company scope.
     *
     * @param taxId the tax ID.
     * @param clientAccountId the client account ID.
     * @param excludeCustomerId customer ID to exclude from check.
     * @return true if unique, false otherwise.
     */
    @Transactional(readOnly = true)
    public boolean TaxIdExistsInCompany(String taxId, Long clientAccountId, Long excludeCustomerId) {
        if (excludeCustomerId != null) {
            return !customerRepository.existsByTaxIdAndCreatedByClientAccountIdAndIdNot(taxId, clientAccountId, excludeCustomerId);
        }
        return customerRepository.existsByTaxIdAndCreatedByClientAccountId(taxId, clientAccountId);
    }

    /**
     * Create marketplace account for customer.
     *
     * @param customerId the customer ID.
     * @param request the account creation request.
     */
    @Transactional
    public void createMarketplaceAccount(Long customerId, CreateAccountRequestDTO request) {
        LOG.debug("Request to create marketplace account for Customer : {}", customerId);

        Long clientAccountId = getCurrentClientAccountId();

        if (!canManageCustomer(customerId, clientAccountId)) {
            throw new AccessDeniedException("Cannot manage this customer");
        }

        Customer customer = customerRepository
            .findByIdAndCreatedByClientAccountId(customerId, clientAccountId)
            .orElseThrow(() -> new BadRequestAlertException("Customer not found", "customer", "idnotfound"));

        if (customer.getUser() != null) {
            throw new BadRequestAlertException("Customer already has a user account", "customer", "accountexists");
        }

        // Check if email is already used
        if (userRepository.findOneByEmailIgnoreCase(request.getEmail()).isPresent()) {
            throw new BadRequestAlertException("Email already in use", "customer", "emailexists");
        }

        // Create user account
        AdminUserDTO userDTO = new AdminUserDTO();
        userDTO.setLogin(request.getEmail());
        userDTO.setFirstName(customer.getFirstName());
        userDTO.setLastName(customer.getLastName());
        userDTO.setEmail(request.getEmail());
        userDTO.setLangKey(request.getLangKey());

        // Create user account first
        User newUser = userService.registerUser(userDTO, null, AuthoritiesConstants.USER_CUSTOMER);

        // Link customer to user
        customer.setUser(newUser);
        customerRepository.save(customer);

        // Send activation email
        if (request.getSendWelcomeEmail()) {
            mailService.sendActivationEmail(newUser);
        }

        LOG.debug("Created marketplace account for Customer : {}", customerId);
    }

    /**
     * Resend activation email for customer.
     *
     * @param customerId the customer ID.
     */
    @Transactional(readOnly = true)
    public void resendActivationEmail(Long customerId) {
        LOG.debug("Request to resend activation email for Customer : {}", customerId);

        Long clientAccountId = getCurrentClientAccountId();

        Customer customer = customerRepository
            .findByIdAndCreatedByClientAccountId(customerId, clientAccountId)
            .orElseThrow(() -> new BadRequestAlertException("Customer not found", "customer", "idnotfound"));

        if (customer.getUser() == null) {
            throw new BadRequestAlertException("Customer does not have a user account", "customer", "noaccount");
        }

        if (customer.getUser().isActivated()) {
            throw new BadRequestAlertException("Customer account is already activated", "customer", "alreadyactivated");
        }

        mailService.sendActivationEmail(customer.getUser());
        LOG.debug("Resent activation email for Customer : {}", customerId);
    }

    /**
     * Check if current user can view customer.
     *
     * @param customerId the customer ID.
     * @param clientAccountId the client account ID.
     * @return true if can view, false otherwise.
     */
    @Transactional(readOnly = true)
    public boolean canViewCustomer(Long customerId, Long clientAccountId) {
        // Can view if customer was created by current company OR has association with current company
        return customerRepository.findByIdAndViewableByClientAccount(customerId, clientAccountId).isPresent();
    }

    /**
     * Check if current user can manage customer.
     *
     * @param customerId the customer ID.
     * @param clientAccountId the client account ID.
     * @return true if can manage, false otherwise.
     */
    @Transactional(readOnly = true)
    public boolean canManageCustomer(Long customerId, Long clientAccountId) {
        // Can manage if customer was created by current company AND customer has no user account
        Optional<Customer> customer = customerRepository.findByIdAndCreatedByClientAccountId(customerId, clientAccountId);
        return customer.map(c -> c.getUser() == null).orElse(false);
    }

    /**
     * Get current client account ID from security context.
     */
    public Long getCurrentClientAccountId() {
        return SecurityUtils.getCurrentClientAccountId();
    }
}
