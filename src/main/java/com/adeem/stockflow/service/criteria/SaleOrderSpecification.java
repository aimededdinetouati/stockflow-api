package com.adeem.stockflow.service.criteria;

import com.adeem.stockflow.domain.*;
import com.adeem.stockflow.domain.enumeration.OrderStatus;
import com.adeem.stockflow.domain.enumeration.OrderType;
import com.adeem.stockflow.domain.enumeration.PaymentStatus;
import com.adeem.stockflow.domain.enumeration.SaleType;
import com.adeem.stockflow.service.criteria.filter.SaleOrderCriteria;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.StringFilter;

/**
 * Service for executing complex queries for {@link SaleOrder} entities in the database.
 * The main input is a {@link SaleOrderCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SaleOrder} or a {@link org.springframework.data.domain.Page} of {@link SaleOrder} which fulfills the criteria.
 */
public class SaleOrderSpecification extends QueryService<SaleOrder> {

    /**
     * Function to convert {@link SaleOrderCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    public Specification<SaleOrder> createSpecification(SaleOrderCriteria criteria) {
        Specification<SaleOrder> specification = Specification.where(null);
        if (criteria != null) {
            // Always apply multi-tenant filtering first
            if (criteria.getClientAccountId() != null) {
                specification = specification.and(
                    buildReferringEntitySpecification(criteria.getClientAccountId(), SaleOrder_.clientAccount, ClientAccount_.id)
                );
            }

            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SaleOrder_.id));
            }
            if (criteria.getReference() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReference(), SaleOrder_.reference));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), SaleOrder_.date));
            }
            if (criteria.getDueDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDueDate(), SaleOrder_.dueDate));
            }
            if (criteria.getNotes() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNotes(), SaleOrder_.notes));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildEnumSpecification(criteria.getStatus(), SaleOrder_.status, OrderStatus.class));
            }
            if (criteria.getOrderType() != null) {
                specification = specification.and(buildEnumSpecification(criteria.getOrderType(), SaleOrder_.orderType, OrderType.class));
            }
            if (criteria.getReservationExpiresAt() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getReservationExpiresAt(), SaleOrder_.reservationExpiresAt)
                );
            }
            if (criteria.getCustomerNotes() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCustomerNotes(), SaleOrder_.customerNotes));
            }
            if (criteria.getShippingCost() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getShippingCost(), SaleOrder_.shippingCost));
            }
            if (criteria.getTvaRate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTvaRate(), SaleOrder_.tvaRate));
            }
            if (criteria.getStampRate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStampRate(), SaleOrder_.stampRate));
            }
            if (criteria.getDiscountRate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDiscountRate(), SaleOrder_.discountRate));
            }
            if (criteria.getTvaAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTvaAmount(), SaleOrder_.tvaAmount));
            }
            if (criteria.getStampAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStampAmount(), SaleOrder_.stampAmount));
            }
            if (criteria.getDiscountAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDiscountAmount(), SaleOrder_.discountAmount));
            }
            if (criteria.getSubTotal() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSubTotal(), SaleOrder_.subTotal));
            }
            if (criteria.getTotal() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotal(), SaleOrder_.total));
            }
            if (criteria.getSaleType() != null) {
                specification = specification.and(buildEnumSpecification(criteria.getSaleType(), SaleOrder_.saleType, SaleType.class));
            }
            if (criteria.getPaymentId() != null) {
                specification = specification.and(
                    buildReferringEntitySpecification(criteria.getPaymentId(), SaleOrder_.payment, Payment_.id)
                );
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(
                    buildReferringEntitySpecification(criteria.getCustomerId(), SaleOrder_.customer, Customer_.id)
                );
            }
            if (criteria.getShipmentId() != null) {
                specification = specification.and(
                    buildReferringEntitySpecification(criteria.getShipmentId(), SaleOrder_.shipment, Shipment_.id)
                );
            }

            // Custom specifications for complex filtering
            if (criteria.getPaymentStatus() != null) {
                specification = specification.and(buildPaymentStatusSpecification(criteria.getPaymentStatus()));
            }
            if (criteria.getCustomerName() != null) {
                specification = specification.and(buildCustomerNameSpecification(criteria.getCustomerName()));
            }
            if (criteria.getFromDate() != null || criteria.getToDate() != null) {
                specification = specification.and(buildDateRangeSpecification(criteria.getFromDate(), criteria.getToDate()));
            }
            if (criteria.getMinTotal() != null || criteria.getMaxTotal() != null) {
                specification = specification.and(buildTotalRangeSpecification(criteria.getMinTotal(), criteria.getMaxTotal()));
            }
            if (criteria.getHasShipment() != null) {
                specification = specification.and(buildHasShipmentSpecification(criteria.getHasShipment()));
            }
            if (criteria.getHasPayment() != null) {
                specification = specification.and(buildHasPaymentSpecification(criteria.getHasPayment()));
            }
        }
        return specification;
    }

    /**
     * Build specification for enum fields from string filters
     */
    private <T extends Enum<T>> Specification<SaleOrder> buildEnumSpecification(
        tech.jhipster.service.filter.StringFilter filter,
        SingularAttribute<? super SaleOrder, T> field,
        Class<T> enumClass
    ) {
        return (root, query, criteriaBuilder) -> {
            if (filter.getEquals() != null) {
                try {
                    T enumValue = Enum.valueOf(enumClass, filter.getEquals().toUpperCase());
                    return criteriaBuilder.equal(root.get(field), enumValue);
                } catch (IllegalArgumentException e) {
                    // Invalid enum value, return no results
                    return criteriaBuilder.disjunction();
                }
            }
            if (filter.getNotEquals() != null) {
                try {
                    T enumValue = Enum.valueOf(enumClass, filter.getNotEquals().toUpperCase());
                    return criteriaBuilder.notEqual(root.get(field), enumValue);
                } catch (IllegalArgumentException e) {
                    // Invalid enum value, return all results
                    return criteriaBuilder.conjunction();
                }
            }
            if (filter.getIn() != null && !filter.getIn().isEmpty()) {
                Predicate predicate = criteriaBuilder.disjunction();
                for (String value : filter.getIn()) {
                    try {
                        T enumValue = Enum.valueOf(enumClass, value.toUpperCase());
                        predicate = criteriaBuilder.or(predicate, criteriaBuilder.equal(root.get(field), enumValue));
                    } catch (IllegalArgumentException e) {
                        // Skip invalid enum values
                    }
                }
                return predicate;
            }
            return criteriaBuilder.conjunction();
        };
    }

