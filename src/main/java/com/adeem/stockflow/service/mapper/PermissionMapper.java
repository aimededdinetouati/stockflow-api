package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Permission;
import com.adeem.stockflow.service.dto.PermissionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Permission} and its DTO {@link PermissionDTO}.
 */
@Mapper(componentModel = "spring")
public interface PermissionMapper extends EntityMapper<PermissionDTO, Permission> {}
