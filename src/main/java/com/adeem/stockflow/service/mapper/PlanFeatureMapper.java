package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.PlanFeature;
import com.adeem.stockflow.domain.PlanFormula;
import com.adeem.stockflow.service.dto.PlanFeatureDTO;
import com.adeem.stockflow.service.dto.PlanFormulaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PlanFeature} and its DTO {@link PlanFeatureDTO}.
 */
@Mapper(componentModel = "spring")
public interface PlanFeatureMapper extends EntityMapper<PlanFeatureDTO, PlanFeature> {
    @Mapping(target = "planFormula", source = "planFormula", qualifiedByName = "planFormulaId")
    PlanFeatureDTO toDto(PlanFeature s);

    @Named("planFormulaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PlanFormulaDTO toDtoPlanFormulaId(PlanFormula planFormula);
}
