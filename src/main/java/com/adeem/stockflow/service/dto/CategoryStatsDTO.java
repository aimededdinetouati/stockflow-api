package com.adeem.stockflow.service.dto;

import com.adeem.stockflow.domain.enumeration.ProductCategory;
import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for category statistics in the marketplace.
 * Contains category information with product counts for navigation and filtering.
 */
public class CategoryStatsDTO implements Serializable {

    private ProductCategory category;
    private String categoryDisplayName;
    private Long productCount;
    private Long availableProductCount;

    public CategoryStatsDTO() {
        // Empty constructor needed for Jackson.
    }

    public CategoryStatsDTO(ProductCategory category, Long productCount, Long availableProductCount) {
        this.category = category;
        this.categoryDisplayName = category != null ? category.name() : "UNCATEGORIZED";
        this.productCount = productCount;
        this.availableProductCount = availableProductCount;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
        this.categoryDisplayName = category != null ? category.name() : "UNCATEGORIZED";
    }

    public String getCategoryDisplayName() {
        return categoryDisplayName;
    }

    public void setCategoryDisplayName(String categoryDisplayName) {
        this.categoryDisplayName = categoryDisplayName;
    }

    public Long getProductCount() {
        return productCount;
    }

    public void setProductCount(Long productCount) {
        this.productCount = productCount;
    }

    public Long getAvailableProductCount() {
        return availableProductCount;
    }

    public void setAvailableProductCount(Long availableProductCount) {
        this.availableProductCount = availableProductCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CategoryStatsDTO)) {
            return false;
        }
        CategoryStatsDTO that = (CategoryStatsDTO) o;
        return Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category);
    }

    @Override
    public String toString() {
        return (
            "CategoryStatsDTO{" +
            "category=" +
            category +
            ", categoryDisplayName='" +
            categoryDisplayName +
            '\'' +
            ", productCount=" +
            productCount +
            ", availableProductCount=" +
            availableProductCount +
            '}'
        );
    }
}
