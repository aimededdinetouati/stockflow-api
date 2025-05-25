package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.ProductFamily;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.dto.ProductFamilyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductFamily} and its DTO {@link ProductFamilyDTO}.
 */
@Mapper(componentModel = "spring", uses = { ClientAccountMapper.class })
public interface ProductFamilyMapper extends EntityMapper<ProductFamilyDTO, ProductFamily> {
    @Mapping(target = "clientAccountId", source = "clientAccount.id")
    ProductFamilyDTO toDto(ProductFamily s);

    @Mapping(target = "clientAccount", source = "clientAccountId")
    ProductFamily toEntity(ProductFamilyDTO productFamilyDTO);
}
