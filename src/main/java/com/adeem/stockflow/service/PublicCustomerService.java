package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.Customer;
import com.adeem.stockflow.domain.User;
import com.adeem.stockflow.domain.enumeration.AccountStatus;
import com.adeem.stockflow.repository.ClientAccountRepository;
import com.adeem.stockflow.repository.CustomerRepository;
import com.adeem.stockflow.repository.UserRepository;
import com.adeem.stockflow.security.AuthoritiesConstants;
import com.adeem.stockflow.service.dto.AdminUserDTO;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.dto.CustomerDTO;
import com.adeem.stockflow.service.dto.CustomerRegistrationDTO;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import com.adeem.stockflow.service.exceptions.ErrorConstants;
import com.adeem.stockflow.service.mapper.ClientAccountMapper;
import com.adeem.stockflow.service.mapper.CustomerMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing public customer operations.
 * Handles customer self-registration and public company discovery.
 */
@Service
@Transactional
public class PublicCustomerService {

    private static final Logger LOG = LoggerFactory.getLogger(PublicCustomerService.class);

    private final CustomerRepository customerRepository;
    private final ClientAccountRepository clientAccountRepository;
    private final UserRepository userRepository;
    private final CustomerMapper customerMapper;
    private final ClientAccountMapper clientAccountMapper;
    private final UserService userService;
    private final MailService mailService;

    public PublicCustomerService(
        CustomerRepository customerRepository,
        ClientAccountRepository clientAccountRepository,
        UserRepository userRepository,
        CustomerMapper customerMapper,
        ClientAccountMapper clientAccountMapper,
        UserService userService,
        MailService mailService
    ) {
        this.customerRepository = customerRepository;
        this.clientAccountRepository = clientAccountRepository;
        this.userRepository = userRepository;
        this.customerMapper = customerMapper;
        this.clientAccountMapper = clientAccountMapper;
        this.userService = userService;
        this.mailService = mailService;
    }

    /**
     * Register a new independent customer in the marketplace.
     *
     * @param registrationDTO the registration data.
     * @return the created customer.
     */
    public CustomerDTO registerCustomer(CustomerRegistrationDTO registrationDTO) {
        LOG.debug("Request to register new customer : {}", registrationDTO.getEmail());

        // Validate email is not already used
        if (userRepository.findOneByEmailIgnoreCase(registrationDTO.getEmail()).isPresent()) {
            throw new BadRequestAlertException("Email already in use", "customer", "emailexists");
        }

        // Check if independent customer with phone or email already exists
        if (customerRepository.existsIndependentCustomerByPhoneOrEmail(registrationDTO.getPhone(), registrationDTO.getEmail())) {
            throw new BadRequestAlertException("Customer with this phone or email already exists", "customer", "customerexists");
        }

        AdminUserDTO userDTO = new AdminUserDTO();
        userDTO.setLogin(registrationDTO.getEmail());
        userDTO.setFirstName(registrationDTO.getFirstName());
        userDTO.setLastName(registrationDTO.getLastName());
        userDTO.setEmail(registrationDTO.getEmail());
        userDTO.setLangKey(registrationDTO.getLangKey());

        // Create user account first
        User newUser = userService.registerUser(userDTO, registrationDTO.getPassword(), AuthoritiesConstants.USER_CUSTOMER);

        // Create customer
        Customer customer = new Customer();
        customer.setFirstName(registrationDTO.getFirstName());
        customer.setLastName(registrationDTO.getLastName());
        customer.setPhone(registrationDTO.getPhone());
        customer.setFax(registrationDTO.getFax());
        customer.setTaxId(registrationDTO.getTaxId());
        customer.setRegistrationArticle(registrationDTO.getRegistrationArticle());
        customer.setStatisticalId(registrationDTO.getStatisticalId());
        customer.setRc(registrationDTO.getRc());
        customer.setEnabled(true);
        customer.setUser(newUser);
        // No createdByClientAccount for independent customers

        customer = customerRepository.save(customer);

        // Send activation email
        mailService.sendActivationEmail(newUser);

        LOG.debug("Created independent customer : {}", customer.getId());
        return customerMapper.toDto(customer);
    }

    /**
     * Get available companies for association.
     *
     * @param pageable the pagination information.
     * @return the list of available companies.
     */
    @Transactional(readOnly = true)
    public Page<ClientAccountDTO> findAvailableCompanies(Pageable pageable) {
        LOG.debug("Request to get available companies");

        // Return all companies - in a real implementation, filter by status
        return clientAccountRepository.findAll(pageable).map(this::toPublicClientAccountDTO);
    }

    /**
     * Get available companies excluding those already associated with customer.
     *
     * @param customerId the customer ID.
     * @param pageable the pagination information.
     * @return the list of available companies.
     */
    @Transactional(readOnly = true)
    public Page<ClientAccountDTO> findAvailableCompaniesForCustomer(Long customerId, Pageable pageable) {
        LOG.debug("Request to get available companies for customer : {}", customerId);

        // Validate customer exists
        if (!customerRepository.existsById(customerId)) {
            throw new BadRequestAlertException("Customer not found", "customer", "customernotfound");
        }

        // This would require a more complex query - for now, return all active companies
        // In a real implementation, you'd exclude companies the customer is already associated with
        return findAvailableCompanies(pageable);
    }

