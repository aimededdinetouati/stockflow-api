package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.Customer;
import com.adeem.stockflow.domain.CustomerClientAssociation;
import com.adeem.stockflow.service.dto.CustomerAssociationDTO;
import java.util.List;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CustomerClientAssociation} and its DTO {@link CustomerAssociationDTO}.
 */
@Mapper(componentModel = "spring", uses = { CustomerMapper.class, ClientAccountMapper.class })
public interface CustomerAssociationMapper extends EntityMapper<CustomerAssociationDTO, CustomerClientAssociation> {
    @Named("toDto")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "clientAccountId", source = "clientAccount.id")
    @Mapping(target = "clientAccountName", source = "clientAccount.companyName")
    @Mapping(target = "customerFirstName", source = "customer.firstName")
    @Mapping(target = "customerLastName", source = "customer.lastName")
    @Mapping(target = "customerPhone", source = "customer.phone")
    @Mapping(target = "customerEmail", source = "customer.user.email")
    CustomerAssociationDTO toDto(CustomerClientAssociation association);

    @Mapping(target = "customer", source = "customerId", qualifiedByName = "customerIdRef")
    @Mapping(target = "clientAccount", source = "clientAccountId", qualifiedByName = "clientAccountIdRef")
    CustomerClientAssociation toEntity(CustomerAssociationDTO associationDTO);

    @Named("customerIdRef")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    Customer toCustomerRef(Long id);

    @Named("clientAccountIdRef")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClientAccount toClientAccountRef(Long id);

    // Override the default collection mapping to use the named method
    @IterableMapping(qualifiedByName = "toDto")
    List<CustomerAssociationDTO> toDto(List<CustomerClientAssociation> entityList);

    default CustomerClientAssociation fromId(Long id) {
        if (id == null) {
            return null;
        }
        CustomerClientAssociation association = new CustomerClientAssociation();
        association.setId(id);
        return association;
    }

    /**
     * Map association for customer view (minimal client account info).
     */
    @Named("toDtoForCustomer")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "clientAccountId", source = "clientAccount.id")
    @Mapping(target = "clientAccountName", source = "clientAccount.companyName")
    @Mapping(target = "customerFirstName", ignore = true)
    @Mapping(target = "customerLastName", ignore = true)
    @Mapping(target = "customerPhone", ignore = true)
    @Mapping(target = "customerEmail", ignore = true)
    CustomerAssociationDTO toDtoForCustomer(CustomerClientAssociation association);

    /**
     * Map association for company view (full customer info).
     */
    @Named("toDtoForCompany")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "clientAccountId", source = "clientAccount.id")
    @Mapping(target = "clientAccountName", source = "clientAccount.companyName")
    @Mapping(target = "customerFirstName", source = "customer.firstName")
    @Mapping(target = "customerLastName", source = "customer.lastName")
    @Mapping(target = "customerPhone", source = "customer.phone")
    @Mapping(target = "customerEmail", source = "customer.user.email")
    CustomerAssociationDTO toDtoForCompany(CustomerClientAssociation association);

    /**
     * Map minimal association info (for lists).
     */
    @Named("toMinimalDto")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "clientAccountId", source = "clientAccount.id")
    @Mapping(target = "clientAccountName", source = "clientAccount.companyName")
    @Mapping(target = "customerFirstName", source = "customer.firstName")
    @Mapping(target = "customerLastName", source = "customer.lastName")
    @Mapping(target = "customerPhone", ignore = true)
    @Mapping(target = "customerEmail", ignore = true)
    @Mapping(target = "notes", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    CustomerAssociationDTO toMinimalDto(CustomerClientAssociation association);

    // Additional collection mapping methods for specific use cases
    @IterableMapping(qualifiedByName = "toDtoForCustomer")
    List<CustomerAssociationDTO> toDtoForCustomer(List<CustomerClientAssociation> entityList);

    @IterableMapping(qualifiedByName = "toDtoForCompany")
    List<CustomerAssociationDTO> toDtoForCompany(List<CustomerClientAssociation> entityList);

    @IterableMapping(qualifiedByName = "toMinimalDto")
    List<CustomerAssociationDTO> toMinimalDto(List<CustomerClientAssociation> entityList);
}
