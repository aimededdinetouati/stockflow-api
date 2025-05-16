package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Cart;
import com.adeem.stockflow.domain.Customer;
import com.adeem.stockflow.service.dto.CartDTO;
import com.adeem.stockflow.service.dto.CustomerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Cart} and its DTO {@link CartDTO}.
 */
@Mapper(componentModel = "spring")
public interface CartMapper extends EntityMapper<CartDTO, Cart> {
    @Mapping(target = "customerId", source = "customer.id")
    CartDTO toDto(Cart s);
}
