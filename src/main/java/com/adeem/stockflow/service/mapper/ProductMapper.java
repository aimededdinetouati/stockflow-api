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
@Mapper(componentModel = "spring", uses = { InventoryMapper.class })
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "clientAccountId", source = "clientAccount.id")
    @Mapping(target = "productFamily", source = "productFamily", qualifiedByName = "productFamilyId")
    @Mapping(target = "inventories", source = "inventories")
    ProductDTO toDto(Product s);

    @Named("productFamilyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductFamilyDTO toDtoProductFamilyId(ProductFamily productFamily);

    default Product fromId(Long id) {
        if (id == null) {
            return null;
        }
        Product product = new Product();
        product.setId(id);
        return product;
    }
}
