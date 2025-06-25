package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.GuestCartItem;
import com.adeem.stockflow.service.dto.GuestCartItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link GuestCartItem} and its DTO {@link GuestCartItemDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProductMapper.class, GuestCartMapper.class })
public interface GuestCartItemMapper extends EntityMapper<GuestCartItemDTO, GuestCartItem> {
    @Mapping(target = "product", source = "product")
    @Mapping(target = "totalPrice", ignore = true)
    GuestCartItemDTO toDto(GuestCartItem guestCartItem);

    @Mapping(target = "product", source = "product")
    @Mapping(target = "guestCart", ignore = true)
    GuestCartItem toEntity(GuestCartItemDTO guestCartItemDTO);

    @AfterMapping
    default void calculateItemTotal(@MappingTarget GuestCartItemDTO dto, GuestCartItem entity) {
        if (dto.getQuantity() != null && dto.getPriceAtTime() != null) {
            dto.setTotalPrice(dto.getQuantity().multiply(dto.getPriceAtTime()));
        }
    }

    default GuestCartItem fromId(Long id) {
        if (id == null) {
            return null;
        }
        GuestCartItem guestCartItem = new GuestCartItem();
        guestCartItem.setId(id);
        return guestCartItem;
    }
}
