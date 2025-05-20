package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.PaymentConfiguration;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.dto.PaymentConfigurationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PaymentConfiguration} and its DTO {@link PaymentConfigurationDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentConfigurationMapper extends EntityMapper<PaymentConfigurationDTO, PaymentConfiguration> {
    @Mapping(target = "clientAccountId", source = "clientAccount.id")
    PaymentConfigurationDTO toDto(PaymentConfiguration s);

    default PaymentConfiguration fromId(Long id) {
        if (id == null) {
            return null;
        }
        PaymentConfiguration paymentConfiguration = new PaymentConfiguration();
        paymentConfiguration.setId(id);
        return paymentConfiguration;
    }
}
