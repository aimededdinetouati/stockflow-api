package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Address;
import com.adeem.stockflow.domain.Customer;
import com.adeem.stockflow.service.dto.AddressDTO;
import com.adeem.stockflow.service.dto.CustomerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Address} and its DTO {@link AddressDTO}.
 */
@Mapper(componentModel = "spring")
public interface AddressMapper extends EntityMapper<AddressDTO, Address> {
    AddressDTO toDto(Address s);
}
