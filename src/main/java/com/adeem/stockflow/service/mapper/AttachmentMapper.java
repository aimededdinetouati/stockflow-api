package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Attachment;
import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.domain.Payment;
import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.domain.User;
import com.adeem.stockflow.service.dto.AttachmentDTO;
import com.adeem.stockflow.service.dto.ClientAccountDTO;
import com.adeem.stockflow.service.dto.PaymentDTO;
import com.adeem.stockflow.service.dto.ProductDTO;
import com.adeem.stockflow.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Attachment} and its DTO {@link AttachmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AttachmentMapper extends EntityMapper<AttachmentDTO, Attachment> {
    @Mapping(target = "clientAccountId", source = "clientAccount.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "paymentId", source = "payment.id")
    @Mapping(target = "productId", source = "product.id")
    AttachmentDTO toDto(Attachment s);
}
