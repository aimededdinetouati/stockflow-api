package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.repository.PaymentConfigurationRepository;
import com.adeem.stockflow.service.PaymentConfigurationService;
import com.adeem.stockflow.service.dto.PaymentConfigurationDTO;
import com.adeem.stockflow.web.rest.errors.BadRequestAlertException;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.adeem.stockflow.domain.PaymentConfiguration}.
 */
@RestController
@RequestMapping("/api/payment-configurations")
public class PaymentConfigurationResource {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentConfigurationResource.class);

    private static final String ENTITY_NAME = "paymentConfiguration";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PaymentConfigurationService paymentConfigurationService;

    private final PaymentConfigurationRepository paymentConfigurationRepository;

    public PaymentConfigurationResource(
        PaymentConfigurationService paymentConfigurationService,
        PaymentConfigurationRepository paymentConfigurationRepository
    ) {
        this.paymentConfigurationService = paymentConfigurationService;
        this.paymentConfigurationRepository = paymentConfigurationRepository;
    }

    /**
     * {@code POST  /payment-configurations} : Create a new paymentConfiguration.
     *
     * @param paymentConfigurationDTO the paymentConfigurationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paymentConfigurationDTO, or with status {@code 400 (Bad Request)} if the paymentConfiguration has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PaymentConfigurationDTO> createPaymentConfiguration(@RequestBody PaymentConfigurationDTO paymentConfigurationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PaymentConfiguration : {}", paymentConfigurationDTO);
        if (paymentConfigurationDTO.getId() != null) {
            throw new BadRequestAlertException("A new paymentConfiguration cannot already have an ID", ENTITY_NAME, "idexists");
        }
        paymentConfigurationDTO = paymentConfigurationService.save(paymentConfigurationDTO);
        return ResponseEntity.created(new URI("/api/payment-configurations/" + paymentConfigurationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, paymentConfigurationDTO.getId().toString()))
            .body(paymentConfigurationDTO);
    }

    /**
     * {@code PUT  /payment-configurations/:id} : Updates an existing paymentConfiguration.
     *
     * @param id the id of the paymentConfigurationDTO to save.
     * @param paymentConfigurationDTO the paymentConfigurationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentConfigurationDTO,
     * or with status {@code 400 (Bad Request)} if the paymentConfigurationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the paymentConfigurationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PaymentConfigurationDTO> updatePaymentConfiguration(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PaymentConfigurationDTO paymentConfigurationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PaymentConfiguration : {}, {}", id, paymentConfigurationDTO);
        if (paymentConfigurationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentConfigurationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paymentConfigurationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        paymentConfigurationDTO = paymentConfigurationService.update(paymentConfigurationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paymentConfigurationDTO.getId().toString()))
            .body(paymentConfigurationDTO);
    }

    /**
     * {@code PATCH  /payment-configurations/:id} : Partial updates given fields of an existing paymentConfiguration, field will ignore if it is null
     *
     * @param id the id of the paymentConfigurationDTO to save.
     * @param paymentConfigurationDTO the paymentConfigurationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentConfigurationDTO,
     * or with status {@code 400 (Bad Request)} if the paymentConfigurationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the paymentConfigurationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the paymentConfigurationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PaymentConfigurationDTO> partialUpdatePaymentConfiguration(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PaymentConfigurationDTO paymentConfigurationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PaymentConfiguration partially : {}, {}", id, paymentConfigurationDTO);
        if (paymentConfigurationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentConfigurationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paymentConfigurationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PaymentConfigurationDTO> result = paymentConfigurationService.partialUpdate(paymentConfigurationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paymentConfigurationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /payment-configurations} : get all the paymentConfigurations.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of paymentConfigurations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PaymentConfigurationDTO>> getAllPaymentConfigurations(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of PaymentConfigurations");
        Page<PaymentConfigurationDTO> page = paymentConfigurationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /payment-configurations/:id} : get the "id" paymentConfiguration.
     *
     * @param id the id of the paymentConfigurationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paymentConfigurationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentConfigurationDTO> getPaymentConfiguration(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PaymentConfiguration : {}", id);
        Optional<PaymentConfigurationDTO> paymentConfigurationDTO = paymentConfigurationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(paymentConfigurationDTO);
    }

    /**
     * {@code DELETE  /payment-configurations/:id} : delete the "id" paymentConfiguration.
     *
     * @param id the id of the paymentConfigurationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentConfiguration(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PaymentConfiguration : {}", id);
        paymentConfigurationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
