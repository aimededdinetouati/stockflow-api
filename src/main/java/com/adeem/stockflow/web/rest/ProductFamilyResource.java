package com.adeem.stockflow.web.rest;

import com.adeem.stockflow.repository.ProductFamilyRepository;
import com.adeem.stockflow.service.ProductFamilyService;
import com.adeem.stockflow.service.dto.ProductFamilyDTO;
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
 * REST controller for managing {@link com.adeem.stockflow.domain.ProductFamily}.
 */
@RestController
@RequestMapping("/api/product-families")
public class ProductFamilyResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductFamilyResource.class);

    private static final String ENTITY_NAME = "productFamily";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductFamilyService productFamilyService;

    private final ProductFamilyRepository productFamilyRepository;

    public ProductFamilyResource(ProductFamilyService productFamilyService, ProductFamilyRepository productFamilyRepository) {
        this.productFamilyService = productFamilyService;
        this.productFamilyRepository = productFamilyRepository;
    }

    /**
     * {@code POST  /product-families} : Create a new productFamily.
     *
     * @param productFamilyDTO the productFamilyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productFamilyDTO, or with status {@code 400 (Bad Request)} if the productFamily has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProductFamilyDTO> createProductFamily(@Valid @RequestBody ProductFamilyDTO productFamilyDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ProductFamily : {}", productFamilyDTO);
        if (productFamilyDTO.getId() != null) {
            throw new BadRequestAlertException("A new productFamily cannot already have an ID", ENTITY_NAME, "idexists");
        }
        productFamilyDTO = productFamilyService.save(productFamilyDTO);
        return ResponseEntity.created(new URI("/api/product-families/" + productFamilyDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, productFamilyDTO.getId().toString()))
            .body(productFamilyDTO);
    }

    /**
     * {@code PUT  /product-families/:id} : Updates an existing productFamily.
     *
     * @param id the id of the productFamilyDTO to save.
     * @param productFamilyDTO the productFamilyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productFamilyDTO,
     * or with status {@code 400 (Bad Request)} if the productFamilyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productFamilyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductFamilyDTO> updateProductFamily(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductFamilyDTO productFamilyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProductFamily : {}, {}", id, productFamilyDTO);
        if (productFamilyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productFamilyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productFamilyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        productFamilyDTO = productFamilyService.update(productFamilyDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productFamilyDTO.getId().toString()))
            .body(productFamilyDTO);
    }

    /**
     * {@code PATCH  /product-families/:id} : Partial updates given fields of an existing productFamily, field will ignore if it is null
     *
     * @param id the id of the productFamilyDTO to save.
     * @param productFamilyDTO the productFamilyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productFamilyDTO,
     * or with status {@code 400 (Bad Request)} if the productFamilyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productFamilyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productFamilyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductFamilyDTO> partialUpdateProductFamily(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductFamilyDTO productFamilyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProductFamily partially : {}, {}", id, productFamilyDTO);
        if (productFamilyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productFamilyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productFamilyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductFamilyDTO> result = productFamilyService.partialUpdate(productFamilyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productFamilyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /product-families} : get all the productFamilies.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productFamilies in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProductFamilyDTO>> getAllProductFamilies(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of ProductFamilies");
        Page<ProductFamilyDTO> page = productFamilyService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /product-families/:id} : get the "id" productFamily.
     *
     * @param id the id of the productFamilyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productFamilyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductFamilyDTO> getProductFamily(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProductFamily : {}", id);
        Optional<ProductFamilyDTO> productFamilyDTO = productFamilyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productFamilyDTO);
    }

    /**
     * {@code DELETE  /product-families/:id} : delete the "id" productFamily.
     *
     * @param id the id of the productFamilyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductFamily(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProductFamily : {}", id);
        productFamilyService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
