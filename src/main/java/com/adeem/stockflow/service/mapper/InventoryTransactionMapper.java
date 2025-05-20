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
    InventoryTransactionDTO toDto(InventoryTransaction s);
}
