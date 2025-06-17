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
@Mapper(componentModel = "spring", uses = { ClientAccountMapper.class })
public interface InventoryTransactionMapper extends EntityMapper<InventoryTransactionDTO, InventoryTransaction> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    @Mapping(target = "inventory", source = "inventory", qualifiedByName = "inventoryId")
    @Mapping(target = "clientAccountId", source = "clientAccount.id")
    InventoryTransactionDTO toDto(InventoryTransaction s);

    @Mapping(target = "clientAccount", source = "clientAccountId")
    InventoryTransaction toEntity(InventoryTransactionDTO inventoryTransactionDTO);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);

    @Named("inventoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InventoryDTO toDtoInventoryId(Inventory inventory);
}
