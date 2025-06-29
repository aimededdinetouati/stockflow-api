package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.service.PublicCustomerService;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.dto.CustomerDTO;
import com.adeem.stockflow.service.dto.CustomerRegistrationDTO;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import com.adeem.stockflow.service.exceptions.ErrorConstants;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for public customer operations.
 * Handles customer self-registration and public company discovery.
 */
@RestController
@RequestMapping("/api/public/customers")
public class PublicCustomerResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublicCustomerResource.class);

    private static final String ENTITY_NAME = "customer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PublicCustomerService publicCustomerService;

    public PublicCustomerResource(PublicCustomerService publicCustomerService) {
        this.publicCustomerService = publicCustomerService;
    }

    /**
     * {@code POST  /public/customers/register} : Register a new independent customer in the marketplace.
     *
     * @param registrationDTO the registration data.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customerDTO, or with status {@code 400 (Bad Request)} if the registration data is invalid.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/register")
    public ResponseEntity<CustomerDTO> registerCustomer(@Valid @RequestBody CustomerRegistrationDTO registrationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to register new customer : {}", registrationDTO.getEmail());

        // Validate registration data
        PublicCustomerService.RegistrationValidationResult validation = publicCustomerService.validateRegistration(registrationDTO);
        if (validation.hasErrors()) {
            throw new BadRequestAlertException("Registration validation failed", ENTITY_NAME, "validationfailed");
        }

        CustomerDTO result = publicCustomerService.registerCustomer(registrationDTO);

        return ResponseEntity.created(new URI("/api/public/customers/" + result.getId()))
            .headers(
                HeaderUtil.createAlert(
                    applicationName,
                    "Registration successful. Please check your email to activate your account.",
                    result.getEmail()
                )
            )
            .body(result);
    }

    /**
     * {@code GET  /public/customers/validate-registration} : Validate registration data.
     *
     * @param email the email to validate.
     * @param phone the phone to validate.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the validation results.
     */
    @GetMapping("/validate-registration")
    public ResponseEntity<Map<String, Object>> validateRegistrationData(
        @RequestParam(value = "email", required = false) String email,
        @RequestParam(value = "phone", required = false) String phone
    ) {
        LOG.debug("REST request to validate registration data - email: {}, phone: {}", email, phone);

        Map<String, Object> validation = new java.util.HashMap<>();

        if (email != null) {
            validation.put("emailAvailable", publicCustomerService.isEmailAvailable(email));
        }

        if (phone != null) {
            validation.put("phoneAvailable", publicCustomerService.isPhoneAvailable(phone));
        }

        return ResponseEntity.ok().body(validation);
    }

    /**
     * {@code GET  /public/customers/marketplace-stats} : Get marketplace statistics.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the marketplace statistics.
     */
    @GetMapping("/marketplace-stats")
    public ResponseEntity<PublicCustomerService.MarketplaceStatsDTO> getMarketplaceStatistics() {
        LOG.debug("REST request to get marketplace statistics");

        PublicCustomerService.MarketplaceStatsDTO stats = publicCustomerService.getMarketplaceStatistics();
        return ResponseEntity.ok().body(stats);
    }
}

/**
 * REST controller for public company discovery.
 */
@RestController
@RequestMapping("/api/public/companies")
class PublicCompanyResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublicCompanyResource.class);

    private final PublicCustomerService publicCustomerService;

    public PublicCompanyResource(PublicCustomerService publicCustomerService) {
        this.publicCustomerService = publicCustomerService;
    }

    /**
     * {@code GET  /public/companies} : Get list of companies available for association.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of companies in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ClientAccountDTO>> getAvailableCompanies(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get available companies");

        Page<ClientAccountDTO> page = publicCustomerService.findAvailableCompanies(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /public/companies/:id/profile} : Get public profile of a company.
     *
     * @param id the company ID.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the company profile, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}/profile")
    public ResponseEntity<ClientAccountDTO> getCompanyProfile(@PathVariable("id") Long id) {
        LOG.debug("REST request to get company profile : {}", id);

        Optional<ClientAccountDTO> companyProfile = publicCustomerService.getCompanyProfile(id);
        return ResponseUtil.wrapOrNotFound(companyProfile);
    }

    /**
     * {@code GET  /public/companies/search} : Search companies by name.
     *
     * @param query the search query.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the search results.
     */
    @GetMapping("/search")
    public ResponseEntity<List<ClientAccountDTO>> searchCompanies(
        @RequestParam("q") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search companies : {}", query);

        Page<ClientAccountDTO> page = publicCustomerService.searchCompanies(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /public/companies/for-customer/:customerId} : Get companies available for a specific customer.
     *
     * @param customerId the customer ID.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the available companies.
     */
    @GetMapping("/for-customer/{customerId}")
    public ResponseEntity<List<ClientAccountDTO>> getAvailableCompaniesForCustomer(
        @PathVariable("customerId") Long customerId,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get available companies for customer : {}", customerId);

        Page<ClientAccountDTO> page = publicCustomerService.findAvailableCompaniesForCustomer(customerId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
