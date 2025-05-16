package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.PlanFormula;
import com.adeem.stockflow.domain.Subscription;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.dto.PlanFormulaDTO;
import com.adeem.stockflow.service.dto.SubscriptionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Subscription} and its DTO {@link SubscriptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface SubscriptionMapper extends EntityMapper<SubscriptionDTO, Subscription> {
    @Mapping(target = "planFormula", source = "planFormula", qualifiedByName = "planFormulaId")
    @Mapping(target = "clientAccountId", source = "clientAccount.id")
    SubscriptionDTO toDto(Subscription s);

    @Named("planFormulaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PlanFormulaDTO toDtoPlanFormulaId(PlanFormula planFormula);
}
