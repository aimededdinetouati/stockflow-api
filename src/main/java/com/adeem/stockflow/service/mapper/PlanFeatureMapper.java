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
    @Mapping(target = "planFormulaId", source = "planFormula.id")
    PlanFeatureDTO toDto(PlanFeature s);

    default PlanFeature fromId(Long id) {
        if (id == null) {
            return null;
        }
        PlanFeature planFeature = new PlanFeature();
        planFeature.setId(id);
        return planFeature;
    }
}
