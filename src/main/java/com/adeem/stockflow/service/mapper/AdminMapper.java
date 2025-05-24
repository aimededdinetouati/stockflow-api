package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Admin;
import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.User;
import com.adeem.stockflow.service.dto.AdminDTO;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Admin} and its DTO {@link AdminDTO}.
 */
@Mapper(componentModel = "spring")
public interface AdminMapper extends EntityMapper<AdminDTO, Admin> {
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "clientAccountId", source = "clientAccount.id")
    AdminDTO toDto(Admin s);

    default Admin fromId(Long id) {
        if (id == null) {
            return null;
        }
        Admin admin = new Admin();
        admin.setId(id);
        return admin;
    }
}
