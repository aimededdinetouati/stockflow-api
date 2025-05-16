package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.domain.PurchaseOrder;
import com.adeem.stockflow.domain.PurchaseOrderItem;
import com.adeem.stockflow.service.dto.ProductDTO;
import com.adeem.stockflow.service.dto.PurchaseOrderDTO;
import com.adeem.stockflow.service.dto.PurchaseOrderItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PurchaseOrderItem} and its DTO {@link PurchaseOrderItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface PurchaseOrderItemMapper extends EntityMapper<PurchaseOrderItemDTO, PurchaseOrderItem> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    @Mapping(target = "purchaseOrderId", source = "purchaseOrder.id")
    PurchaseOrderItemDTO toDto(PurchaseOrderItem s);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);

    @Named("purchaseOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PurchaseOrderDTO toDtoPurchaseOrderId(PurchaseOrder purchaseOrder);
}
