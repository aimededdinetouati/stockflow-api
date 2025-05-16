package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.domain.SaleOrder;
import com.adeem.stockflow.domain.SaleOrderItem;
import com.adeem.stockflow.service.dto.ProductDTO;
import com.adeem.stockflow.service.dto.SaleOrderDTO;
import com.adeem.stockflow.service.dto.SaleOrderItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SaleOrderItem} and its DTO {@link SaleOrderItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface SaleOrderItemMapper extends EntityMapper<SaleOrderItemDTO, SaleOrderItem> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    @Mapping(target = "saleOrderId", source = "saleOrder.id")
    SaleOrderItemDTO toDto(SaleOrderItem s);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);
}
