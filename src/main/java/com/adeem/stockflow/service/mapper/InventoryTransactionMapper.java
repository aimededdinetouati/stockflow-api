package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Inventory;
import com.adeem.stockflow.domain.InventoryTransaction;
import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.service.dto.InventoryDTO;
import com.adeem.stockflow.service.dto.InventoryTransactionDTO;
import com.adeem.stockflow.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InventoryTransaction} and its DTO {@link InventoryTransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface InventoryTransactionMapper extends EntityMapper<InventoryTransactionDTO, InventoryTransaction> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    @Mapping(target = "inventory", source = "inventory", qualifiedByName = "inventoryId")
    InventoryTransactionDTO toDto(InventoryTransaction s);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);

    @Named("inventoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InventoryDTO toDtoInventoryId(Inventory inventory);
}
