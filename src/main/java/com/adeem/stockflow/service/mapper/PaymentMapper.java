package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.Customer;
import com.adeem.stockflow.domain.Payment;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.dto.CustomerDTO;
import com.adeem.stockflow.service.dto.PaymentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Payment} and its DTO {@link PaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper extends EntityMapper<PaymentDTO, Payment> {
    @Mapping(target = "clientAccount", source = "clientAccount", qualifiedByName = "clientAccountId")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerId")
    PaymentDTO toDto(Payment s);

    @Named("clientAccountId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClientAccountDTO toDtoClientAccountId(ClientAccount clientAccount);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);
}
