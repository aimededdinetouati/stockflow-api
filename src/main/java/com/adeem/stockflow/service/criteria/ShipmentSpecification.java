package com.adeem.stockflow.service.criteria;

import com.adeem.stockflow.domain.*;
import com.adeem.stockflow.domain.enumeration.ShippingStatus;
import jakarta.persistence.criteria.*;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specification for filtering Shipment entities.
 * Supports multi-tenant filtering and comprehensive search criteria.
 */
public class ShipmentSpecification {

    private ShipmentSpecification() {
        // Utility class
    }

    /**
     * Multi-tenant scoping - always required.
     */
    public static Specification<Shipment> withClientAccountId(Long clientAccountId) {
        return (root, query, criteriaBuilder) -> {
            if (clientAccountId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("clientAccount").get("id"), clientAccountId);
        };
    }

    /**
     * Filter by shipping status.
     */
    public static Specification<Shipment> withStatus(ShippingStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    /**
     * Filter by multiple shipping statuses.
     */
    public static Specification<Shipment> withMultipleStatuses(List<ShippingStatus> statuses) {
        return (root, query, criteriaBuilder) -> {
            if (statuses == null || statuses.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("status").in(statuses);
        };
    }

    /**
     * Filter by carrier name containing text.
     */
    public static Specification<Shipment> withCarrierContaining(String carrier) {
        return (root, query, criteriaBuilder) -> {
            if (carrier == null || carrier.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("carrier")), "%" + carrier.toLowerCase() + "%");
        };
    }

    /**
     * Filter by exact carrier name.
     */
    public static Specification<Shipment> withCarrier(String carrier) {
        return (root, query, criteriaBuilder) -> {
            if (carrier == null || carrier.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(criteriaBuilder.lower(root.get("carrier")), carrier.toLowerCase());
        };
    }

    /**
     * Filter by tracking number containing text.
     */
    public static Specification<Shipment> withTrackingNumberContaining(String trackingNumber) {
        return (root, query, criteriaBuilder) -> {
            if (trackingNumber == null || trackingNumber.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("trackingNumber")), "%" + trackingNumber.toLowerCase() + "%");
        };
    }

    /**
     * Filter by exact tracking number.
     */
    public static Specification<Shipment> withTrackingNumber(String trackingNumber) {
        return (root, query, criteriaBuilder) -> {
            if (trackingNumber == null || trackingNumber.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("trackingNumber"), trackingNumber);
        };
    }

    /**
     * Filter by reference containing text.
     */
    public static Specification<Shipment> withReferenceContaining(String reference) {
        return (root, query, criteriaBuilder) -> {
            if (reference == null || reference.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("reference")), "%" + reference.toLowerCase() + "%");
        };
    }

