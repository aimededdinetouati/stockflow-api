package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Address;
import com.adeem.stockflow.domain.SaleOrder;
import com.adeem.stockflow.domain.Shipment;
import com.adeem.stockflow.service.dto.AddressDTO;
import com.adeem.stockflow.service.dto.SaleOrderDTO;
import com.adeem.stockflow.service.dto.ShipmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Shipment} and its DTO {@link ShipmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShipmentMapper extends EntityMapper<ShipmentDTO, Shipment> {
    @Mapping(target = "saleOrder", source = "saleOrder", qualifiedByName = "saleOrderId")
    @Mapping(target = "address", source = "address", qualifiedByName = "addressId")
    ShipmentDTO toDto(Shipment s);

    @Named("saleOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SaleOrderDTO toDtoSaleOrderId(SaleOrder saleOrder);

    @Named("addressId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AddressDTO toDtoAddressId(Address address);
}
