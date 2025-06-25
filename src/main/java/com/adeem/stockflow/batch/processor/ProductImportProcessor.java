package com.adeem.stockflow.batch.processor;

import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.ProductFamily;
import com.adeem.stockflow.domain.enumeration.ImportErrorType;
import com.adeem.stockflow.domain.enumeration.InventoryStatus;
import com.adeem.stockflow.domain.enumeration.ProductCategory;
import com.adeem.stockflow.repository.ClientAccountRepository;
import com.adeem.stockflow.repository.ProductFamilyRepository;
import com.adeem.stockflow.repository.ProductRepository;
import com.adeem.stockflow.service.InventoryService;
import com.adeem.stockflow.service.ProductService;
import com.adeem.stockflow.service.criteria.ProductSpecification;
import com.adeem.stockflow.service.dto.*;
import com.adeem.stockflow.service.dto.batch.ImportErrorDTO;
import com.adeem.stockflow.service.dto.batch.ProductCreationResult;
import com.adeem.stockflow.service.dto.batch.ProductImportRow;
import com.adeem.stockflow.service.mapper.ProductFamilyMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Batch ItemProcessor for converting ProductImportRow to ProductCreationResult.
 * Handles validation, product family resolution, and product creation.
 */
@Component
@StepScope
public class ProductImportProcessor implements ItemProcessor<ProductImportRow, ProductCreationResult> {

    private static final Logger LOG = LoggerFactory.getLogger(ProductImportProcessor.class);

    private final ProductService productService;
    private final InventoryService inventoryService;
    private final ProductRepository productRepository;
    private final ProductFamilyRepository productFamilyRepository;
    private final ClientAccountRepository clientAccountRepository;
    private final ProductFamilyMapper productFamilyMapper;

    @Value("#{jobParameters['clientAccountId']}")
    private Long clientAccountId;

    public ProductImportProcessor(
        ProductService productService,
        InventoryService inventoryService,
        ProductRepository productRepository,
        ProductFamilyRepository productFamilyRepository,
        ClientAccountRepository clientAccountRepository,
        ProductFamilyMapper productFamilyMapper
    ) {
        this.productService = productService;
        this.inventoryService = inventoryService;
        this.productRepository = productRepository;
        this.productFamilyRepository = productFamilyRepository;
        this.clientAccountRepository = clientAccountRepository;
        this.productFamilyMapper = productFamilyMapper;
    }

    @Override
    @Transactional
    public ProductCreationResult process(ProductImportRow item) throws Exception {
        LOG.debug("Processing import row {}: {}", item.getRowNumber(), item.getCode());

        // Validate the row
        List<ImportErrorDTO> validationErrors = validateImportRow(item);
        if (!validationErrors.isEmpty()) {
            return ProductCreationResult.failed(item.getRowNumber(), item.getDataRowNumber(), validationErrors);
        }

        try {
            // Create the product
            ProductDTO productDTO = createProductFromImportRow(item);
            ProductDTO savedProduct = productService.save(productDTO);

            // Create initial inventory
            if (item.getQuantity() != null && item.getQuantity().compareTo(BigDecimal.ZERO) >= 0) {
                createInitialInventory(savedProduct, item.getQuantity());
            }

            return ProductCreationResult.success(
                item.getRowNumber(),
                item.getDataRowNumber(),
                savedProduct.getId(),
                savedProduct.getCode()
            );
        } catch (Exception e) {
            LOG.error("Error processing import row {}: {}", item.getRowNumber(), e.getMessage(), e);

            ImportErrorDTO error = new ImportErrorDTO(
                item.getRowNumber(),
                item.getDataRowNumber(),
                "general",
                null,
                ImportErrorType.SYSTEM,
                "System error: " + e.getMessage(),
                "Please check the data and try again"
            );

            return ProductCreationResult.failed(item.getRowNumber(), item.getDataRowNumber(), error);
        }
    }

