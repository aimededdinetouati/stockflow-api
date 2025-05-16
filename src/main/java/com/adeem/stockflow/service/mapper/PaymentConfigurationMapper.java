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
    @Mapping(target = "clientAccount", source = "clientAccount", qualifiedByName = "clientAccountId")
    PaymentConfigurationDTO toDto(PaymentConfiguration s);

    @Named("clientAccountId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClientAccountDTO toDtoClientAccountId(ClientAccount clientAccount);
}
