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
    @Mapping(target = "addressType", source = "addressType")
    @Mapping(target = "streetAddress", source = "streetAddress")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "postalCode", source = "postalCode")
    @Mapping(target = "country", source = "country")
    @Mapping(target = "isDefault", source = "isDefault")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    AddressDTO toDtoAddressId(Address address);

    default ClientAccount fromId(Long id) {
        if (id == null) {
            return null;
        }
        ClientAccount clientAccount = new ClientAccount();
        clientAccount.setId(id);
        return clientAccount;
    }
}
