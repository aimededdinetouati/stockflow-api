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
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    @Mapping(target = "cartId", source = "cart.id")
    CartItemDTO toDto(CartItem s);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);

    default CartItem fromId(Long id) {
        if (id == null) {
            return null;
        }
        CartItem cartItem = new CartItem();
        cartItem.setId(id);
        return cartItem;
    }
}
