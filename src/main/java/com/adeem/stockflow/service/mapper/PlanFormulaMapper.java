package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.PlanFormula;
import com.adeem.stockflow.service.dto.PlanFormulaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PlanFormula} and its DTO {@link PlanFormulaDTO}.
 */
@Mapper(componentModel = "spring")
public interface PlanFormulaMapper extends EntityMapper<PlanFormulaDTO, PlanFormula> {}