    /**
     * Filter by shipping date range.
     */
    public static Specification<Shipment> withShippingDateRange(Instant from, Instant to) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (from != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("shippingDate"), from));
            }

            if (to != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("shippingDate"), to));
            }

            return predicate;
        };
    }

    /**
     * Filter by estimated delivery date range.
     */
    public static Specification<Shipment> withEstimatedDeliveryDateRange(Instant from, Instant to) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (from != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("estimatedDeliveryDate"), from));
            }

            if (to != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("estimatedDeliveryDate"), to));
            }

            return predicate;
        };
    }

    /**
     * Filter by notes containing text.
     */
    public static Specification<Shipment> withNotesContaining(String notes) {
        return (root, query, criteriaBuilder) -> {
            if (notes == null || notes.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("notes")), "%" + notes.toLowerCase() + "%");
        };
    }

    /**
     * Filter shipments that have Yalidine integration.
     */
    public static Specification<Shipment> withYalidineShipments() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("yalidineShipmentId"));
    }

    /**
     * Filter shipments without Yalidine integration.
     */
    public static Specification<Shipment> withoutYalidineShipments() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("yalidineShipmentId"));
    }

    /**
     * Filter by Yalidine carrier specifically.
     */
    public static Specification<Shipment> withYalidineCarrier() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.upper(root.get("carrier")), "YALIDINE");
    }

    /**
     * Filter by sale order ID.
     */
    public static Specification<Shipment> withSaleOrderId(Long saleOrderId) {
        return (root, query, criteriaBuilder) -> {
            if (saleOrderId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("saleOrder").get("id"), saleOrderId);
        };
    }

    /**
     * Filter by sale order reference.
     */
    public static Specification<Shipment> withSaleOrderReference(String orderReference) {
        return (root, query, criteriaBuilder) -> {
            if (orderReference == null || orderReference.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            Join<Shipment, SaleOrder> orderJoin = root.join("saleOrder");
            return criteriaBuilder.like(criteriaBuilder.lower(orderJoin.get("reference")), "%" + orderReference.toLowerCase() + "%");
        };
    }

    /**
     * Filter by customer ID through sale order.
     */
    public static Specification<Shipment> withCustomerId(Long customerId) {
        return (root, query, criteriaBuilder) -> {
            if (customerId == null) {
                return criteriaBuilder.conjunction();
            }

            Join<Shipment, SaleOrder> orderJoin = root.join("saleOrder");
            Join<SaleOrder, Customer> customerJoin = orderJoin.join("customer");
            return criteriaBuilder.equal(customerJoin.get("id"), customerId);
        };
    }

    /**
     * Filter overdue shipments (estimated delivery date has passed).
     */
    public static Specification<Shipment> isOverdue() {
        return (root, query, criteriaBuilder) -> {
            Instant now = Instant.now();

            Predicate hasEstimatedDate = criteriaBuilder.isNotNull(root.get("estimatedDeliveryDate"));
            Predicate isPastDue = criteriaBuilder.lessThan(root.get("estimatedDeliveryDate"), now);
            Predicate notDelivered = criteriaBuilder.notEqual(root.get("status"), ShippingStatus.DELIVERED);

            return criteriaBuilder.and(hasEstimatedDate, isPastDue, notDelivered);
        };
    }

    /**
     * Filter by delivery address city.
     */
    public static Specification<Shipment> withDeliveryCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (city == null || city.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            Join<Shipment, Address> addressJoin = root.join("address", JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(addressJoin.get("city")), "%" + city.toLowerCase() + "%");
        };
    }

    /**
     * Filter shipments shipped in the last N days.
     */
    public static Specification<Shipment> shippedInLastDays(int days) {
        return (root, query, criteriaBuilder) -> {
            Instant cutoffDate = Instant.now().minus(days, java.time.temporal.ChronoUnit.DAYS);
            return criteriaBuilder.greaterThanOrEqualTo(root.get("shippingDate"), cutoffDate);
        };
    }

    /**
     * Filter pending shipments that need processing.
     */
    public static Specification<Shipment> needsProcessing() {
        return (root, query, criteriaBuilder) -> {
            Predicate isPending = criteriaBuilder.equal(root.get("status"), ShippingStatus.PENDING);
            Predicate isProcessing = criteriaBuilder.equal(root.get("status"), ShippingStatus.PROCESSING);

            return criteriaBuilder.or(isPending, isProcessing);
        };
    }

    /**
     * Filter by weight range.
     */
    public static Specification<Shipment> withWeightRange(Double minWeight, Double maxWeight) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (minWeight != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("weight"), minWeight));
            }

            if (maxWeight != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("weight"), maxWeight));
            }

            return predicate;
        };
    }

    /**
     * Filter deliveries completed today.
     */
    public static Specification<Shipment> deliveredToday() {
        return (root, query, criteriaBuilder) -> {
            Instant startOfDay = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.DAYS);
            Instant endOfDay = startOfDay.plus(1, java.time.temporal.ChronoUnit.DAYS);

            Predicate isDelivered = criteriaBuilder.equal(root.get("status"), ShippingStatus.DELIVERED);
            Predicate deliveredToday = criteriaBuilder.between(root.get("actualDeliveryDate"), startOfDay, endOfDay);

            return criteriaBuilder.and(isDelivered, deliveredToday);
        };
    }
}
