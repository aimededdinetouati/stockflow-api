package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.domain.Customer;
import com.adeem.stockflow.repository.CustomerRepository;
import com.adeem.stockflow.security.AuthoritiesConstants;
import com.adeem.stockflow.security.SecurityUtils;
import com.adeem.stockflow.service.CustomerService;
import com.adeem.stockflow.service.criteria.CustomerSpecification;
import com.adeem.stockflow.service.criteria.filter.CustomerCriteria;
import com.adeem.stockflow.service.dto.CreateAccountRequestDTO;
import com.adeem.stockflow.service.dto.CustomerDTO;
import com.adeem.stockflow.service.dto.CustomerStatsDTO;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.adeem.stockflow.domain.Customer}.
 * Enhanced with multi-tenant security and comprehensive customer management.
 */
@RestController
@RequestMapping("/api/customers")
@PreAuthorize("hasAuthority('" + AuthoritiesConstants.USER_ADMIN + "')")
public class CustomerResource {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerResource.class);

    private static final String ENTITY_NAME = "customer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    public CustomerResource(CustomerService customerService, CustomerRepository customerRepository) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
    }

    /**
     * {@code POST  /customers} : Create a new customer for the current company.
     *
     * @param customerDTO the customerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customerDTO, or with status {@code 400 (Bad Request)} if the customer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) throws URISyntaxException {
        LOG.debug("REST request to save Customer : {}", customerDTO);

        if (customerDTO.getId() != null) {
            throw new BadRequestAlertException("A new customer cannot already have an ID", ENTITY_NAME, "idexists");
        }

        customerDTO.setCreatedByClientAccountId(SecurityUtils.getCurrentClientAccountId());
        CustomerDTO result = customerService.create(customerDTO);

        return ResponseEntity.created(new URI("/api/customers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /customers/:id} : Updates an existing customer.
     *
     * @param id the id of the customerDTO to save.
     * @param customerDTO the customerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerDTO,
     * or with status {@code 400 (Bad Request)} if the customerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the customerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CustomerDTO customerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Customer : {}, {}", id, customerDTO);

        if (customerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CustomerDTO result = customerService.update(customerDTO);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, customerDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /customers} : get all customers for the current company.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of customers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        CustomerCriteria criteria,
        @RequestParam(value = "includeDeleted", required = false, defaultValue = "false") Boolean includeDeleted
    ) {
        LOG.debug("REST request to get Customers by criteria: {}", criteria);

        Page<CustomerDTO> page;

        if (criteria != null && !criteria.isEmpty()) {
            Specification<Customer> specification = CustomerSpecification.createSpecification(criteria);
            page = customerService.findAll(specification, pageable);
        } else if (Boolean.TRUE.equals(includeDeleted)) {
            page = customerService.findAllIncludingDeleted(pageable);
        } else {
            page = customerService.findAll(pageable);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /customers/:id} : get the "id" customer.
     *
     * @param id the id of the customerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Customer : {}", id);
        Optional<CustomerDTO> customerDTO = customerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(customerDTO);
    }

    /**
     * {@code DELETE  /customers/:id} : delete the "id" customer (soft delete).
     *
     * @param id the id of the customerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (No Content)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Customer : {}", id);
        customerService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code POST  /customers/:id/reactivate} : reactivate a soft deleted customer.
     *
     * @param id the id of the customer to reactivate.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
     */
    @PostMapping("/{id}/reactivate")
    public ResponseEntity<Void> reactivateCustomer(@PathVariable("id") Long id) {
        LOG.debug("REST request to reactivate Customer : {}", id);
        customerService.reactivate(id);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert(applicationName, "Customer reactivated", id.toString())).build();
    }

    /**
     * {@code GET  /customers/search} : search customers within company scope.
     *
     * @param query the search query.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the search results.
     */
    @GetMapping("/search")
    public ResponseEntity<List<CustomerDTO>> searchCustomers(
        @RequestParam("q") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search Customers : {}", query);

        Page<CustomerDTO> page = customerService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /customers/statistics} : get customer statistics for current company.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the statistics.
     */
    @GetMapping("/statistics")
    public ResponseEntity<CustomerStatsDTO> getCustomerStatistics() {
        LOG.debug("REST request to get Customer statistics");
        CustomerStatsDTO stats = customerService.getStatistics();
        return ResponseEntity.ok().body(stats);
    }

    /**
     * {@code GET  /customers/count} : count customers for current company.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the count.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCustomers() {
        LOG.debug("REST request to count Customers");
        Long count = customerService.count();
        return ResponseEntity.ok().body(count);
    }

    /**
     * {@code GET  /customers/check-phone} : check if phone exists within company.
     *
     * @param phone the phone number to check.
     * @param clientAccountId the client account ID.
     * @param excludeCustomerId customer ID to exclude from check.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the result.
     */
    @GetMapping("/check-phone")
    public ResponseEntity<Boolean> checkPhoneExistence(
        @RequestParam("phone") String phone,
        @RequestParam("clientAccountId") Long clientAccountId,
        @RequestParam(value = "excludeCustomerId", required = false) Long excludeCustomerId
    ) {
        LOG.debug("REST request to check phone existence : {}", phone);

        // Security check: can only check for current company
        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();
        if (!currentClientAccountId.equals(clientAccountId)) {
            throw new BadRequestAlertException("Can only check for current company", ENTITY_NAME, "invalidcompany");
        }

        boolean exists = customerService.phoneExistsInCompany(phone, clientAccountId, excludeCustomerId);
        return ResponseEntity.ok().body(exists);
    }

    /**
     * {@code GET  /customers/check-tax-id} : check if tax ID is unique within company.
     *
     * @param taxId the tax ID to check.
     * @param clientAccountId the client account ID.
     * @param excludeCustomerId customer ID to exclude from check.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the result.
     */
    @GetMapping("/check-tax-id")
    public ResponseEntity<Boolean> checkTaxIdUniqueness(
        @RequestParam("taxId") String taxId,
        @RequestParam("clientAccountId") Long clientAccountId,
        @RequestParam(value = "excludeCustomerId", required = false) Long excludeCustomerId
    ) {
        LOG.debug("REST request to check tax ID uniqueness : {}", taxId);

        // Security check: can only check for current company
        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();
        if (!currentClientAccountId.equals(clientAccountId)) {
            throw new BadRequestAlertException("Can only check for current company", ENTITY_NAME, "invalidcompany");
        }

        boolean isUnique = customerService.TaxIdExistsInCompany(taxId, clientAccountId, excludeCustomerId);
        return ResponseEntity.ok().body(isUnique);
    }

    /**
     * {@code POST  /customers/:id/create-account} : create marketplace account for customer.
     *
     * @param id the customer ID.
     * @param request the account creation request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
     */
    @PostMapping("/{id}/create-account")
    public ResponseEntity<Void> createMarketplaceAccount(@PathVariable("id") Long id, @Valid @RequestBody CreateAccountRequestDTO request) {
        LOG.debug("REST request to create marketplace account for Customer : {}", id);
        customerService.createMarketplaceAccount(id, request);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert(applicationName, "Marketplace account created", id.toString())).build();
    }

    /**
     * {@code POST  /customers/:id/resend-activation} : resend activation email for customer.
     *
     * @param id the customer ID.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
     */
    @PostMapping("/{id}/resend-activation")
    public ResponseEntity<Void> resendActivationEmail(@PathVariable("id") Long id) {
        LOG.debug("REST request to resend activation email for Customer : {}", id);
        customerService.resendActivationEmail(id);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert(applicationName, "Activation email resent", id.toString())).build();
    }
}
