package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.repository.ClientAccountRepository;
import com.adeem.stockflow.service.ClientAccountService;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
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
 * REST controller for managing {@link com.adeem.stockflow.domain.ClientAccount}.
 */
@RestController
@RequestMapping("/api/client-accounts")
public class ClientAccountResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClientAccountResource.class);

    private static final String ENTITY_NAME = "clientAccount";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClientAccountService clientAccountService;

    private final ClientAccountRepository clientAccountRepository;

    public ClientAccountResource(ClientAccountService clientAccountService, ClientAccountRepository clientAccountRepository) {
        this.clientAccountService = clientAccountService;
        this.clientAccountRepository = clientAccountRepository;
    }

    /**
     * {@code POST  /client-accounts} : Create a new clientAccount.
     *
     * @param clientAccountDTO the clientAccountDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clientAccountDTO, or with status {@code 400 (Bad Request)} if the clientAccount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClientAccountDTO> createClientAccount(@Valid @RequestBody ClientAccountDTO clientAccountDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ClientAccount : {}", clientAccountDTO);
        if (clientAccountDTO.getId() != null) {
            throw new BadRequestAlertException("A new clientAccount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        clientAccountDTO = clientAccountService.save(clientAccountDTO);
        return ResponseEntity.created(new URI("/api/client-accounts/" + clientAccountDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, clientAccountDTO.getId().toString()))
            .body(clientAccountDTO);
    }

    /**
     * {@code PUT  /client-accounts/:id} : Updates an existing clientAccount.
     *
     * @param id the id of the clientAccountDTO to save.
     * @param clientAccountDTO the clientAccountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientAccountDTO,
     * or with status {@code 400 (Bad Request)} if the clientAccountDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clientAccountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClientAccountDTO> updateClientAccount(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClientAccountDTO clientAccountDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ClientAccount : {}, {}", id, clientAccountDTO);
        if (clientAccountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientAccountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientAccountRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        clientAccountDTO = clientAccountService.update(clientAccountDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientAccountDTO.getId().toString()))
            .body(clientAccountDTO);
    }

    /**
     * {@code PATCH  /client-accounts/:id} : Partial updates given fields of an existing clientAccount, field will ignore if it is null
     *
     * @param id the id of the clientAccountDTO to save.
     * @param clientAccountDTO the clientAccountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientAccountDTO,
     * or with status {@code 400 (Bad Request)} if the clientAccountDTO is not valid,
     * or with status {@code 404 (Not Found)} if the clientAccountDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the clientAccountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClientAccountDTO> partialUpdateClientAccount(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClientAccountDTO clientAccountDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ClientAccount partially : {}, {}", id, clientAccountDTO);
        if (clientAccountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientAccountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientAccountRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClientAccountDTO> result = clientAccountService.partialUpdate(clientAccountDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientAccountDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /client-accounts} : get all the clientAccounts.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clientAccounts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ClientAccountDTO>> getAllClientAccounts(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of ClientAccounts");
        Page<ClientAccountDTO> page = clientAccountService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /client-accounts/:id} : get the "id" clientAccount.
     *
     * @param id the id of the clientAccountDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clientAccountDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientAccountDTO> getClientAccount(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClientAccount : {}", id);
        Optional<ClientAccountDTO> clientAccountDTO = clientAccountService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clientAccountDTO);
    }

    /**
     * {@code DELETE  /client-accounts/:id} : delete the "id" clientAccount.
     *
     * @param id the id of the clientAccountDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClientAccount(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClientAccount : {}", id);
        clientAccountService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
