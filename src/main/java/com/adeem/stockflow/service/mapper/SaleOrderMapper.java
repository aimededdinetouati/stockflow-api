package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.Customer;
import com.adeem.stockflow.domain.Payment;
import com.adeem.stockflow.domain.SaleOrder;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.dto.CustomerDTO;
import com.adeem.stockflow.service.dto.PaymentDTO;
import com.adeem.stockflow.service.dto.SaleOrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SaleOrder} and its DTO {@link SaleOrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface SaleOrderMapper extends EntityMapper<SaleOrderDTO, SaleOrder> {
    @Mapping(target = "payment", source = "payment", qualifiedByName = "paymentId")
    @Mapping(target = "clientAccount", source = "clientAccount", qualifiedByName = "clientAccountId")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerId")
    SaleOrderDTO toDto(SaleOrder s);

    @Named("paymentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PaymentDTO toDtoPaymentId(Payment payment);

    @Named("clientAccountId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClientAccountDTO toDtoClientAccountId(ClientAccount clientAccount);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);
}
