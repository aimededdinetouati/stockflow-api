package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.PlanFormula;
import com.adeem.stockflow.domain.ResourceLimit;
import com.adeem.stockflow.service.dto.PlanFormulaDTO;
import com.adeem.stockflow.service.dto.ResourceLimitDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ResourceLimit} and its DTO {@link ResourceLimitDTO}.
 */
@Mapper(componentModel = "spring")
public interface ResourceLimitMapper extends EntityMapper<ResourceLimitDTO, ResourceLimit> {
    @Mapping(target = "planFormula", source = "planFormula", qualifiedByName = "planFormulaId")
    ResourceLimitDTO toDto(ResourceLimit s);

    @Named("planFormulaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PlanFormulaDTO toDtoPlanFormulaId(PlanFormula planFormula);
}