    /**
     * Validate the import row data.
     */
    private List<ImportErrorDTO> validateImportRow(ProductImportRow item) {
        List<ImportErrorDTO> errors = new ArrayList<>();

        // Validate mandatory fields
        if (item.getCode() == null || item.getCode().trim().isEmpty()) {
            errors.add(createValidationError(item, "code", item.getCode(), "Product code is required", "Provide a unique product code"));
        }

        if (item.getName() == null || item.getName().trim().isEmpty()) {
            errors.add(createValidationError(item, "name", item.getName(), "Product name is required", "Provide a product name"));
        }

        if (item.getQuantity() == null) {
            errors.add(createValidationError(item, "quantity", null, "Initial quantity is required", "Provide the initial stock quantity"));
        } else if (item.getQuantity().compareTo(BigDecimal.ZERO) < 0) {
            errors.add(
                createValidationError(
                    item,
                    "quantity",
                    item.getQuantity().toString(),
                    "Quantity cannot be negative",
                    "Provide a positive quantity value"
                )
            );
        }

        // Validate product code uniqueness
        if (item.getCode() != null && !item.getCode().trim().isEmpty()) {
            boolean codeExists = productRepository
                .findOne(
                    ProductSpecification.withCode(item.getCode().trim()).and(ProductSpecification.withClientAccountId(clientAccountId))
                )
                .isPresent();

            if (codeExists) {
                errors.add(
                    createDuplicateError(item, "code", item.getCode().trim(), "Product code already exists", "Use a unique product code")
                );
            }
        }

        // Validate numeric fields
        if (item.getPrice() != null && item.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            errors.add(
                createValidationError(
                    item,
                    "price",
                    item.getPrice().toString(),
                    "Price cannot be negative",
                    "Provide a positive price value"
                )
            );
        }

        if (item.getMinimumStockLevel() != null && item.getMinimumStockLevel().compareTo(BigDecimal.ZERO) < 0) {
            errors.add(
                createValidationError(
                    item,
                    "minimumStockLevel",
                    item.getMinimumStockLevel().toString(),
                    "Minimum stock level cannot be negative",
                    "Provide a positive minimum stock level"
                )
            );
        }

        // Validate category
        if (item.getCategory() != null && !item.getCategory().trim().isEmpty()) {
            if (!isValidProductCategory(item.getCategory())) {
                errors.add(
                    createValidationError(
                        item,
                        "category",
                        item.getCategory(),
                        "Invalid product category",
                        "Use one of the supported categories: " + String.join(", ", getSupportedCategories())
                    )
                );
            }
        }

        return errors;
    }

    /**
     * Create ProductDTO from import row.
     */
    private ProductDTO createProductFromImportRow(ProductImportRow item) {
        ProductDTO productDTO = new ProductDTO();

        // Set mandatory fields
        productDTO.setCode(item.getCode().trim());
        productDTO.setName(item.getName().trim());
        productDTO.setClientAccountId(clientAccountId);
        productDTO.setApplyTva(item.getApplyTva() != null ? item.getApplyTva() : false);

        // Set optional fields
        if (item.getDescription() != null && !item.getDescription().trim().isEmpty()) {
            productDTO.setDescription(item.getDescription().trim());
        }

        if (item.getPrice() != null) {
            productDTO.setSellingPrice(item.getPrice());
        }

        if (item.getManufacturer() != null && !item.getManufacturer().trim().isEmpty()) {
            productDTO.setManufacturerCode(item.getManufacturer().trim());
        }

        if (item.getUpc() != null && !item.getUpc().trim().isEmpty()) {
            productDTO.setUpc(item.getUpc().trim());
        }

        if (item.getManufacturerCode() != null && !item.getManufacturerCode().trim().isEmpty()) {
            productDTO.setManufacturerCode(item.getManufacturerCode().trim());
        }

        if (item.getMinimumStockLevel() != null) {
            productDTO.setMinimumStockLevel(item.getMinimumStockLevel());
        } else {
            // Set default minimum stock level
            productDTO.setMinimumStockLevel(BigDecimal.ZERO);
        }

        // Set category
        if (item.getCategory() != null && !item.getCategory().trim().isEmpty()) {
            ProductCategory category = parseProductCategory(item.getCategory());
            productDTO.setCategory(category);
        } else {
            productDTO.setCategory(ProductCategory.MISC);
        }

        // Set visibility
        if (item.getIsVisibleToCustomers() != null) {
            productDTO.setIsVisibleToCustomers(item.getIsVisibleToCustomers());
        } else {
            productDTO.setIsVisibleToCustomers(true); // Default to visible
        }

        // Handle product family
        if (item.getFamily() != null && !item.getFamily().trim().isEmpty()) {
            ProductFamilyDTO familyDTO = resolveOrCreateProductFamily(item.getFamily().trim());
            productDTO.setProductFamily(familyDTO);
        }

        return productDTO;
    }

