package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.Quota;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.dto.QuotaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Quota} and its DTO {@link QuotaDTO}.
 */
@Mapper(componentModel = "spring")
public interface QuotaMapper extends EntityMapper<QuotaDTO, Quota> {
    @Mapping(target = "clientAccountId", source = "clientAccount.id")
    QuotaDTO toDto(Quota s);
}
