package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Payment;
import com.adeem.stockflow.domain.PaymentReceipt;
import com.adeem.stockflow.service.dto.PaymentDTO;
import com.adeem.stockflow.service.dto.PaymentReceiptDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PaymentReceipt} and its DTO {@link PaymentReceiptDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentReceiptMapper extends EntityMapper<PaymentReceiptDTO, PaymentReceipt> {
    @Mapping(target = "payment", source = "payment", qualifiedByName = "paymentId")
    PaymentReceiptDTO toDto(PaymentReceipt s);

    @Named("paymentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PaymentDTO toDtoPaymentId(Payment payment);
}
