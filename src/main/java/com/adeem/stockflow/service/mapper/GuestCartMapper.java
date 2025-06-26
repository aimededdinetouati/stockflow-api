// File: src/main/java/com/adeem/stockflow/service/mapper/GuestCartMapper.java
package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.GuestCart;
import com.adeem.stockflow.service.dto.GuestCartDTO;
import java.time.Instant;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link GuestCart} and its DTO {@link GuestCartDTO}.
 */
@Mapper(componentModel = "spring", uses = { GuestCartItemMapper.class })
public interface GuestCartMapper extends EntityMapper<GuestCartDTO, GuestCart> {
    @Mapping(target = "items", source = "items")
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "totalItems", ignore = true)
    @Mapping(target = "isExpired", ignore = true)
    GuestCartDTO toDto(GuestCart guestCart);

    @Mapping(target = "items", source = "items")
    GuestCart toEntity(GuestCartDTO guestCartDTO);

    @AfterMapping
    default void calculateTotals(@MappingTarget GuestCartDTO dto, GuestCart entity) {
        if (dto.getItems() != null) {
            dto.setTotalItems(dto.getItems().size());
            dto.setTotalAmount(
                dto
                    .getItems()
                    .stream()
                    .map(item -> item.getQuantity().multiply(item.getPriceAtTime()))
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
            );
        } else {
            dto.setTotalItems(0);
            dto.setTotalAmount(java.math.BigDecimal.ZERO);
        }

        // Check if cart is expired
        dto.setIsExpired(dto.getExpiresAt() != null && dto.getExpiresAt().isBefore(Instant.now()));
    }

    default GuestCart fromId(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        GuestCart guestCart = new GuestCart();
        guestCart.setSessionId(sessionId);
        return guestCart;
    }
}
