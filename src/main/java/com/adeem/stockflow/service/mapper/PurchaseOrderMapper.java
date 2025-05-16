package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Admin;
import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.PurchaseOrder;
import com.adeem.stockflow.domain.Supplier;
import com.adeem.stockflow.service.dto.AdminDTO;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.dto.PurchaseOrderDTO;
import com.adeem.stockflow.service.dto.SupplierDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PurchaseOrder} and its DTO {@link PurchaseOrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper extends EntityMapper<PurchaseOrderDTO, PurchaseOrder> {
    @Mapping(target = "clientAccount", source = "clientAccount", qualifiedByName = "clientAccountId")
    @Mapping(target = "admin", source = "admin", qualifiedByName = "adminId")
    @Mapping(target = "supplier", source = "supplier", qualifiedByName = "supplierId")
    PurchaseOrderDTO toDto(PurchaseOrder s);

    @Named("clientAccountId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClientAccountDTO toDtoClientAccountId(ClientAccount clientAccount);

    @Named("adminId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AdminDTO toDtoAdminId(Admin admin);

    @Named("supplierId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SupplierDTO toDtoSupplierId(Supplier supplier);
}
