package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.Supplier;
import com.adeem.stockflow.service.dto.SupplierDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Supplier} and its DTO {@link SupplierDTO}.
 */
@Mapper(componentModel = "spring", uses = { AddressMapper.class })
public interface SupplierMapper extends EntityMapper<SupplierDTO, Supplier> {
    @Mapping(source = "clientAccount.id", target = "clientAccountId")
    @Mapping(source = "address", target = "address")
    SupplierDTO toDto(Supplier supplier);

    @Mapping(source = "clientAccountId", target = "clientAccount.id")
    @Mapping(source = "address", target = "address")
    Supplier toEntity(SupplierDTO supplierDTO);

    /**
     * Map client account ID to ClientAccount entity.
     */
    default ClientAccount map(Long clientAccountId) {
        if (clientAccountId == null) {
            return null;
        }
        ClientAccount clientAccount = new ClientAccount();
        clientAccount.setId(clientAccountId);
        return clientAccount;
    }

    /**
     * Map ClientAccount entity to ID.
     */
    default Long map(ClientAccount clientAccount) {
        return clientAccount != null ? clientAccount.getId() : null;
    }

    default Supplier fromId(Long id) {
        if (id == null) {
            return null;
        }
        Supplier supplier = new Supplier();
        supplier.setId(id);
        return supplier;
    }
}
