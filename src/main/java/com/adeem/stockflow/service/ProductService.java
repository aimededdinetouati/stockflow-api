package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.Inventory;
import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.domain.ProductFamily;
import com.adeem.stockflow.domain.enumeration.AttachmentType;
import com.adeem.stockflow.domain.enumeration.TransactionType;
import com.adeem.stockflow.repository.ProductFamilyRepository;
import com.adeem.stockflow.repository.ProductRepository;
import com.adeem.stockflow.service.criteria.ProductSpecification;
import com.adeem.stockflow.service.dto.*;
import com.adeem.stockflow.service.exceptions.BadRequestAlertException;
import com.adeem.stockflow.service.exceptions.ErrorConstants;
import com.adeem.stockflow.service.mapper.InventoryMapper;
import com.adeem.stockflow.service.mapper.ProductMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final InventoryMapper inventoryMapper;

    public ProductService(
        ProductRepository productRepository,
        ProductFamilyRepository productFamilyRepository,
        InventoryService inventoryService,
        AttachmentService attachmentService,
        ProductMapper productMapper,
        InventoryMapper inventoryMapper
    ) {
        this.productRepository = productRepository;
        this.productFamilyRepository = productFamilyRepository;
        this.inventoryService = inventoryService;
        this.attachmentService = attachmentService;
        this.productMapper = productMapper;
        this.inventoryMapper = inventoryMapper;
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

    @CacheEvict(value = "inventoryStats", key = "#productDTO.clientAccountId")
    public ProductDTO create(ProductDTO productDTO, InventoryDTO inventoryDTO, List<MultipartFile> images) throws IOException {
        LOG.debug("Request to create Product : {}", productDTO);
        checkFields(productDTO);

        ProductDTO newProduct = copyProductFields(productDTO, new ProductDTO());

        ProductDTO savedProduct = save(newProduct);

        if (inventoryDTO != null) {
            inventoryDTO.setClientAccountId(productDTO.getClientAccountId());
            inventoryDTO.setProduct(savedProduct);
            inventoryDTO.setQuantity(inventoryDTO.getQuantity() == null ? BigDecimal.ZERO : inventoryDTO.getQuantity());
            inventoryService.create(inventoryDTO);
        }

        if (images != null && !images.isEmpty()) {
            addProductImages(savedProduct.getId(), images);
        }

        return savedProduct;
    }

    /**
     * Update a product with inventory and images.
     *
     * @param productDTO the entity to update
     * @param inventoryDTO the inventory data to update (optional)
     * @param images the product images to add (optional)
     * @return the updated entity
     */
    @CacheEvict(value = "inventoryStats", key = "#productDTO.clientAccountId")
    public ProductDTO update(ProductDTO productDTO, InventoryDTO inventoryDTO, List<MultipartFile> images) throws IOException {
        LOG.debug("Request to update Product : {}", productDTO);

        Product existing = productRepository
            .findById(productDTO.getId())
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", "", "idnotfound"));

        // Validate the product fields
        checkFields(productDTO);

        // Update product fields
        updateProductFields(existing, productDTO);

        // update the product family
        updateProductFamily(existing, productDTO.getProductFamily());

        // Update the product fields
        existing.setIsPersisted();
        Product updated = productRepository.save(existing);

        // Update the inventory if provided
        if (inventoryDTO != null && inventoryDTO.getId() != null) {
            inventoryDTO.setClientAccountId(productDTO.getClientAccountId());
            inventoryDTO.setProduct(productMapper.toDto(updated));
            inventoryService.update(inventoryDTO);
        }

        // Add new images if provided
        if (images != null && !images.isEmpty()) {
            addProductImages(updated.getId(), images);
        }

        return productMapper.toDto(updated);
    }

    /**
     * Helper method to copy product fields from source to target DTO
     */
    private ProductDTO copyProductFields(ProductDTO source, ProductDTO target) {
        target.setCode(source.getCode());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setManufacturerCode(source.getManufacturerCode());
        target.setUpc(source.getUpc());
        target.setSellingPrice(source.getSellingPrice());
        target.setCostPrice(source.getCostPrice());
        target.setProfitMargin(source.getProfitMargin());
        target.setMinimumStockLevel(source.getMinimumStockLevel());
        target.setCategory(source.getCategory());
        target.setApplyTva(source.getApplyTva());
        target.setIsVisibleToCustomers(source.getIsVisibleToCustomers());
        target.setExpirationDate(source.getExpirationDate());
        target.setClientAccountId(source.getClientAccountId());
        target.setProductFamily(source.getProductFamily());
        return target;
    }

    /**
     * Helper method to update product fields from DTO to entity
     */
    private void updateProductFields(Product entity, ProductDTO dto) {
        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());
        entity.setCategory(dto.getCategory());
        entity.setManufacturerCode(dto.getManufacturerCode());
        entity.setUpc(dto.getUpc());
        entity.setSellingPrice(dto.getSellingPrice());
        entity.setCostPrice(dto.getCostPrice());
        entity.setProfitMargin(dto.getProfitMargin());
        entity.setMinimumStockLevel(dto.getMinimumStockLevel());
        entity.setApplyTva(dto.getApplyTva());
        entity.setIsVisibleToCustomers(dto.getIsVisibleToCustomers());
        entity.setExpirationDate(dto.getExpirationDate());
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

        if (productDTO.getApplyTva() == null) {
            throw new BadRequestAlertException("Apply TVA cannot be null", "product", "applytva");
        }

        if (productDTO.getName() == null) {
            throw new BadRequestAlertException("Name cannot be null", "product", "name");
        }

        if (productDTO.getCode() == null) {
            throw new BadRequestAlertException("Code cannot be null", "product", "code");
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
     * Update the product family of a product.
     *
     * @param product the product entity to update
     * @param newProductFamilyDTO the new product family data (can be null to remove product family)
     */
    private void updateProductFamily(Product product, ProductFamilyDTO newProductFamilyDTO) {
        // If no change in product family, do nothing
        if (!isProductFamilyChanged(product, newProductFamilyDTO)) {
            return;
        }

        // Set product family to null if newProductFamilyDTO is null
        if (newProductFamilyDTO == null) {
            product.setProductFamily(null);
            return;
        }

        // Otherwise, fetch and set the new product family
        ProductFamily newProductFamily = findProductFamilyById(newProductFamilyDTO.getId());
        product.setProductFamily(newProductFamily);
    }

    /**
     * Check if the product family has changed.
     *
     * @param product the product entity
     * @param newProductFamilyDTO the new product family data
     * @return true if the product family has changed, false otherwise
     */
    private boolean isProductFamilyChanged(Product product, ProductFamilyDTO newProductFamilyDTO) {
        ProductFamily existingProductFamily = product.getProductFamily();

        // Both are null - no change
        if (existingProductFamily == null && newProductFamilyDTO == null) {
            return false;
        }

        // One is null, the other is not - change detected
        if (existingProductFamily == null || newProductFamilyDTO == null) {
            return true;
        }

        // Both are not null - compare IDs
        return !existingProductFamily.getId().equals(newProductFamilyDTO.getId());
    }

    /**
     * Find a product family by ID or throw an exception if not found.
     *
     * @param id the product family ID
     * @return the product family entity
     * @throws BadRequestAlertException if the product family does not exist
     */
    private ProductFamily findProductFamilyById(Long id) {
        return productFamilyRepository
            .findById(id)
            .orElseThrow(() ->
                new BadRequestAlertException("Product family does not exist", "", ErrorConstants.PRODUCT_FAMILY_DOES_NOT_EXIST)
            );
    }

    /**
     * Get all the products.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Specification<Product> specification, Pageable pageable) {
        LOG.debug("Request to get all Products");
        return productRepository.findAll(specification, pageable).map(productMapper::toDto);
    }

    /**
     * Get one product by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductDTO> findOne(Long id) {
        LOG.debug("Request to get ProductDTO : {}", id);
        return productRepository.findById(id).map(productMapper::toDto);
    }

    public Optional<Product> findEntity(Specification<Product> specification) {
        LOG.debug("Request to get Product : {}", specification);
        return productRepository.findOne(specification);
    }

    /**
     * Count products matching the given specification.
     *
     * @param spec The specification to match.
     * @return The count of matching products.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(Specification<Product> spec) {
        LOG.debug("Request to count Products by criteria");
        return productRepository.count(spec);
    }

    /**
     * Get one product by id and client account id.
     *
     * @param id the id of the entity.
     * @param clientAccountId the client account id.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductDTO> findOneForClientAccount(Long id, Long clientAccountId) {
        LOG.debug("Request to get ProductDTO for client account : {}, {}", id, clientAccountId);
        return productRepository
            .findOne(ProductSpecification.withId(id).and(ProductSpecification.withClientAccountId(clientAccountId)))
            .map(productMapper::toDto);
    }

    /**
     * Find products with inventory below minimum stock level.
     *
     * @param clientAccountId the client account ID.
     * @param pageable the pagination information.
     * @return a page of products with low stock.
     */
    @Transactional(readOnly = true)
    public Page<ProductWithInventoryDTO> findLowStockProducts(Long clientAccountId, Pageable pageable) {
        LOG.debug("Request to get low stock products for client account : {}", clientAccountId);

        // Get products with their inventory information
        Page<Object[]> result = productRepository.findProductsWithLowStock(clientAccountId, pageable);

        List<ProductWithInventoryDTO> dtoList = new ArrayList<>();

        for (Object[] row : result.getContent()) {
            Product product = (Product) row[0];
            com.adeem.stockflow.domain.Inventory inventory = (com.adeem.stockflow.domain.Inventory) row[1];

            ProductWithInventoryDTO dto = new ProductWithInventoryDTO();
            dto.setProduct(productMapper.toDto(product));
            if (inventory != null) {
                dto.setInventory(inventoryMapper.toDto(inventory));
            }

            dtoList.add(dto);
        }

        return new PageImpl<>(dtoList, pageable, result.getTotalElements());
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
