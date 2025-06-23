package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.*;
import com.adeem.stockflow.service.dto.*;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SaleOrder} and its DTO {@link SaleOrderDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProductMapper.class })
public interface SaleOrderMapper extends EntityMapper<SaleOrderDTO, SaleOrder> {
    @Mapping(target = "payment", source = "payment", qualifiedByName = "paymentId")
    @Mapping(target = "clientAccountId", source = "clientAccount.id")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerId")
    @Mapping(target = "orderItems", source = "orderItems", qualifiedByName = "toOrderItemDto")
    SaleOrderDTO toDto(SaleOrder s);

    @Mapping(target = "orderItems", source = "orderItems", qualifiedByName = "toOrderItem")
    SaleOrder toEntity(SaleOrderDTO s);

    @Named("paymentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PaymentDTO toDtoPaymentId(Payment payment);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);

    @Named("toOrderItemDto")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "saleOrderId", source = "saleOrder.id")
    SaleOrderItemDTO toOrderItemDto(SaleOrderItem orderItem);

    @Named("toOrderItem")
    @Mapping(target = "product", source = "product")
    SaleOrderItem toOrderItem(SaleOrderItemDTO orderItemDTO);
}
