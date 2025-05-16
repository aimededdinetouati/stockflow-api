package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.ProductFamily;
import com.adeem.stockflow.service.dto.ProductFamilyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductFamily} and its DTO {@link ProductFamilyDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductFamilyMapper extends EntityMapper<ProductFamilyDTO, ProductFamily> {}
