package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Inventory;
import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.service.dto.InventoryDTO;
import com.adeem.stockflow.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Inventory} and its DTO {@link InventoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface InventoryMapper extends EntityMapper<InventoryDTO, Inventory> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    InventoryDTO toDto(Inventory s);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);
}
