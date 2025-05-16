package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Admin;
import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.Customer;
import com.adeem.stockflow.domain.PurchaseOrder;
import com.adeem.stockflow.domain.ReturnOrder;
import com.adeem.stockflow.domain.SaleOrder;
import com.adeem.stockflow.domain.Supplier;
import com.adeem.stockflow.service.dto.AdminDTO;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.dto.CustomerDTO;
import com.adeem.stockflow.service.dto.PurchaseOrderDTO;
import com.adeem.stockflow.service.dto.ReturnOrderDTO;
import com.adeem.stockflow.service.dto.SaleOrderDTO;
import com.adeem.stockflow.service.dto.SupplierDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ReturnOrder} and its DTO {@link ReturnOrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReturnOrderMapper extends EntityMapper<ReturnOrderDTO, ReturnOrder> {
    @Mapping(target = "clientAccountId", source = "clientAccount.id")
    @Mapping(target = "processedBy", source = "processedBy", qualifiedByName = "adminId")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerId")
    @Mapping(target = "supplier", source = "supplier", qualifiedByName = "supplierId")
    @Mapping(target = "originalSaleOrder", source = "originalSaleOrder", qualifiedByName = "saleOrderId")
    @Mapping(target = "originalPurchaseOrder", source = "originalPurchaseOrder", qualifiedByName = "purchaseOrderId")
    ReturnOrderDTO toDto(ReturnOrder s);

    @Named("adminId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AdminDTO toDtoAdminId(Admin admin);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);

    @Named("supplierId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SupplierDTO toDtoSupplierId(Supplier supplier);

    @Named("saleOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SaleOrderDTO toDtoSaleOrderId(SaleOrder saleOrder);

    @Named("purchaseOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PurchaseOrderDTO toDtoPurchaseOrderId(PurchaseOrder purchaseOrder);
}
