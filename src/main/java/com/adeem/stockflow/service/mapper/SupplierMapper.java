package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Address;
import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.Supplier;
import com.adeem.stockflow.service.dto.AddressDTO;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.dto.SupplierDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Supplier} and its DTO {@link SupplierDTO}.
 */
@Mapper(componentModel = "spring")
public interface SupplierMapper extends EntityMapper<SupplierDTO, Supplier> {
    @Mapping(target = "address", source = "address", qualifiedByName = "addressId")
    @Mapping(target = "clientAccount", source = "clientAccount", qualifiedByName = "clientAccountId")
    SupplierDTO toDto(Supplier s);

    @Named("addressId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AddressDTO toDtoAddressId(Address address);

    @Named("clientAccountId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClientAccountDTO toDtoClientAccountId(ClientAccount clientAccount);
}
