// File: src/main/java/com/adeem/stockflow/service/dto/MarketplaceProductDTO.java
package com.adeem.stockflow.service.dto;

import com.adeem.stockflow.domain.enumeration.ProductCategory;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * A marketplace-focused DTO for the {@link com.adeem.stockflow.domain.Product} entity.
 * Exposes only marketplace-relevant product information.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MarketplaceProductDTO implements Serializable {

    private Long id;
    private String name;
    private String description;
    private String code;
    private BigDecimal sellingPrice;
    private ProductCategory category;
    private String companyName;
    private String companyLocation;
    private BigDecimal availableQuantity;
    private List<String> imageUrls;
    private Boolean isVisibleToCustomers;
    private Boolean isAvailable;

    public MarketplaceProductDTO() {
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
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLocation() {
        return companyLocation;
    }

    public void setCompanyLocation(String companyLocation) {
        this.companyLocation = companyLocation;
    }

    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(BigDecimal availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MarketplaceProductDTO)) {
            return false;
        }

        MarketplaceProductDTO productDTO = (MarketplaceProductDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MarketplaceProductDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", code='" + getCode() + "'" +
            ", sellingPrice=" + getSellingPrice() +
            ", category='" + getCategory() + "'" +
            ", companyName='" + getCompanyName() + "'" +
            ", companyLocation='" + getCompanyLocation() + "'" +
            ", availableQuantity=" + getAvailableQuantity() +
            ", isVisibleToCustomers=" + getIsVisibleToCustomers() +
            ", isAvailable=" + getIsAvailable() +
            "}";
    }
}
