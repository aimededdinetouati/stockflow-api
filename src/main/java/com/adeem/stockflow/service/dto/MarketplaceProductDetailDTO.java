// File: src/main/java/com/adeem/stockflow/service/dto/MarketplaceProductDetailDTO.java
package com.adeem.stockflow.service.dto;

import com.adeem.stockflow.domain.enumeration.ProductCategory;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Detailed DTO for marketplace product detail pages.
 * Contains comprehensive product information including company profile and inventory details.
 */
public class MarketplaceProductDetailDTO implements Serializable {

    private Long id;
    private String name;
    private String description;
    private String code;
    private BigDecimal sellingPrice;
    private ProductCategory category;
    private String categoryDisplayName;
    private Boolean isVisibleToCustomers;
    private Boolean isAvailable;

    // Inventory information
    private BigDecimal availableQuantity;
    private BigDecimal minimumStockLevel;
    private Boolean isLowStock;

    // Company information
    private CompanyProfileDTO company;

    // Media
    private List<String> imageUrls;

    // Related products (same category or company)
    private List<MarketplaceProductDTO> relatedProducts;

    public MarketplaceProductDetailDTO() {
        // Empty constructor needed for Jackson.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
        this.categoryDisplayName = category != null ? category.name() : null;
    }

    public String getCategoryDisplayName() {
        return categoryDisplayName;
    }

    public void setCategoryDisplayName(String categoryDisplayName) {
        this.categoryDisplayName = categoryDisplayName;
    }

    public Boolean getIsVisibleToCustomers() {
        return isVisibleToCustomers;
    }

    public void setIsVisibleToCustomers(Boolean isVisibleToCustomers) {
        this.isVisibleToCustomers = isVisibleToCustomers;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(BigDecimal availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public BigDecimal getMinimumStockLevel() {
        return minimumStockLevel;
    }

    public void setMinimumStockLevel(BigDecimal minimumStockLevel) {
        this.minimumStockLevel = minimumStockLevel;
    }

    public Boolean getIsLowStock() {
        return isLowStock;
    }

    public void setIsLowStock(Boolean isLowStock) {
        this.isLowStock = isLowStock;
    }

    public CompanyProfileDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyProfileDTO company) {
        this.company = company;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<MarketplaceProductDTO> getRelatedProducts() {
        return relatedProducts;
    }

    public void setRelatedProducts(List<MarketplaceProductDTO> relatedProducts) {
        this.relatedProducts = relatedProducts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MarketplaceProductDetailDTO)) {
            return false;
        }
        MarketplaceProductDetailDTO that = (MarketplaceProductDetailDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return (
            "MarketplaceProductDetailDTO{" +
            "id=" +
            id +
            ", name='" +
            name +
            '\'' +
            ", code='" +
            code +
            '\'' +
            ", sellingPrice=" +
            sellingPrice +
            ", category=" +
            category +
            ", isAvailable=" +
            isAvailable +
            ", availableQuantity=" +
            availableQuantity +
            '}'
        );
    }

    /**
     * Nested DTO for company profile information in marketplace context.
     * Excludes sensitive business information.
     */
    public static class CompanyProfileDTO implements Serializable {

        private Long id;
        private String companyName;
        private String phone;
        private String city;
        private String country;
        private String contactEmail;

        public CompanyProfileDTO() {
            // Empty constructor needed for Jackson.
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getContactEmail() {
            return contactEmail;
        }

        public void setContactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CompanyProfileDTO)) return false;
            CompanyProfileDTO that = (CompanyProfileDTO) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return (
                "CompanyProfileDTO{" +
                "id=" +
                id +
                ", companyName='" +
                companyName +
                '\'' +
                ", city='" +
                city +
                '\'' +
                ", country='" +
                country +
                '\'' +
                '}'
            );
        }
    }
}
