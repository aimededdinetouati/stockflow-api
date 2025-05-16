package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.domain.PurchaseOrderItem;
import com.adeem.stockflow.domain.ReturnOrder;
import com.adeem.stockflow.domain.ReturnOrderItem;
import com.adeem.stockflow.domain.SaleOrderItem;
import com.adeem.stockflow.service.dto.ProductDTO;
import com.adeem.stockflow.service.dto.PurchaseOrderItemDTO;
import com.adeem.stockflow.service.dto.ReturnOrderDTO;
import com.adeem.stockflow.service.dto.ReturnOrderItemDTO;
import com.adeem.stockflow.service.dto.SaleOrderItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ReturnOrderItem} and its DTO {@link ReturnOrderItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReturnOrderItemMapper extends EntityMapper<ReturnOrderItemDTO, ReturnOrderItem> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    @Mapping(target = "originalSaleOrderItem", source = "originalSaleOrderItem", qualifiedByName = "saleOrderItemId")
    @Mapping(target = "originalPurchaseOrderItem", source = "originalPurchaseOrderItem", qualifiedByName = "purchaseOrderItemId")
    @Mapping(target = "returnOrderId", source = "returnOrder.id")
    ReturnOrderItemDTO toDto(ReturnOrderItem s);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);

    @Named("saleOrderItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SaleOrderItemDTO toDtoSaleOrderItemId(SaleOrderItem saleOrderItem);

    @Named("purchaseOrderItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PurchaseOrderItemDTO toDtoPurchaseOrderItemId(PurchaseOrderItem purchaseOrderItem);

    @Named("returnOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ReturnOrderDTO toDtoReturnOrderId(ReturnOrder returnOrder);
}
