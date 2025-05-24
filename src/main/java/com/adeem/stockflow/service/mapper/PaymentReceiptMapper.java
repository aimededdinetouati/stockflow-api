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
    @Mapping(target = "paymentId", source = "payment.id")
    PaymentReceiptDTO toDto(PaymentReceipt s);

    default PaymentReceipt fromId(Long id) {
        if (id == null) {
            return null;
        }
        PaymentReceipt paymentReceipt = new PaymentReceipt();
        paymentReceipt.setId(id);
        return paymentReceipt;
    }
}