    /**
     * Get public profile of a company.
     *
     * @param clientAccountId the client account ID.
     * @return the company profile.
     */
    @Transactional(readOnly = true)
    public Optional<ClientAccountDTO> getCompanyProfile(Long clientAccountId) {
        LOG.debug("Request to get company profile : {}", clientAccountId);

        return clientAccountRepository.findById(clientAccountId).map(this::toPublicClientAccountDTO);
    }

    /**
     * Search companies by name.
     *
     * @param query the search query.
     * @param pageable the pagination information.
     * @return the search results.
     */
    @Transactional(readOnly = true)
    public Page<ClientAccountDTO> searchCompanies(String query, Pageable pageable) {
        LOG.debug("Request to search companies : {}", query);

        if (query == null || query.trim().isEmpty()) {
            return findAvailableCompanies(pageable);
        }

        // For now, return all companies - in real implementation, add search functionality
        return findAvailableCompanies(pageable);
    }

    /**
     * Check if email is available for registration.
     *
     * @param email the email to check.
     * @return true if available, false if already used.
     */
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        LOG.debug("Request to check email availability : {}", email);
        return !userRepository.findOneByEmailIgnoreCase(email).isPresent();
    }

    /**
     * Check if phone is available for registration.
     *
     * @param phone the phone to check.
     * @return true if available, false if already used by independent customer.
     */
    @Transactional(readOnly = true)
    public boolean isPhoneAvailable(String phone) {
        LOG.debug("Request to check phone availability : {}", phone);
        // Only check independent customers (those with user accounts)
        return customerRepository.findByPhoneAndUserIsNull(phone).isEmpty();
    }

    /**
     * Validate registration data.
     *
     * @param registrationDTO the registration data.
     * @return validation result.
     */
    @Transactional(readOnly = true)
    public RegistrationValidationResult validateRegistration(CustomerRegistrationDTO registrationDTO) {
        LOG.debug("Request to validate registration data");

        RegistrationValidationResult result = new RegistrationValidationResult();

        // Check email availability
        if (!isEmailAvailable(registrationDTO.getEmail())) {
            result.addError("email", "Email is already in use");
        }

        // Check phone availability
        if (!isPhoneAvailable(registrationDTO.getPhone())) {
            result.addError("phone", "Phone number is already in use");
        }

        // Additional business validations can be added here

        return result;
    }

    /**
     * Get marketplace statistics (public info).
     *
     * @return the marketplace statistics.
     */
    @Transactional(readOnly = true)
    public MarketplaceStatsDTO getMarketplaceStatistics() {
        LOG.debug("Request to get marketplace statistics");

        Long totalCompanies = clientAccountRepository.count();
        Long totalCustomers = customerRepository.count();

        // Count customers with user accounts (independent customers)
        Long independentCustomers = customerRepository.findAll().stream().filter(c -> c.getUser() != null).mapToLong(c -> 1L).sum();

        MarketplaceStatsDTO stats = new MarketplaceStatsDTO();
        stats.setTotalCompanies(totalCompanies);
        stats.setTotalCustomers(totalCustomers);
        stats.setIndependentCustomers(independentCustomers);

        return stats;
    }

    /**
     * Find customers by phone for account linking.
     * Used internally when a company wants to link an existing independent customer.
     *
     * @param phone the phone number.
     * @return the customer if found.
     */
    @Transactional(readOnly = true)
    public CustomerDTO findCustomerByPhone(String phone) {
        LOG.debug("Request to find customer by phone : {}", phone);

        // Find customers without user accounts by phone
        return customerRepository.findByPhoneAndUserIsNull(phone).stream().findFirst().map(customerMapper::toDto).orElse(null);
    }

    /**
     * Convert ClientAccount to public DTO (hiding sensitive information).
     */
    private ClientAccountDTO toPublicClientAccountDTO(ClientAccount clientAccount) {
        ClientAccountDTO dto = clientAccountMapper.toDto(clientAccount);

        // Remove sensitive information for public view
        dto.setTaxIdentifier(null);
        dto.setRegistrationArticle(null);
        dto.setStatisticalId(null);
        dto.setCommercialRegistry(null);
        dto.setBankAccount(null);
        dto.setBankName(null);
        dto.setSocialCapital(null);

        return dto;
    }

    /**
     * Inner class for registration validation results.
     */
    public static class RegistrationValidationResult {

        private final Map<String, String> errors = new HashMap<>();

        public void addError(String field, String message) {
            errors.put(field, message);
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public Map<String, String> getErrors() {
            return errors;
        }
    }

    /**
     * Inner class for marketplace statistics.
     */
    public static class MarketplaceStatsDTO {

        private Long totalCompanies;
        private Long totalCustomers;
        private Long independentCustomers;

        // Getters and setters
        public Long getTotalCompanies() {
            return totalCompanies;
        }

        public void setTotalCompanies(Long totalCompanies) {
            this.totalCompanies = totalCompanies;
        }

        public Long getTotalCustomers() {
            return totalCustomers;
        }

        public void setTotalCustomers(Long totalCustomers) {
            this.totalCustomers = totalCustomers;
        }

        public Long getIndependentCustomers() {
            return independentCustomers;
        }

        public void setIndependentCustomers(Long independentCustomers) {
            this.independentCustomers = independentCustomers;
        }
    }
}
