package com.adeem.stockflow.service.criteria;

import com.adeem.stockflow.domain.SaleOrder;
import com.adeem.stockflow.domain.enumeration.OrderStatus;
import com.adeem.stockflow.domain.enumeration.SaleType;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for filtering SaleOrder entities.
 */
public class SaleOrderSpecification {

    /**
     * Filter by order reference.
     */
    public static Specification<SaleOrder> withReference(String reference) {
        return BaseSpecification.equals("reference", reference);
    }

    /**
     * Filter by order status.
     */
    public static Specification<SaleOrder> withStatus(OrderStatus status) {
        return BaseSpecification.equals("status", status);
    }

    /**
     * Filter by sale type.
     */
    public static Specification<SaleOrder> withSaleType(SaleType type) {
        return BaseSpecification.equals("saleType", type);
    }

    /**
     * Filter by order date between two dates.
     */
    public static Specification<SaleOrder> withDateBetween(ZonedDateTime start, ZonedDateTime end) {
        return BaseSpecification.between("date", start, end);
    }

    /**
     * Filter by due date between two dates.
     */
    public static Specification<SaleOrder> withDueDateBetween(ZonedDateTime start, ZonedDateTime end) {
        return BaseSpecification.between("dueDate", start, end);
    }

    /**
     * Filter orders with due date before the specified date.
     */
    public static Specification<SaleOrder> withDueDateBefore(ZonedDateTime date) {
        return BaseSpecification.lessThan("dueDate", date);
    }

    /**
     * Filter by order total greater than or equal to the specified amount.
     */
    public static Specification<SaleOrder> withTotalGreaterThanOrEqual(BigDecimal total) {
        return BaseSpecification.greaterThanOrEqual("total", total);
    }

    /**
     * Filter by order total less than or equal to the specified amount.
     */
    public static Specification<SaleOrder> withTotalLessThanOrEqual(BigDecimal total) {
        return BaseSpecification.lessThanOrEqual("total", total);
    }

    /**
     * Filter orders with a total between the specified range.
     */
    public static Specification<SaleOrder> withTotalBetween(BigDecimal min, BigDecimal max) {
        return BaseSpecification.between("total", min, max);
    }

    /**
     * Filter orders containing specific text in notes.
     */
    public static Specification<SaleOrder> withNotesContaining(String text) {
        return BaseSpecification.contains("notes", text);
    }

    /**
     * Filter by customer ID.
     */
    public static Specification<SaleOrder> withCustomerId(Long customerId) {
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
    public static Specification<SaleOrder> withClientAccountId(Long clientAccountId) {
        return (root, query, criteriaBuilder) -> {
            if (clientAccountId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("clientAccount", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), clientAccountId);
        };
    }

    /**
     * Filter by product ID (orders containing the specified product).
     */
    public static Specification<SaleOrder> withProductId(Long productId) {
        return (root, query, criteriaBuilder) -> {
            if (productId == null) {
                return criteriaBuilder.conjunction();
            }
            var orderItemsJoin = root.join("orderItems", jakarta.persistence.criteria.JoinType.LEFT);
            var productJoin = orderItemsJoin.join("product", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(productJoin.get("id"), productId);
        };
    }

    /**
     * Filter by payment status.
     */
    public static Specification<SaleOrder> withPaymentStatus(com.adeem.stockflow.domain.enumeration.PaymentStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            var paymentJoin = root.join("payment", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(paymentJoin.get("status"), status);
        };
    }

    /**
     * Filter orders that have a shipment.
     */
    public static Specification<SaleOrder> hasShipment() {
        return (root, query, criteriaBuilder) -> {
            var shipmentJoin = root.join("shipment", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.isNotNull(shipmentJoin.get("id"));
        };
    }

    /**
     * Filter orders with specific shipping status.
     */
    public static Specification<SaleOrder> withShippingStatus(com.adeem.stockflow.domain.enumeration.ShippingStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            var shipmentJoin = root.join("shipment", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(shipmentJoin.get("status"), status);
        };
    }

    /**
     * Filter orders that are overdue (due date is in the past and status is not CANCELLED or RETURNED).
     */
    public static Specification<SaleOrder> isOverdue() {
        return (root, query, criteriaBuilder) -> {
            ZonedDateTime now = ZonedDateTime.now();
            return criteriaBuilder.and(
                criteriaBuilder.lessThan(root.get("dueDate"), now),
                criteriaBuilder.notEqual(root.get("status"), OrderStatus.CANCELLED),
                criteriaBuilder.notEqual(root.get("status"), OrderStatus.RETURNED)
            );
        };
    }
}
