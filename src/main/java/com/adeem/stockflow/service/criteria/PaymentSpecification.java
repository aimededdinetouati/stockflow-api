package com.adeem.stockflow.service.criteria;

import com.adeem.stockflow.domain.Payment;
import com.adeem.stockflow.domain.enumeration.PaymentGateway;
import com.adeem.stockflow.domain.enumeration.PaymentMethod;
import com.adeem.stockflow.domain.enumeration.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for filtering Payment entities.
 */
public class PaymentSpecification {

    /**
     * Filter by payment reference.
     */
    public static Specification<Payment> withReference(String reference) {
        return BaseSpecification.equals("reference", reference);
    }

    /**
     * Filter by payment status.
     */
    public static Specification<Payment> withStatus(PaymentStatus status) {
        return BaseSpecification.equals("status", status);
    }

    /**
     * Filter by payment method.
     */
    public static Specification<Payment> withMethod(PaymentMethod method) {
        return BaseSpecification.equals("method", method);
    }

    /**
     * Filter by payment gateway.
     */
    public static Specification<Payment> withGateway(PaymentGateway gateway) {
        return BaseSpecification.equals("gateway", gateway);
    }

    /**
     * Filter by payment amount greater than or equal to a specific amount.
     */
    public static Specification<Payment> withAmountGreaterThanOrEqual(BigDecimal amount) {
        return BaseSpecification.greaterThanOrEqual("amount", amount);
    }

    /**
     * Filter by payment amount less than or equal to a specific amount.
     */
    public static Specification<Payment> withAmountLessThanOrEqual(BigDecimal amount) {
        return BaseSpecification.lessThanOrEqual("amount", amount);
    }

    /**
     * Filter by payment amount between a range.
     */
    public static Specification<Payment> withAmountBetween(BigDecimal min, BigDecimal max) {
        return BaseSpecification.between("amount", min, max);
    }

    /**
     * Filter by payment date between two dates.
     */
    public static Specification<Payment> withDateBetween(ZonedDateTime start, ZonedDateTime end) {
        return BaseSpecification.between("date", start, end);
    }

    /**
     * Filter by payment date before a specific date.
     */
    public static Specification<Payment> withDateBefore(ZonedDateTime date) {
        return BaseSpecification.lessThan("date", date);
    }

    /**
     * Filter by payment date after a specific date.
     */
    public static Specification<Payment> withDateAfter(ZonedDateTime date) {
        return BaseSpecification.greaterThan("date", date);
    }

    /**
     * Filter by reconciliation status.
     */
    public static Specification<Payment> withReconciled(Boolean reconciled) {
        return reconciled ? BaseSpecification.isTrue("reconciled") : BaseSpecification.isFalse("reconciled");
    }

    /**
     * Filter by reconciliation date between two dates.
     */
    public static Specification<Payment> withReconciledDateBetween(ZonedDateTime start, ZonedDateTime end) {
        return BaseSpecification.between("reconciledDate", start, end);
    }

    /**
     * Filter by reconciled by user.
     */
    public static Specification<Payment> withReconciledBy(String reconciledBy) {
        return BaseSpecification.equals("reconciledBy", reconciledBy);
    }

    /**
     * Filter by bank name.
     */
    public static Specification<Payment> withBankName(String bankName) {
        return BaseSpecification.contains("bankName", bankName);
    }

    /**
     * Filter by account number.
     */
    public static Specification<Payment> withAccountNumber(String accountNumber) {
        return BaseSpecification.equals("accountNumber", accountNumber);
    }

    /**
     * Filter by transfer reference number.
     */
    public static Specification<Payment> withTransferReferenceNumber(String transferReferenceNumber) {
        return BaseSpecification.equals("transferReferenceNumber", transferReferenceNumber);
    }

    /**
     * Filter by Chargily transaction ID.
     */
    public static Specification<Payment> withChargilyTransactionId(String chargilyTransactionId) {
        return BaseSpecification.equals("chargilyTransactionId", chargilyTransactionId);
    }

    /**
     * Filter by notes containing specific text.
     */
    public static Specification<Payment> withNotesContaining(String text) {
        return BaseSpecification.contains("notes", text);
    }

    /**
     * Filter by customer ID.
     */
    public static Specification<Payment> withCustomerId(Long customerId) {
        return (root, query, criteriaBuilder) -> {
            if (customerId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("customer", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), customerId);
        };
    }

    /**
     * Filter by client account ID.
     */
    public static Specification<Payment> withClientAccountId(Long clientAccountId) {
        return (root, query, criteriaBuilder) -> {
            if (clientAccountId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("clientAccount", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), clientAccountId);
        };
    }

    /**
     * Filter by sale order ID.
     */
    public static Specification<Payment> withSaleOrderId(Long saleOrderId) {
        return (root, query, criteriaBuilder) -> {
            if (saleOrderId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("saleOrder", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), saleOrderId);
        };
    }

    /**
     * Filter by payments that have attachments.
     */
    public static Specification<Payment> hasAttachments() {
        return (root, query, criteriaBuilder) -> {
            var attachmentsJoin = root.join("attachments", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.isNotNull(attachmentsJoin.get("id"));
        };
    }

    /**
     * Filter payments that need reconciliation (status CONFIRMED but not reconciled).
     */
    public static Specification<Payment> needsReconciliation() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.and(
                criteriaBuilder.equal(root.get("status"), PaymentStatus.CONFIRMED),
                criteriaBuilder.isFalse(root.get("reconciled"))
            );
    }

    /**
     * Filter by created by user.
     */
    public static Specification<Payment> withCreatedBy(String createdBy) {
        return BaseSpecification.equals("createdBy", createdBy);
    }

    /**
     * Filter by created date between two dates.
     */
    public static Specification<Payment> withCreatedDateBetween(Instant start, Instant end) {
        return BaseSpecification.between("createdDate", start, end);
    }
}
