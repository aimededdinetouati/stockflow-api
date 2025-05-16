package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.ProductFamily;
import com.adeem.stockflow.repository.ProductFamilyRepository;
import com.adeem.stockflow.service.dto.ProductFamilyDTO;
import com.adeem.stockflow.service.mapper.ProductFamilyMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.ProductFamily}.
 */
@Service
@Transactional
public class ProductFamilyService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductFamilyService.class);

    private final ProductFamilyRepository productFamilyRepository;

    private final ProductFamilyMapper productFamilyMapper;

    public ProductFamilyService(ProductFamilyRepository productFamilyRepository, ProductFamilyMapper productFamilyMapper) {
        this.productFamilyRepository = productFamilyRepository;
        this.productFamilyMapper = productFamilyMapper;
    }

    /**
     * Save a productFamily.
     *
     * @param productFamilyDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductFamilyDTO save(ProductFamilyDTO productFamilyDTO) {
        LOG.debug("Request to save ProductFamily : {}", productFamilyDTO);
        ProductFamily productFamily = productFamilyMapper.toEntity(productFamilyDTO);
        productFamily = productFamilyRepository.save(productFamily);
        return productFamilyMapper.toDto(productFamily);
    }

    /**
     * Update a productFamily.
     *
     * @param productFamilyDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductFamilyDTO update(ProductFamilyDTO productFamilyDTO) {
        LOG.debug("Request to update ProductFamily : {}", productFamilyDTO);
        ProductFamily productFamily = productFamilyMapper.toEntity(productFamilyDTO);
        productFamily.setIsPersisted();
        productFamily = productFamilyRepository.save(productFamily);
        return productFamilyMapper.toDto(productFamily);
    }

    /**
     * Partially update a productFamily.
     *
     * @param productFamilyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProductFamilyDTO> partialUpdate(ProductFamilyDTO productFamilyDTO) {
        LOG.debug("Request to partially update ProductFamily : {}", productFamilyDTO);

        return productFamilyRepository
            .findById(productFamilyDTO.getId())
            .map(existingProductFamily -> {
                productFamilyMapper.partialUpdate(existingProductFamily, productFamilyDTO);

                return existingProductFamily;
            })
            .map(productFamilyRepository::save)
            .map(productFamilyMapper::toDto);
    }

    /**
     * Get all the productFamilies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductFamilyDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ProductFamilies");
        return productFamilyRepository.findAll(pageable).map(productFamilyMapper::toDto);
    }

    /**
     * Get one productFamily by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductFamilyDTO> findOne(Long id) {
        LOG.debug("Request to get ProductFamily : {}", id);
        return productFamilyRepository.findById(id).map(productFamilyMapper::toDto);
    }

    /**
     * Delete the productFamily by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ProductFamily : {}", id);
        productFamilyRepository.deleteById(id);
    }
}
