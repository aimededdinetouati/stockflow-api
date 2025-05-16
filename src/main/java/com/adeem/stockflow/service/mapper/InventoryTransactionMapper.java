package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.InventoryTransaction;
import com.adeem.stockflow.domain.PurchaseOrder;
import com.adeem.stockflow.domain.ReturnOrderItem;
import com.adeem.stockflow.domain.SaleOrder;
import com.adeem.stockflow.service.dto.InventoryTransactionDTO;
import com.adeem.stockflow.service.dto.PurchaseOrderDTO;
import com.adeem.stockflow.service.dto.ReturnOrderItemDTO;
import com.adeem.stockflow.service.dto.SaleOrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InventoryTransaction} and its DTO {@link InventoryTransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface InventoryTransactionMapper extends EntityMapper<InventoryTransactionDTO, InventoryTransaction> {
    @Mapping(target = "returnItemId", source = "returnOrderItem.id")
    @Mapping(target = "saleItemId", source = "saleOrderItem.id")
    @Mapping(target = "purchaseItemId", source = "purchaseOrderItem.id")
    InventoryTransactionDTO toDto(InventoryTransaction s);
}
