package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.ProductFamily;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.dto.ProductFamilyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductFamily} and its DTO {@link ProductFamilyDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductFamilyMapper extends EntityMapper<ProductFamilyDTO, ProductFamily> {
    @Mapping(target = "clientAccount", source = "clientAccount", qualifiedByName = "clientAccountId")
    ProductFamilyDTO toDto(ProductFamily s);

    @Named("clientAccountId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClientAccountDTO toDtoClientAccountId(ClientAccount clientAccount);
}
