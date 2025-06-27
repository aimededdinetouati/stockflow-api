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
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "totalItems", ignore = true)
    @Mapping(target = "isExpired", ignore = true)
    GuestCartDTO toDto(GuestCart guestCart);

    GuestCart toEntity(GuestCartDTO guestCartDTO);

    default GuestCart fromId(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        GuestCart guestCart = new GuestCart();
        guestCart.setSessionId(sessionId);
        return guestCart;
    }
}
