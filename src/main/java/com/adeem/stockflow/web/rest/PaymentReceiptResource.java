package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.repository.PaymentReceiptRepository;
import com.adeem.stockflow.service.PaymentReceiptService;
import com.adeem.stockflow.service.dto.PaymentReceiptDTO;
import com.adeem.stockflow.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
 * REST controller for managing {@link com.adeem.stockflow.domain.PaymentReceipt}.
 */
@RestController
@RequestMapping("/api/payment-receipts")
public class PaymentReceiptResource {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentReceiptResource.class);

    private static final String ENTITY_NAME = "paymentReceipt";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PaymentReceiptService paymentReceiptService;

    private final PaymentReceiptRepository paymentReceiptRepository;

    public PaymentReceiptResource(PaymentReceiptService paymentReceiptService, PaymentReceiptRepository paymentReceiptRepository) {
        this.paymentReceiptService = paymentReceiptService;
        this.paymentReceiptRepository = paymentReceiptRepository;
    }

    /**
     * {@code POST  /payment-receipts} : Create a new paymentReceipt.
     *
     * @param paymentReceiptDTO the paymentReceiptDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paymentReceiptDTO, or with status {@code 400 (Bad Request)} if the paymentReceipt has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PaymentReceiptDTO> createPaymentReceipt(@Valid @RequestBody PaymentReceiptDTO paymentReceiptDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PaymentReceipt : {}", paymentReceiptDTO);
        if (paymentReceiptDTO.getId() != null) {
            throw new BadRequestAlertException("A new paymentReceipt cannot already have an ID", ENTITY_NAME, "idexists");
        }
        paymentReceiptDTO = paymentReceiptService.save(paymentReceiptDTO);
        return ResponseEntity.created(new URI("/api/payment-receipts/" + paymentReceiptDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, paymentReceiptDTO.getId().toString()))
            .body(paymentReceiptDTO);
    }

    /**
     * {@code PUT  /payment-receipts/:id} : Updates an existing paymentReceipt.
     *
     * @param id the id of the paymentReceiptDTO to save.
     * @param paymentReceiptDTO the paymentReceiptDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentReceiptDTO,
     * or with status {@code 400 (Bad Request)} if the paymentReceiptDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the paymentReceiptDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PaymentReceiptDTO> updatePaymentReceipt(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PaymentReceiptDTO paymentReceiptDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PaymentReceipt : {}, {}", id, paymentReceiptDTO);
        if (paymentReceiptDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentReceiptDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paymentReceiptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        paymentReceiptDTO = paymentReceiptService.update(paymentReceiptDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paymentReceiptDTO.getId().toString()))
            .body(paymentReceiptDTO);
    }

    /**
     * {@code PATCH  /payment-receipts/:id} : Partial updates given fields of an existing paymentReceipt, field will ignore if it is null
     *
     * @param id the id of the paymentReceiptDTO to save.
     * @param paymentReceiptDTO the paymentReceiptDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentReceiptDTO,
     * or with status {@code 400 (Bad Request)} if the paymentReceiptDTO is not valid,
     * or with status {@code 404 (Not Found)} if the paymentReceiptDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the paymentReceiptDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PaymentReceiptDTO> partialUpdatePaymentReceipt(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PaymentReceiptDTO paymentReceiptDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PaymentReceipt partially : {}, {}", id, paymentReceiptDTO);
        if (paymentReceiptDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentReceiptDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paymentReceiptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PaymentReceiptDTO> result = paymentReceiptService.partialUpdate(paymentReceiptDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paymentReceiptDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /payment-receipts} : get all the paymentReceipts.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of paymentReceipts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PaymentReceiptDTO>> getAllPaymentReceipts(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of PaymentReceipts");
        Page<PaymentReceiptDTO> page = paymentReceiptService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /payment-receipts/:id} : get the "id" paymentReceipt.
     *
     * @param id the id of the paymentReceiptDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paymentReceiptDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentReceiptDTO> getPaymentReceipt(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PaymentReceipt : {}", id);
        Optional<PaymentReceiptDTO> paymentReceiptDTO = paymentReceiptService.findOne(id);
        return ResponseUtil.wrapOrNotFound(paymentReceiptDTO);
    }

    /**
     * {@code DELETE  /payment-receipts/:id} : delete the "id" paymentReceipt.
     *
     * @param id the id of the paymentReceiptDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentReceipt(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PaymentReceipt : {}", id);
        paymentReceiptService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
