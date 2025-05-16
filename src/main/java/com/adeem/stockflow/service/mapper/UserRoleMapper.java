package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Admin;
import com.adeem.stockflow.domain.Role;
import com.adeem.stockflow.domain.UserRole;
import com.adeem.stockflow.service.dto.AdminDTO;
import com.adeem.stockflow.service.dto.RoleDTO;
import com.adeem.stockflow.service.dto.UserRoleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserRole} and its DTO {@link UserRoleDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserRoleMapper extends EntityMapper<UserRoleDTO, UserRole> {
    @Mapping(target = "admin", source = "admin", qualifiedByName = "adminId")
    @Mapping(target = "role", source = "role", qualifiedByName = "roleId")
    UserRoleDTO toDto(UserRole s);

    @Named("adminId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AdminDTO toDtoAdminId(Admin admin);

    @Named("roleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RoleDTO toDtoRoleId(Role role);
}
