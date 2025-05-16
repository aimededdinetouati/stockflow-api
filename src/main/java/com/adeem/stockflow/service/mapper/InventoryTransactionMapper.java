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
    @Mapping(target = "returnOrderItem", source = "returnOrderItem", qualifiedByName = "returnOrderItemId")
    @Mapping(target = "saleOrderItem", source = "saleOrderItem", qualifiedByName = "saleOrderId")
    @Mapping(target = "purchaseOrderItem", source = "purchaseOrderItem", qualifiedByName = "purchaseOrderId")
    InventoryTransactionDTO toDto(InventoryTransaction s);

    @Named("returnOrderItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ReturnOrderItemDTO toDtoReturnOrderItemId(ReturnOrderItem returnOrderItem);

    @Named("saleOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SaleOrderDTO toDtoSaleOrderId(SaleOrder saleOrder);

    @Named("purchaseOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PurchaseOrderDTO toDtoPurchaseOrderId(PurchaseOrder purchaseOrder);
}
