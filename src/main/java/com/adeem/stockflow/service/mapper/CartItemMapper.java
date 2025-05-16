package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Cart;
import com.adeem.stockflow.domain.CartItem;
import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.service.dto.CartDTO;
import com.adeem.stockflow.service.dto.CartItemDTO;
import com.adeem.stockflow.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CartItem} and its DTO {@link CartItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface CartItemMapper extends EntityMapper<CartItemDTO, CartItem> {
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "cartId", source = "cart.id")
    CartItemDTO toDto(CartItem s);
}