    /**
     * Create initial inventory for the product.
     */
    private void createInitialInventory(ProductDTO product, BigDecimal quantity) {
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setProduct(product);
        inventoryDTO.setQuantity(quantity);
        inventoryDTO.setAvailableQuantity(quantity);
        inventoryDTO.setStatus(quantity.compareTo(BigDecimal.ZERO) == 0 ? InventoryStatus.OUT_OF_STOCK : InventoryStatus.AVAILABLE);
        inventoryDTO.setClientAccountId(clientAccountId);

        inventoryService.create(inventoryDTO);
    }

    /**
     * Resolve existing product family or create new one.
     */
    private ProductFamilyDTO resolveOrCreateProductFamily(String familyName) {
        // Try to find existing family
        Optional<ProductFamily> existingFamily = productFamilyRepository
            .findByNameContainingIgnoreCaseAndClientAccountId(
                familyName,
                clientAccountId,
                org.springframework.data.domain.PageRequest.of(0, 1)
            )
            .stream()
            .findFirst();

        return existingFamily
            .map(productFamilyMapper::toDto)
            .orElseGet(() -> {
                // Create new family
                ProductFamily newFamily = new ProductFamily();
                newFamily.setName(familyName);

                // Set client account
                ClientAccount clientAccount = clientAccountRepository
                    .findById(clientAccountId)
                    .orElseThrow(() -> new IllegalStateException("Client account not found: " + clientAccountId));
                newFamily.setClientAccount(clientAccount);

                ProductFamily savedFamily = productFamilyRepository.save(newFamily);
                LOG.debug("Created new product family: {} for client account: {}", familyName, clientAccountId);

                return productFamilyMapper.toDto(savedFamily);
            });
    }

    /**
     * Check if the category string is valid.
     */
    private boolean isValidProductCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return false;
        }

        try {
            parseProductCategory(category);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parse product category from string.
     */
    private ProductCategory parseProductCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return ProductCategory.MISC;
        }

        String normalizedCategory = category.trim().toUpperCase().replace(" ", "_").replace("-", "_");

        try {
            return ProductCategory.valueOf(normalizedCategory);
        } catch (IllegalArgumentException e) {
            // Try some common mappings
            switch (normalizedCategory) {
                case "ELECTRONIC":
                case "ELECTRONIQUE":
                    return ProductCategory.ELECTRONICS;
                case "COMPUTER":
                case "ORDINATEUR":
                    return ProductCategory.COMPUTERS;
                case "MOBILE":
                case "PHONE":
                case "TELEPHONE":
                    return ProductCategory.MOBILE_PHONES;
                case "CLOTHES":
                case "CLOTHING":
                case "VETEMENT":
                    return ProductCategory.CLOTHING;
                case "FOOD":
                case "GROCERY":
                case "ALIMENTATION":
                    return ProductCategory.GROCERIES;
                case "BOOK":
                case "BOOKS":
                case "LIVRE":
                    return ProductCategory.BOOKS;
                case "TOY":
                case "TOYS":
                case "JOUET":
                    return ProductCategory.TOYS;
                default:
                    return ProductCategory.MISC;
            }
        }
    }

    /**
     * Get list of supported categories for error messages.
     */
    private List<String> getSupportedCategories() {
        return Arrays.stream(ProductCategory.values()).map(ProductCategory::name).toList();
    }

    /**
     * Helper method to create validation error.
     */
    private ImportErrorDTO createValidationError(
        ProductImportRow item,
        String fieldName,
        String fieldValue,
        String errorMessage,
        String suggestion
    ) {
        return new ImportErrorDTO(
            item.getRowNumber(),
            item.getDataRowNumber(),
            fieldName,
            fieldValue,
            ImportErrorType.VALIDATION,
            errorMessage,
            suggestion
        );
    }

    /**
     * Helper method to create duplicate error.
     */
    private ImportErrorDTO createDuplicateError(
        ProductImportRow item,
        String fieldName,
        String fieldValue,
        String errorMessage,
        String suggestion
    ) {
        return new ImportErrorDTO(
            item.getRowNumber(),
            item.getDataRowNumber(),
            fieldName,
            fieldValue,
            ImportErrorType.DUPLICATE,
            errorMessage,
            suggestion
        );
    }

    /**
     * Helper method to create business rule error.
     */
    private ImportErrorDTO createBusinessRuleError(
        ProductImportRow item,
        String fieldName,
        String fieldValue,
        String errorMessage,
        String suggestion
    ) {
        return new ImportErrorDTO(
            item.getRowNumber(),
            item.getDataRowNumber(),
            fieldName,
            fieldValue,
            ImportErrorType.BUSINESS_RULE,
            errorMessage,
            suggestion
        );
    }
}
