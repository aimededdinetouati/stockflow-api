package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.domain.enumeration.AttachmentType;
import com.adeem.stockflow.domain.enumeration.TransactionType;
import com.adeem.stockflow.repository.ProductFamilyRepository;
import com.adeem.stockflow.repository.ProductRepository;
import com.adeem.stockflow.service.criteria.ProductSpecification;
import com.adeem.stockflow.service.dto.AttachmentDTO;
import com.adeem.stockflow.service.dto.InventoryDTO;
import com.adeem.stockflow.service.dto.ProductDTO;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import com.adeem.stockflow.service.exceptions.ErrorConstants;
import com.adeem.stockflow.service.mapper.ProductMapper;
import java.io.IOException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Implementation for managing {@link com.adeem.stockflow.domain.Product}.
 */
@Service
@Transactional
public class ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductFamilyRepository productFamilyRepository;
    private final InventoryService inventoryService;
    private final AttachmentService attachmentService;
    private final ProductMapper productMapper;

    public ProductService(
        ProductRepository productRepository,
        ProductFamilyRepository productFamilyRepository,
        InventoryService inventoryService,
        AttachmentService attachmentService,
        ProductMapper productMapper
    ) {
        this.productRepository = productRepository;
        this.productFamilyRepository = productFamilyRepository;
        this.inventoryService = inventoryService;
        this.attachmentService = attachmentService;
        this.productMapper = productMapper;
    }

    /**
     * Save a product.
     *
     * @param productDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductDTO save(ProductDTO productDTO) {
        LOG.debug("Request to save Product : {}", productDTO);
        Product product = productMapper.toEntity(productDTO);
        product = productRepository.save(product);
        return productMapper.toDto(product);
    }

    public ProductDTO create(ProductDTO productDTO, InventoryDTO inventoryDTO, List<MultipartFile> images) throws IOException {
        LOG.debug("Request to create Product : {}", productDTO);
        checkFields(productDTO);

        ProductDTO newProduct = new ProductDTO();

        // Set product fields
        newProduct.setCode(productDTO.getCode());
        newProduct.setName(productDTO.getName());
        newProduct.setDescription(productDTO.getDescription());
        newProduct.setManufacturerCode(productDTO.getManufacturerCode());
        newProduct.setUpc(productDTO.getUpc());
        newProduct.setSellingPrice(productDTO.getSellingPrice());
        newProduct.setCostPrice(productDTO.getCostPrice());
        newProduct.setProfitMargin(productDTO.getProfitMargin());
        newProduct.setMinimumStockLevel(productDTO.getMinimumStockLevel());
        newProduct.setCategory(productDTO.getCategory());
        newProduct.setApplyTva(productDTO.getApplyTva());
        newProduct.setIsVisibleToCustomers(productDTO.getIsVisibleToCustomers());
        newProduct.setExpirationDate(productDTO.getExpirationDate());
        newProduct.setClientAccountId(productDTO.getClientAccountId());
        newProduct.setProductFamily(productDTO.getProductFamily());

        ProductDTO savedProduct = save(newProduct);

        inventoryDTO.setProductId(savedProduct.getId());
        inventoryService.create(
            inventoryDTO.getQuantity(),
            inventoryDTO.getAvailableQuantity(),
            TransactionType.INITIAL,
            savedProduct.getId()
        );

        if (images != null && !images.isEmpty()) {
            addProductImages(productDTO.getId(), images);
        }

        return savedProduct;
    }

    private void checkFields(ProductDTO productDTO) {
        LOG.debug("Checking product validity");

        // Check if a product with the same code already exists in the database to ensure uniqueness
        productRepository
            .findOne(ProductSpecification.withCode(productDTO.getCode()))
            .ifPresent(existing -> {
                if (!existing.getCode().equals(productDTO.getCode())) {
                    throw new BadRequestAlertException("Product code already exists", "product", ErrorConstants.PRODUCT_CODE_EXISTS);
                }
            });

        // Verify if the specified product family exists in the database
        var productFamily = productDTO.getProductFamily();
        if (productFamily != null) {
            productFamilyRepository
                .findById(productFamily.getId())
                .orElseThrow(() ->
                    new BadRequestAlertException("Product family does not exist", "", ErrorConstants.PRODUCT_FAMILY_DOES_NOT_EXIST)
                );
        }
    }

    private void addProductImages(Long productId, List<MultipartFile> images) throws IOException {
        List<AttachmentDTO> attachmentDTOList = new ArrayList<>();
        for (MultipartFile image : images) {
            AttachmentDTO attachmentDTO = new AttachmentDTO();
            attachmentDTO.setProductId(productId);
            attachmentDTO.setAltText(image.getOriginalFilename());
            attachmentDTO.setData(image.getBytes());
            attachmentDTO.setType(AttachmentType.PRODUCT_IMAGE);
            attachmentDTO.setFileSize(image.getSize());
            attachmentDTO.setIsPrimary(false);
            attachmentDTO.setDataContentType(image.getContentType());
            attachmentDTOList.add(attachmentDTO);
        }
        attachmentService.saveAll(attachmentDTOList);
    }

    /**
     * Update a product.
     *
     * @param productDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductDTO update(ProductDTO productDTO) {
        LOG.debug("Request to update Product : {}", productDTO);
        Product product = productMapper.toEntity(productDTO);
        product.setIsPersisted();
        product = productRepository.save(product);
        return productMapper.toDto(product);
    }

    /**
     * Partially update a product.
     *
     * @param productDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProductDTO> partialUpdate(ProductDTO productDTO) {
        LOG.debug("Request to partially update Product : {}", productDTO);

        return productRepository
            .findById(productDTO.getId())
            .map(existingProduct -> {
                productMapper.partialUpdate(existingProduct, productDTO);

                return existingProduct;
            })
            .map(productRepository::save)
            .map(productMapper::toDto);
    }

    /**
     * Get all the products.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Products");
        return productRepository.findAll(pageable).map(productMapper::toDto);
    }

    /**
     * Get one product by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductDTO> findOne(Long id) {
        LOG.debug("Request to get Product : {}", id);
        return productRepository.findById(id).map(productMapper::toDto);
    }

    /**
     * Delete the product by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Product : {}", id);
        productRepository.deleteById(id);
    }
}
