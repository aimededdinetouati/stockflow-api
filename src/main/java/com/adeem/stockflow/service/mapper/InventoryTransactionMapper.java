package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.InventoryTransaction;
import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.service.dto.InventoryTransactionDTO;
import com.adeem.stockflow.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InventoryTransaction} and its DTO {@link InventoryTransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface InventoryTransactionMapper extends EntityMapper<InventoryTransactionDTO, InventoryTransaction> {
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    InventoryTransactionDTO toDto(InventoryTransaction s);

    default InventoryTransaction fromId(Long id) {
        if (id == null) {
            return null;
        }
        InventoryTransaction inventoryTransaction = new InventoryTransaction();
        inventoryTransaction.setId(id);
        return inventoryTransaction;
    }
}
