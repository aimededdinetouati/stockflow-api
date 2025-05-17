package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Address;
import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.Quota;
import com.adeem.stockflow.service.dto.AddressDTO;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.dto.QuotaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ClientAccount} and its DTO {@link ClientAccountDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClientAccountMapper extends EntityMapper<ClientAccountDTO, ClientAccount> {
    @Mapping(target = "quota", source = "quota", qualifiedByName = "quotaId")
    @Mapping(target = "address", source = "address", qualifiedByName = "addressId")
    ClientAccountDTO toDto(ClientAccount s);

    @Named("quotaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    QuotaDTO toDtoQuotaId(Quota quota);

    @Named("addressId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AddressDTO toDtoAddressId(Address address);
}