    /**
     * Build specification for payment status filtering
     */
    private Specification<SaleOrder> buildPaymentStatusSpecification(tech.jhipster.service.filter.StringFilter filter) {
        return (root, query, criteriaBuilder) -> {
            Join<SaleOrder, Payment> paymentJoin = root.join(SaleOrder_.payment, JoinType.LEFT);

            if (filter.getEquals() != null) {
                try {
                    PaymentStatus status = PaymentStatus.valueOf(filter.getEquals().toUpperCase());
                    return criteriaBuilder.equal(paymentJoin.get(Payment_.status), status);
                } catch (IllegalArgumentException e) {
                    return criteriaBuilder.disjunction();
                }
            }
            if (filter.getNotEquals() != null) {
                try {
                    PaymentStatus status = PaymentStatus.valueOf(filter.getNotEquals().toUpperCase());
                    return criteriaBuilder.notEqual(paymentJoin.get(Payment_.status), status);
                } catch (IllegalArgumentException e) {
                    return criteriaBuilder.conjunction();
                }
            }
            return criteriaBuilder.conjunction();
        };
    }

    /**
     * Build specification for customer name filtering
     */
    private Specification<SaleOrder> buildCustomerNameSpecification(StringFilter filter) {
        return (root, query, criteriaBuilder) -> {
            Join<SaleOrder, Customer> customerJoin = root.join(SaleOrder_.customer, JoinType.INNER);

            Predicate predicate = criteriaBuilder.conjunction();

            if (filter.getEquals() != null) {
                Predicate firstNameMatch = criteriaBuilder.equal(
                    criteriaBuilder.lower(customerJoin.get(Customer_.firstName)),
                    filter.getEquals().toLowerCase()
                );
                Predicate lastNameMatch = criteriaBuilder.equal(
                    criteriaBuilder.lower(customerJoin.get(Customer_.lastName)),
                    filter.getEquals().toLowerCase()
                );
                predicate = criteriaBuilder.or(firstNameMatch, lastNameMatch);
            }

            if (filter.getContains() != null) {
                String pattern = "%" + filter.getContains().toLowerCase() + "%";
                Predicate firstNameMatch = criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get(Customer_.firstName)), pattern);
                Predicate lastNameMatch = criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get(Customer_.lastName)), pattern);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.or(firstNameMatch, lastNameMatch));
            }

            return predicate;
        };
    }

    /**
     * Build specification for date range filtering
     */
    private Specification<SaleOrder> buildDateRangeSpecification(
        tech.jhipster.service.filter.ZonedDateTimeFilter fromDate,
        tech.jhipster.service.filter.ZonedDateTimeFilter toDate
    ) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (fromDate != null && fromDate.getGreaterThanOrEqual() != null) {
                predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.greaterThanOrEqualTo(root.get(SaleOrder_.date), fromDate.getGreaterThanOrEqual())
                );
            }

            if (toDate != null && toDate.getLessThanOrEqual() != null) {
                predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.lessThanOrEqualTo(root.get(SaleOrder_.date), toDate.getLessThanOrEqual())
                );
            }

            return predicate;
        };
    }

    /**
     * Build specification for total amount range filtering
     */
    private Specification<SaleOrder> buildTotalRangeSpecification(
        tech.jhipster.service.filter.BigDecimalFilter minTotal,
        tech.jhipster.service.filter.BigDecimalFilter maxTotal
    ) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (minTotal != null && minTotal.getGreaterThanOrEqual() != null) {
                predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.greaterThanOrEqualTo(root.get(SaleOrder_.total), minTotal.getGreaterThanOrEqual())
                );
            }

            if (maxTotal != null && maxTotal.getLessThanOrEqual() != null) {
                predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.lessThanOrEqualTo(root.get(SaleOrder_.total), maxTotal.getLessThanOrEqual())
                );
            }

            return predicate;
        };
    }

    /**
     * Build specification for has shipment filtering
     */
    private Specification<SaleOrder> buildHasShipmentSpecification(tech.jhipster.service.filter.BooleanFilter filter) {
        return (root, query, criteriaBuilder) -> {
            if (filter.getEquals() != null) {
                if (filter.getEquals()) {
                    return criteriaBuilder.isNotNull(root.get(SaleOrder_.shipment));
                } else {
                    return criteriaBuilder.isNull(root.get(SaleOrder_.shipment));
                }
            }
            return criteriaBuilder.conjunction();
        };
    }

    /**
     * Build specification for has payment filtering
     */
    private Specification<SaleOrder> buildHasPaymentSpecification(tech.jhipster.service.filter.BooleanFilter filter) {
        return (root, query, criteriaBuilder) -> {
            if (filter.getEquals() != null) {
                if (filter.getEquals()) {
                    return criteriaBuilder.isNotNull(root.get(SaleOrder_.payment));
                } else {
                    return criteriaBuilder.isNull(root.get(SaleOrder_.payment));
                }
            }
            return criteriaBuilder.conjunction();
        };
    }

    /**
     * Static methods for common specifications (for use without criteria object)
     */
    public static Specification<SaleOrder> withClientAccountId(Long clientAccountId) {
        return (root, query, criteriaBuilder) -> {
            if (clientAccountId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(SaleOrder_.clientAccount).get(ClientAccount_.id), clientAccountId);
        };
    }

    public static Specification<SaleOrder> withStatus(OrderStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(SaleOrder_.status), status);
        };
    }

    public static Specification<SaleOrder> withOrderType(OrderType orderType) {
        return (root, query, criteriaBuilder) -> {
            if (orderType == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(SaleOrder_.orderType), orderType);
        };
    }

    public static Specification<SaleOrder> withCustomerId(Long customerId) {
        return (root, query, criteriaBuilder) -> {
            if (customerId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(SaleOrder_.customer).get(Customer_.id), customerId);
        };
    }

    public static Specification<SaleOrder> withReferenceContaining(String reference) {
        return (root, query, criteriaBuilder) -> {
            if (reference == null || reference.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get(SaleOrder_.reference)), "%" + reference.toLowerCase() + "%");
        };
    }

    public static Specification<SaleOrder> withExpiredReservations() {
        return (root, query, criteriaBuilder) -> {
            ZonedDateTime now = ZonedDateTime.now();

            Predicate hasExpiration = criteriaBuilder.isNotNull(root.get(SaleOrder_.reservationExpiresAt));
            Predicate isExpired = criteriaBuilder.lessThan(root.get(SaleOrder_.reservationExpiresAt), now);
            Predicate isConfirmed = criteriaBuilder.equal(root.get(SaleOrder_.status), OrderStatus.CONFIRMED);

            return criteriaBuilder.and(hasExpiration, isExpired, isConfirmed);
        };
    }

    public static Specification<SaleOrder> withDateRange(ZonedDateTime from, ZonedDateTime to) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (from != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get(SaleOrder_.date), from));
            }

            if (to != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get(SaleOrder_.date), to));
            }

            return predicate;
        };
    }

    public static Specification<SaleOrder> isOverdue() {
        return (root, query, criteriaBuilder) -> {
            ZonedDateTime now = ZonedDateTime.now();

            Predicate dueDatePast = criteriaBuilder.lessThan(root.get(SaleOrder_.dueDate), now);
            Predicate notCompleted = criteriaBuilder.notEqual(root.get(SaleOrder_.status), OrderStatus.COMPLETED);
            Predicate notCancelled = criteriaBuilder.notEqual(root.get(SaleOrder_.status), OrderStatus.CANCELLED);

            return criteriaBuilder.and(dueDatePast, notCompleted, notCancelled);
        };
    }

    public static Specification<SaleOrder> withoutPayment() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get(SaleOrder_.payment));
    }

    public static Specification<SaleOrder> withoutShipment() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get(SaleOrder_.shipment));
    }

    public static Specification<SaleOrder> needsShipmentCreation() {
        return (root, query, criteriaBuilder) -> {
            Predicate isConfirmed = criteriaBuilder.equal(root.get(SaleOrder_.status), OrderStatus.CONFIRMED);
            Predicate isDelivery = criteriaBuilder.equal(root.get(SaleOrder_.orderType), OrderType.DELIVERY);
            Predicate noShipment = criteriaBuilder.isNull(root.get(SaleOrder_.shipment));

            return criteriaBuilder.and(isConfirmed, isDelivery, noShipment);
        };
    }

    public static Specification<SaleOrder> createdInLastDays(int days) {
        return (root, query, criteriaBuilder) -> {
            ZonedDateTime cutoffDate = ZonedDateTime.now().minusDays(days);
            return criteriaBuilder.greaterThanOrEqualTo(root.get(SaleOrder_.date), cutoffDate);
        };
    }

    /**
     * Build specification for customer type filtering (managed vs independent)
     */
    public static Specification<SaleOrder> withCustomerType(String customerType) {
        return (root, query, criteriaBuilder) -> {
            if (customerType == null) {
                return criteriaBuilder.conjunction();
            }

            Join<SaleOrder, Customer> customerJoin = root.join(SaleOrder_.customer);

            if ("managed".equalsIgnoreCase(customerType)) {
                return criteriaBuilder.isNotNull(customerJoin.get(Customer_.createdByClientAccount));
            } else if ("independent".equalsIgnoreCase(customerType)) {
                return criteriaBuilder.isNull(customerJoin.get(Customer_.createdByClientAccount));
            }

            return criteriaBuilder.conjunction();
        };
    }
}
