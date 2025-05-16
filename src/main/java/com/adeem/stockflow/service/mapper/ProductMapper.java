package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.domain.ProductFamily;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.dto.ProductDTO;
import com.adeem.stockflow.service.dto.ProductFamilyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "clientAccount", source = "clientAccount", qualifiedByName = "clientAccountId")
    @Mapping(target = "productFamily", source = "productFamily", qualifiedByName = "productFamilyId")
    ProductDTO toDto(Product s);

    @Named("clientAccountId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClientAccountDTO toDtoClientAccountId(ClientAccount clientAccount);

    @Named("productFamilyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductFamilyDTO toDtoProductFamilyId(ProductFamily productFamily);
}
