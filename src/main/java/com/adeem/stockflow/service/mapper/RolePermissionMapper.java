package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Permission;
import com.adeem.stockflow.domain.Role;
import com.adeem.stockflow.domain.RolePermission;
import com.adeem.stockflow.service.dto.PermissionDTO;
import com.adeem.stockflow.service.dto.RoleDTO;
import com.adeem.stockflow.service.dto.RolePermissionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RolePermission} and its DTO {@link RolePermissionDTO}.
 */
@Mapper(componentModel = "spring")
public interface RolePermissionMapper extends EntityMapper<RolePermissionDTO, RolePermission> {
    @Mapping(target = "role", source = "role", qualifiedByName = "roleId")
    @Mapping(target = "permission", source = "permission", qualifiedByName = "permissionId")
    RolePermissionDTO toDto(RolePermission s);

    @Named("roleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RoleDTO toDtoRoleId(Role role);

    @Named("permissionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PermissionDTO toDtoPermissionId(Permission permission);
}
