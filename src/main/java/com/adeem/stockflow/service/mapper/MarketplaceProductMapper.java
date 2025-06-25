// File: src/main/java/com/adeem/stockflow/service/mapper/MarketplaceProductMapper.java
package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.service.dto.MarketplaceProductDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its marketplace DTO {@link MarketplaceProductDTO}.
 * This mapper specifically converts internal Product entities to marketplace-safe DTOs
 * that hide sensitive business information.
 */
@Mapper(componentModel = "spring")
public interface MarketplaceProductMapper extends EntityMapper<MarketplaceProductDTO, Product> {
    @Mapping(target = "companyName", source = "clientAccount.companyName")
    @Mapping(target = "companyLocation", expression = "java(getCompanyLocation(product))")
    @Mapping(target = "availableQuantity", expression = "java(getAvailableQuantity(product))")
    @Mapping(target = "imageUrls", expression = "java(getImageUrls(product))")
    @Mapping(target = "isAvailable", expression = "java(getIsAvailable(product))")
    MarketplaceProductDTO toDto(Product product);

    @Named("getCompanyLocation")
    default String getCompanyLocation(Product product) {
        if (product.getClientAccount() != null && product.getClientAccount().getAddress() != null) {
            return product.getClientAccount().getAddress().getCity() + ", " + product.getClientAccount().getAddress().getCountry();
        }
        return null;
    }

    @Named("getAvailableQuantity")
    default BigDecimal getAvailableQuantity(Product product) {
        if (product.getInventories() != null && !product.getInventories().isEmpty()) {
            return product
                .getInventories()
                .stream()
                .map(inventory -> inventory.getAvailableQuantity() != null ? inventory.getAvailableQuantity() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return BigDecimal.ZERO;
    }

    @Named("getImageUrls")
    default List<String> getImageUrls(Product product) {
        if (product.getImages() != null) {
            return product
                .getImages()
                .stream()
                .map(attachment -> "/api/attachments/" + attachment.getId() + "/download")
                .collect(Collectors.toList());
        }
        return List.of();
    }

    @Named("getIsAvailable")
    default Boolean getIsAvailable(Product product) {
        return (
            product.getIsVisibleToCustomers() != null &&
            product.getIsVisibleToCustomers() &&
            getAvailableQuantity(product).compareTo(BigDecimal.ZERO) > 0
        );
    }

    // Don't implement toEntity as this is a read-only marketplace DTO
    default Product toEntity(MarketplaceProductDTO dto) {
        throw new UnsupportedOperationException("Marketplace DTO should not be converted back to entity");
    }

    default Product fromId(Long id) {
        if (id == null) {
            return null;
        }
        Product product = new Product();
        product.setId(id);
        return product;
    }
}
