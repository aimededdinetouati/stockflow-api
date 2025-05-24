package com.adeem.stockflow.service.criteria;

import com.adeem.stockflow.domain.Shipment;
import com.adeem.stockflow.domain.enumeration.ShippingStatus;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for filtering Shipment entities.
 */
public class ShipmentSpecification {

    /**
     * Filter by shipment reference.
     */
    public static Specification<Shipment> withReference(String reference) {
        return BaseSpecification.equals("reference", reference);
    }

    /**
     * Filter by tracking number.
     */
    public static Specification<Shipment> withTrackingNumber(String trackingNumber) {
        return BaseSpecification.equals("trackingNumber", trackingNumber);
    }

    /**
     * Filter by carrier.
     */
    public static Specification<Shipment> withCarrier(String carrier) {
        return BaseSpecification.contains("carrier", carrier);
    }

    /**
     * Filter by shipping status.
     */
    public static Specification<Shipment> withStatus(ShippingStatus status) {
        return BaseSpecification.equals("status", status);
    }

    /**
     * Filter by shipping date between two dates.
     */
    public static Specification<Shipment> withShippingDateBetween(Instant start, Instant end) {
        return BaseSpecification.between("shippingDate", start, end);
    }

    /**
     * Filter by estimated delivery date between two dates.
     */
    public static Specification<Shipment> withEstimatedDeliveryDateBetween(Instant start, Instant end) {
        return BaseSpecification.between("estimatedDeliveryDate", start, end);
    }

    /**
     * Filter by actual delivery date between two dates.
     */
    public static Specification<Shipment> withActualDeliveryDateBetween(Instant start, Instant end) {
        return BaseSpecification.between("actualDeliveryDate", start, end);
    }

    /**
     * Filter shipments that are delayed (estimated delivery date has passed but status is not DELIVERED).
     */
    public static Specification<Shipment> isDelayed() {
        return (root, query, criteriaBuilder) -> {
            Instant now = Instant.now();
            return criteriaBuilder.and(
                criteriaBuilder.lessThan(root.get("estimatedDeliveryDate"), now),
                criteriaBuilder.notEqual(root.get("status"), ShippingStatus.DELIVERED),
                criteriaBuilder.notEqual(root.get("status"), ShippingStatus.RETURNED),
                criteriaBuilder.notEqual(root.get("status"), ShippingStatus.FAILED)
            );
        };
    }

    /**
     * Filter by shipping cost greater than or equal to a specific amount.
     */
    public static Specification<Shipment> withShippingCostGreaterThanOrEqual(BigDecimal cost) {
        return BaseSpecification.greaterThanOrEqual("shippingCost", cost);
    }

    /**
     * Filter by shipping cost less than or equal to a specific amount.
     */
    public static Specification<Shipment> withShippingCostLessThanOrEqual(BigDecimal cost) {
        return BaseSpecification.lessThanOrEqual("shippingCost", cost);
    }

    /**
     * Filter by weight greater than or equal to a specific value.
     */
    public static Specification<Shipment> withWeightGreaterThanOrEqual(Double weight) {
        return BaseSpecification.greaterThanOrEqual("weight", weight);
    }

    /**
     * Filter by weight less than or equal to a specific value.
     */
    public static Specification<Shipment> withWeightLessThanOrEqual(Double weight) {
        return BaseSpecification.lessThanOrEqual("weight", weight);
    }

    /**
     * Filter by notes containing specific text.
     */
    public static Specification<Shipment> withNotesContaining(String text) {
        return BaseSpecification.contains("notes", text);
    }

    /**
     * Filter by sale order ID.
     */
    public static Specification<Shipment> withSaleOrderId(Long saleOrderId) {
        return (root, query, criteriaBuilder) -> {
            if (saleOrderId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("saleOrder", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), saleOrderId);
        };
    }

    /**
     * Filter by address ID.
     */
    public static Specification<Shipment> withAddressId(Long addressId) {
        return (root, query, criteriaBuilder) -> {
            if (addressId == null) {
                return criteriaBuilder.conjunction();
            }
            var join = root.join("address", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(join.get("id"), addressId);
        };
    }

    /**
     * Filter by delivery city.
     */
    public static Specification<Shipment> withDeliveryCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (city == null || city.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            var addressJoin = root.join("address", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(addressJoin.get("city")), "%" + city.toLowerCase() + "%");
        };
    }

    /**
     * Filter by delivery country.
     */
    public static Specification<Shipment> withDeliveryCountry(String country) {
        return (root, query, criteriaBuilder) -> {
            if (country == null || country.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            var addressJoin = root.join("address", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(addressJoin.get("country")), "%" + country.toLowerCase() + "%");
        };
    }

    /**
     * Filter by customer ID (from sale order).
     */
    public static Specification<Shipment> withCustomerId(Long customerId) {
        return (root, query, criteriaBuilder) -> {
            if (customerId == null) {
                return criteriaBuilder.conjunction();
            }
            var saleOrderJoin = root.join("saleOrder", jakarta.persistence.criteria.JoinType.LEFT);
            var customerJoin = saleOrderJoin.join("customer", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(customerJoin.get("id"), customerId);
        };
    }

    /**
     * Filter by client account ID (from sale order).
     */
    public static Specification<Shipment> withClientAccountId(Long clientAccountId) {
        return (root, query, criteriaBuilder) -> {
            if (clientAccountId == null) {
                return criteriaBuilder.conjunction();
            }
            var saleOrderJoin = root.join("saleOrder", jakarta.persistence.criteria.JoinType.LEFT);
            var clientAccountJoin = saleOrderJoin.join("clientAccount", jakarta.persistence.criteria.JoinType.LEFT);
            return criteriaBuilder.equal(clientAccountJoin.get("id"), clientAccountId);
        };
    }

    /**
     * Filter by created by user.
     */
    public static Specification<Shipment> withCreatedBy(String createdBy) {
        return BaseSpecification.equals("createdBy", createdBy);
    }

    /**
     * Filter by created date between two dates.
     */
    public static Specification<Shipment> withCreatedDateBetween(Instant start, Instant end) {
        return BaseSpecification.between("createdDate", start, end);
    }

    /**
     * Filter by last modified by user.
     */
    public static Specification<Shipment> withLastModifiedBy(String lastModifiedBy) {
        return BaseSpecification.equals("lastModifiedBy", lastModifiedBy);
    }

    /**
     * Filter by last modified date between two dates.
     */
    public static Specification<Shipment> withLastModifiedDateBetween(Instant start, Instant end) {
        return BaseSpecification.between("lastModifiedDate", start, end);
    }
}
