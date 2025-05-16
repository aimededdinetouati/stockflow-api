package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.InventoryTransaction;
import com.adeem.stockflow.domain.ReturnOrderItem;
import com.adeem.stockflow.service.dto.InventoryTransactionDTO;
import com.adeem.stockflow.service.dto.ReturnOrderItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InventoryTransaction} and its DTO {@link InventoryTransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface InventoryTransactionMapper extends EntityMapper<InventoryTransactionDTO, InventoryTransaction> {
    @Mapping(target = "returnOrderItem", source = "returnOrderItem", qualifiedByName = "returnOrderItemId")
    InventoryTransactionDTO toDto(InventoryTransaction s);

    @Named("returnOrderItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ReturnOrderItemDTO toDtoReturnOrderItemId(ReturnOrderItem returnOrderItem);
}
