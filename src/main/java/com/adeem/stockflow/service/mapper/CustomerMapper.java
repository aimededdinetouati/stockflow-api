package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.Customer;
import com.adeem.stockflow.domain.User;
import com.adeem.stockflow.service.dto.CustomerDTO;
import java.util.List;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Customer} and its DTO {@link CustomerDTO}.
 */
@Mapper(componentModel = "spring", uses = { ClientAccountMapper.class, UserMapper.class, CustomerAssociationMapper.class })
public interface CustomerMapper extends EntityMapper<CustomerDTO, Customer> {
    @Named("toDto")
    @Mapping(target = "createdByClientAccountId", source = "createdByClientAccount.id")
    @Mapping(target = "createdByClientAccountName", source = "createdByClientAccount.companyName")
    @Mapping(target = "hasUserAccount", expression = "java(customer.getUser() != null)")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "fullName", expression = "java(getFullName(customer))")
    @Mapping(target = "isManaged", expression = "java(isManaged(customer))")
    @Mapping(target = "isIndependent", expression = "java(isIndependent(customer))")
    @Mapping(target = "associations", ignore = true) // Loaded separately when needed
    CustomerDTO toDto(Customer customer);

    @Mapping(target = "createdByClientAccount", source = "createdByClientAccountId", qualifiedByName = "clientAccountRef")
    @Mapping(target = "user", ignore = true) // User is handled separately
    @Mapping(target = "associations", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    Customer toEntity(CustomerDTO customerDTO);

    @Named("clientAccountRef")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClientAccount toClientAccountRef(Long id);

    // Override the default collection mapping to use the named method
    @IterableMapping(qualifiedByName = "toDto")
    List<CustomerDTO> toDto(List<Customer> entityList);

    default Customer fromId(Long id) {
        if (id == null) {
            return null;
        }
        Customer customer = new Customer();
        customer.setId(id);
        return customer;
    }

    /**
     * Get full name from customer entity.
     */
    default String getFullName(Customer customer) {
        if (customer.getFirstName() != null && customer.getLastName() != null) {
            return customer.getFirstName() + " " + customer.getLastName();
        }
        return null;
    }

    /**
     * Check if customer is managed (created by company, no user account).
     */
    default Boolean isManaged(Customer customer) {
        return customer.getCreatedByClientAccount() != null && customer.getUser() == null;
    }

    /**
     * Check if customer is independent (has user account).
     */
    default Boolean isIndependent(Customer customer) {
        return customer.getUser() != null;
    }

    /**
     * Map minimal customer info (for association DTOs).
     */
    @Named("toMinimalDto")
    @Mapping(target = "createdByClientAccountId", ignore = true)
    @Mapping(target = "createdByClientAccountName", ignore = true)
    @Mapping(target = "hasUserAccount", ignore = true)
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "fullName", expression = "java(getFullName(customer))")
    @Mapping(target = "isManaged", ignore = true)
    @Mapping(target = "isIndependent", ignore = true)
    @Mapping(target = "associations", ignore = true)
    @Mapping(target = "fax", ignore = true)
    @Mapping(target = "taxId", ignore = true)
    @Mapping(target = "registrationArticle", ignore = true)
    @Mapping(target = "statisticalId", ignore = true)
    @Mapping(target = "rc", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    CustomerDTO toMinimalDto(Customer customer);

    @IterableMapping(qualifiedByName = "toMinimalDto")
    List<CustomerDTO> toMinimalDto(List<Customer> entityList);
}
