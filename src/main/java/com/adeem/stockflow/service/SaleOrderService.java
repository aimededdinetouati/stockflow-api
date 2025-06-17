package com.adeem.stockflow.service;

import com.adeem.stockflow.config.Constants;
import com.adeem.stockflow.domain.*;
import com.adeem.stockflow.domain.enumeration.*;
import com.adeem.stockflow.repository.*;
import com.adeem.stockflow.security.SecurityUtils;
import com.adeem.stockflow.service.criteria.SaleOrderSpecification;
import com.adeem.stockflow.service.dto.*;
import com.adeem.stockflow.service.exceptions.*;
import com.adeem.stockflow.service.mapper.SaleOrderMapper;
import com.adeem.stockflow.service.util.GlobalUtils;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link SaleOrder}.
 * Enhanced with multi-tenant security, inventory reservation, and delivery management.
 */
@Service
@Transactional
public class SaleOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(SaleOrderService.class);

    private final SaleOrderRepository saleOrderRepository;
    private final SaleOrderMapper saleOrderMapper;
    private final InventoryTransactionService inventoryTransactionService;
    private final CustomerRepository customerRepository;
    private final ClientAccountRepository clientAccountRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ShipmentService shipmentService;
    private final SaleOrderItemService saleOrderItemService;

    public SaleOrderService(
        SaleOrderRepository saleOrderRepository,
        SaleOrderMapper saleOrderMapper,
        InventoryTransactionService inventoryTransactionService,
        CustomerRepository customerRepository,
        ClientAccountRepository clientAccountRepository,
        ProductRepository productRepository,
        InventoryRepository inventoryRepository,
        ShipmentService shipmentService,
        SaleOrderItemService saleOrderItemService
    ) {
        this.saleOrderRepository = saleOrderRepository;
        this.saleOrderMapper = saleOrderMapper;
        this.inventoryTransactionService = inventoryTransactionService;
        this.customerRepository = customerRepository;
        this.clientAccountRepository = clientAccountRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.shipmentService = shipmentService;
        this.saleOrderItemService = saleOrderItemService;
    }

    /**
     * Create a saleOrder.
     *
     * @param saleOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public SaleOrderDTO create(SaleOrderDTO saleOrderDTO) {
        LOG.debug("Request to save SaleOrder : {}", saleOrderDTO);

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Validate customer relationship
        Customer customer = customerRepository
            .findById(saleOrderDTO.getCustomer().getId())
            .orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));

        if (saleOrderDTO.getOrderItems() == null || saleOrderDTO.getOrderItems().isEmpty()) {
            throw new BadRequestAlertException("order items cannot be null or empty", "", ErrorConstants.REQUIRED_ORDER_ITEMS);
        }

        ClientAccount clientAccount = clientAccountRepository
            .findById(currentClientAccountId)
            .orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));

        Set<SaleOrderItem> orderItems = validateAndCreateOrderItems(saleOrderDTO.getOrderItems(), currentClientAccountId);

        // Generate reference if creating new order
        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setClientAccount(clientAccount);
        saleOrder.setReference(generateReference(clientAccount.getId()));
        saleOrder.setStatus(OrderStatus.DRAFTED);
        saleOrder.setDate(saleOrderDTO.getDate());
        saleOrder.setNotes(saleOrderDTO.getNotes());
        saleOrder.setReservationExpiresAt(saleOrderDTO.getDate().plusHours(clientAccount.getReservationTimeoutHours()));
        saleOrder.setShippingCost(saleOrderDTO.getShippingCost());
        saleOrder.setOrderType(saleOrderDTO.getOrderType());
        saleOrder.setSaleType(saleOrderDTO.getSaleType());
        saleOrder.setTvaRate(saleOrderDTO.getTvaRate());
        saleOrder.setStampRate(saleOrderDTO.getStampRate());
        saleOrder.setDiscountRate(saleOrderDTO.getDiscountRate());
        saleOrder.setStampApplied(saleOrderDTO.isStampApplied());
        saleOrder.setTvaApplied(saleOrderDTO.isTvaApplied());
        saleOrder.setCustomer(customer);

        SaleOrder finalSaleOrder = saleOrder;
        orderItems.forEach(orderItem -> orderItem.setSaleOrder(finalSaleOrder));
        saleOrder.setOrderItems(orderItems);

        // Calculate totals
        calculateOrderTotals(saleOrder, orderItems);

        saleOrder = saleOrderRepository.save(saleOrder);
        return saleOrderMapper.toDto(saleOrder);
    }

    /**
     * Update a saleOrder with business rule validation.
     *
     * @param saleOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public SaleOrderDTO update(SaleOrderDTO saleOrderDTO) {
        LOG.debug("Request to update SaleOrder : {}", saleOrderDTO);

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();
        ClientAccount clientAccount = clientAccountRepository
            .findById(currentClientAccountId)
            .orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));

        SaleOrder existingOrder = saleOrderRepository
            .findById(saleOrderDTO.getId())
            .orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));

        // Validate ownership
        if (!existingOrder.getClientAccount().getId().equals(currentClientAccountId)) {
            throw new AccessDeniedException(Constants.NOT_ALLOWED);
        }

        // Validate modification is allowed
        if (!canModifyOrder(existingOrder)) {
            throw new OrderModificationNotAllowedException("Order cannot be modified after confirmation");
        }

        existingOrder.getOrderItems().clear();
        Set<SaleOrderItem> newOrderItems = validateAndCreateOrderItems(saleOrderDTO.getOrderItems(), currentClientAccountId);

        existingOrder.setDate(saleOrderDTO.getDate());
        existingOrder.setNotes(saleOrderDTO.getNotes());
        existingOrder.setReservationExpiresAt(saleOrderDTO.getDate().plusHours(clientAccount.getReservationTimeoutHours()));
        existingOrder.setShippingCost(saleOrderDTO.getShippingCost());
        existingOrder.setOrderType(saleOrderDTO.getOrderType());
        existingOrder.setSaleType(saleOrderDTO.getSaleType());
        existingOrder.setTvaRate(saleOrderDTO.getTvaRate());
        existingOrder.setStampRate(saleOrderDTO.getStampRate());
        existingOrder.setDiscountRate(saleOrderDTO.getDiscountRate());
        existingOrder.setStampApplied(saleOrderDTO.isStampApplied());
        existingOrder.setTvaApplied(saleOrderDTO.isTvaApplied());
        SaleOrder finalSaleOrder = existingOrder;
        newOrderItems.forEach(orderItem -> orderItem.setSaleOrder(finalSaleOrder));
        existingOrder.setOrderItems(newOrderItems);

        // Calculate totals
        calculateOrderTotals(existingOrder, newOrderItems);

        existingOrder.setIsPersisted();
        existingOrder = saleOrderRepository.save(existingOrder);
        return saleOrderMapper.toDto(existingOrder);
    }

    /**
     * Get all saleOrders for current client account.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SaleOrderDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SaleOrders");

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();

        Specification<SaleOrder> spec = SaleOrderSpecification.withClientAccountId(currentClientAccountId);
        return saleOrderRepository.findAll(spec, pageable).map(saleOrderMapper::toDto);
    }

    /**
     * Get all saleOrders with criteria filtering.
     *
     * @param pageable the pagination information.
     * @param spec the specification for filtering.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SaleOrderDTO> findAllWithCriteria(Pageable pageable, Specification<SaleOrder> spec) {
        LOG.debug("Request to get SaleOrders with criteria");

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Always add client account filter
        Specification<SaleOrder> finalSpec = Specification.where(SaleOrderSpecification.withClientAccountId(currentClientAccountId));
        if (spec != null) {
            finalSpec = finalSpec.and(spec);
        }

        return saleOrderRepository.findAll(finalSpec, pageable).map(saleOrderMapper::toDto);
    }

    /**
     * Get one saleOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SaleOrderDTO> findOne(Long id) {
        LOG.debug("Request to get SaleOrder : {}", id);

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();

        return saleOrderRepository
            .findById(id)
            .filter(order -> order.getClientAccount().getId().equals(currentClientAccountId))
            .map(saleOrderMapper::toDto);
    }

    /**
     * Delete the saleOrder by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SaleOrder : {}", id);

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();

        SaleOrder saleOrder = saleOrderRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Sale order not found", "SaleOrder", "orderNotFound"));

        // Validate ownership
        if (!saleOrder.getClientAccount().getId().equals(currentClientAccountId)) {
            throw new BadRequestAlertException("Access denied", "SaleOrder", "accessDenied");
        }

        // Only allow deletion of drafted orders
        if (saleOrder.getStatus() != OrderStatus.DRAFTED) {
            throw new BadRequestAlertException("Cannot delete confirmed orders", "SaleOrder", "cannotDeleteConfirmed");
        }

        saleOrderRepository.deleteById(id);
    }

    /**
     * Confirm an order - transition from DRAFTED to CONFIRMED.
     *
     * @param id the order id.
     * @return the updated order.
     */
    public SaleOrderDTO confirmOrder(Long id) {
        LOG.debug("Request to confirm SaleOrder : {}", id);

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();

        SaleOrder saleOrder = saleOrderRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Sale order not found", "SaleOrder", "orderNotFound"));

        // Validate ownership
        if (!saleOrder.getClientAccount().getId().equals(currentClientAccountId)) {
            throw new BadRequestAlertException("Access denied", "SaleOrder", "accessDenied");
        }

        // Validate status transition
        if (saleOrder.getStatus() != OrderStatus.DRAFTED) {
            throw new InvalidOrderStatusTransitionException(String.format("Cannot confirm order with status %s", saleOrder.getStatus()));
        }

        // Validate inventory availability
        validateInventoryAvailability(saleOrder);

        // Reserve inventory
        reserveInventoryForOrder(saleOrder);

        // Update order status and set expiration
        saleOrder.setStatus(OrderStatus.CONFIRMED);
        ClientAccount clientAccount = saleOrder.getClientAccount();
        if (clientAccount.getReservationTimeoutHours() != null) {
            saleOrder.setReservationExpiresAt(ZonedDateTime.now().plusHours(clientAccount.getReservationTimeoutHours()));
        }

        saleOrder = saleOrderRepository.save(saleOrder);
        return saleOrderMapper.toDto(saleOrder);
    }

    /**
     * Cancel an order.
     *
     * @param id the order id.
     * @param cancelRequest the cancellation details.
     * @return the updated order.
     */
    public SaleOrderDTO cancelOrder(Long id, CancelOrderDTO cancelRequest) {
        LOG.debug("Request to cancel SaleOrder : {} with reason: {}", id, cancelRequest.getReason());

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();

        SaleOrder saleOrder = saleOrderRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Sale order not found", "SaleOrder", "orderNotFound"));

        // Validate ownership
        if (!saleOrder.getClientAccount().getId().equals(currentClientAccountId)) {
            throw new BadRequestAlertException("Access denied", "SaleOrder", "accessDenied");
        }

        // Cannot cancel completed orders
        if (saleOrder.getStatus() == OrderStatus.COMPLETED) {
            throw new InvalidOrderStatusTransitionException("Cannot cancel completed orders");
        }

        // Release reserved inventory if order was confirmed
        if (saleOrder.getStatus() == OrderStatus.CONFIRMED || saleOrder.getStatus() == OrderStatus.SHIPPED) {
            releaseReservedInventory(saleOrder);
        }

        // Cancel shipment if exists
        if (saleOrder.getShipment() != null) {
            // Cancel with Yalidine or mark as cancelled
            shipmentService.cancelShipment(saleOrder.getShipment().getId());
        }

        saleOrder.setStatus(OrderStatus.CANCELLED);
        saleOrder.setNotes(saleOrder.getNotes() + "\nCancellation reason: " + cancelRequest.getReason());

        saleOrder = saleOrderRepository.save(saleOrder);
        return saleOrderMapper.toDto(saleOrder);
    }

    /**
     * Mark order as picked up (for STORE_PICKUP orders).
     *
     * @param id the order id.
     * @return the updated order.
     */
    public SaleOrderDTO markOrderPickedUp(Long id) {
        LOG.debug("Request to mark SaleOrder as picked up : {}", id);

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();

        SaleOrder saleOrder = saleOrderRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Sale order not found", "SaleOrder", "orderNotFound"));

        // Validate ownership
        if (!saleOrder.getClientAccount().getId().equals(currentClientAccountId)) {
            throw new BadRequestAlertException("Access denied", "SaleOrder", "accessDenied");
        }

        // Validate order type and status
        if (saleOrder.getOrderType() != OrderType.STORE_PICKUP) {
            throw new BadRequestAlertException("Order is not a store pickup order", "SaleOrder", "notStorePickup");
        }

        if (saleOrder.getStatus() != OrderStatus.CONFIRMED) {
            throw new InvalidOrderStatusTransitionException(
                String.format("Cannot mark order with status %s as picked up", saleOrder.getStatus())
            );
        }

        // Update status and complete inventory transaction
        saleOrder.setStatus(OrderStatus.PICKED_UP);
        completeInventoryTransaction(saleOrder);

        saleOrder = saleOrderRepository.save(saleOrder);
        return saleOrderMapper.toDto(saleOrder);
    }

    /**
     * Validate inventory availability for order items.
     *
     * @param items the order items to validate.
     * @return validation result.
     */
    public InventoryValidationDTO validateOrderAvailability(List<OrderItemDTO> items) {
        LOG.debug("Request to validate inventory availability for {} items", items.size());

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();

        InventoryValidationDTO result = new InventoryValidationDTO();
        result.setValid(true);
        result.setErrors(
            items
                .stream()
                .map(item -> {
                    Optional<Inventory> inventory = inventoryRepository.findByProductIdAndClientAccountId(
                        item.getProductId(),
                        currentClientAccountId
                    );

                    if (inventory.isEmpty()) {
                        InventoryValidationDTO.InventoryValidationErrorDTO error = new InventoryValidationDTO.InventoryValidationErrorDTO();
                        error.setProductId(item.getProductId());
                        error.setRequestedQuantity(item.getQuantity());
                        error.setAvailableQuantity(BigDecimal.ZERO);
                        error.setMessage("Product not found in inventory");
                        result.setValid(false);
                        return error;
                    }

                    Inventory inv = inventory.get();
                    if (inv.getAvailableQuantity().compareTo(item.getQuantity()) < 0) {
                        InventoryValidationDTO.InventoryValidationErrorDTO error = new InventoryValidationDTO.InventoryValidationErrorDTO();
                        error.setProductId(item.getProductId());
                        error.setProductName(inv.getProduct().getName());
                        error.setRequestedQuantity(item.getQuantity());
                        error.setAvailableQuantity(inv.getAvailableQuantity());
                        error.setMessage("Insufficient inventory");
                        result.setValid(false);
                        return error;
                    }

                    return null;
                })
                .filter(error -> error != null)
                .collect(Collectors.toList())
        );

        return result;
    }

    /**
     * Get order statistics for current client account.
     *
     * @return statistics DTO.
     */
    @Transactional(readOnly = true)
    public SaleOrderStatsDTO getOrderStatistics() {
        LOG.debug("Request to get order statistics");

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Implementation would calculate various statistics
        // This is a simplified version - you'd implement proper repository queries
        SaleOrderStatsDTO stats = new SaleOrderStatsDTO();

        List<SaleOrder> orders = saleOrderRepository.findByClientAccountId(currentClientAccountId);

        stats.setTotalOrders((long) orders.size());
        stats.setDraftedOrders(orders.stream().filter(o -> o.getStatus() == OrderStatus.DRAFTED).count());
        stats.setConfirmedOrders(orders.stream().filter(o -> o.getStatus() == OrderStatus.CONFIRMED).count());
        stats.setShippedOrders(orders.stream().filter(o -> o.getStatus() == OrderStatus.SHIPPED).count());
        stats.setCompletedOrders(orders.stream().filter(o -> o.getStatus() == OrderStatus.COMPLETED).count());
        stats.setCancelledOrders(orders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count());

        BigDecimal totalRevenue = orders
            .stream()
            .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
            .map(SaleOrder::getTotal)
            .filter(total -> total != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalRevenue(totalRevenue);

        if (stats.getCompletedOrders() > 0) {
            stats.setAverageOrderValue(totalRevenue.divide(BigDecimal.valueOf(stats.getCompletedOrders()), 2, BigDecimal.ROUND_HALF_UP));
        }

        stats.setDeliveryOrders(orders.stream().filter(o -> o.getOrderType() == OrderType.DELIVERY).count());
        stats.setPickupOrders(orders.stream().filter(o -> o.getOrderType() == OrderType.STORE_PICKUP).count());

        return stats;
    }

    // Private helper methods

    private void validateCustomerRelationship(Long customerId, Long clientAccountId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));
    }

    private Set<SaleOrderItem> validateAndCreateOrderItems(Set<SaleOrderItemDTO> itemDTOs, Long clientAccountId) {
        if (itemDTOs == null || itemDTOs.isEmpty()) {
            throw new BadRequestAlertException("Order must contain at least one item", "SaleOrder", "noItems");
        }

        Set<SaleOrderItem> orderItems = new HashSet<>();

        for (SaleOrderItemDTO itemDTO : itemDTOs) {
            SaleOrderItem orderItem = createAndValidateOrderItem(itemDTO, clientAccountId);
            orderItems.add(orderItem);
        }

        return orderItems;
    }

    private SaleOrderItem createAndValidateOrderItem(SaleOrderItemDTO itemDTO, Long clientAccountId) {
        // Validate product exists and belongs to company
        Product product = productRepository
            .findById(itemDTO.getProduct().getId())
            .orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));

        if (!product.getClientAccount().getId().equals(clientAccountId)) {
            throw new AccessDeniedException(Constants.NOT_ALLOWED);
        }

        BigDecimal unitPrice = itemDTO.getUnitPrice() != null ? itemDTO.getUnitPrice() : product.getSellingPrice();

        if (unitPrice == null) {
            throw new BadRequestAlertException("Product price not set", "SaleOrderItem", ErrorConstants.REQUIRED_UNIT_PRICE);
        }

        SaleOrderItem orderItem = new SaleOrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(itemDTO.getQuantity());
        orderItem.setUnitPrice(unitPrice);
        orderItem.setTotal(itemDTO.getQuantity().multiply(unitPrice));

        return orderItem;
    }

    private String generateReference(Long clientAccountId) {
        String reference = saleOrderRepository.getLastReference(clientAccountId).orElse(null);
        return GlobalUtils.generateReference(reference);
    }

    private void calculateOrderTotals(SaleOrder saleOrder, Set<SaleOrderItem> orderItems) {
        BigDecimal subTotal = orderItems
            .stream()
            .map(SaleOrderItem::getTotal)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        saleOrder.setSubTotal(subTotal);

        BigDecimal discountAmount = calculateDiscountAmount(subTotal, saleOrder.getDiscountRate());
        saleOrder.setDiscountAmount(discountAmount);

        BigDecimal netTotal = subTotal.subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);

        if (saleOrder.isTvaApplied()) {
            BigDecimal tvaAmount = calculateTvaAmount(netTotal);
            saleOrder.setTvaAmount(tvaAmount);
        }

        if (saleOrder.isStampApplied()) {
            BigDecimal stampRate = calculateStampRate(netTotal);
            BigDecimal stampAmount = calculateStampAmount(netTotal, saleOrder.getTvaAmount(), stampRate);
            saleOrder.setStampAmount(stampAmount);
            saleOrder.setStampRate(stampRate);
        }

        BigDecimal total = netTotal
            .add(saleOrder.getTvaAmount() != null ? saleOrder.getTvaAmount() : BigDecimal.ZERO)
            .add(saleOrder.getStampAmount() != null ? saleOrder.getStampAmount() : BigDecimal.ZERO);

        saleOrder.setTotal(total);
    }

    private BigDecimal calculateTvaAmount(BigDecimal netTotal) {
        BigDecimal tvaRate = BigDecimal.valueOf(19);
        if (netTotal == null || netTotal.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        return netTotal.multiply(tvaRate).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal calculateStampAmount(BigDecimal netTotal, BigDecimal tvaAmount, BigDecimal stampRate) {
        if (
            netTotal == null || netTotal.compareTo(BigDecimal.ZERO) <= 0 || stampRate.compareTo(BigDecimal.ZERO) <= 0
        ) return BigDecimal.ZERO;
        BigDecimal stampBase = netTotal.add(tvaAmount != null ? tvaAmount : BigDecimal.ZERO);
        // Rate still based on netTotal

        return stampBase.multiply(stampRate).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    // Update stamp rate calculation to use netTotal instead of subTotal
    private BigDecimal calculateStampRate(BigDecimal netTotal) {
        if (netTotal == null) return BigDecimal.ZERO;

        if (netTotal.compareTo(BigDecimal.valueOf(30000)) < 0) {
            return BigDecimal.valueOf(1);
        }
        if (netTotal.compareTo(BigDecimal.valueOf(100000)) <= 0) {
            return BigDecimal.valueOf(1.5);
        }
        return BigDecimal.valueOf(2);
    }

    private BigDecimal calculateDiscountAmount(BigDecimal subTotal, BigDecimal discountRate) {
        if (discountRate == null || discountRate.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        return subTotal.multiply(discountRate).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    private boolean canModifyOrder(SaleOrder order) {
        return order.getStatus() == OrderStatus.DRAFTED;
    }

    private void validateInventoryAvailability(SaleOrder saleOrder) {
        for (SaleOrderItem item : saleOrder.getOrderItems()) {
            Optional<Inventory> inventory = inventoryRepository.findByProductIdAndClientAccountId(
                item.getProduct().getId(),
                saleOrder.getClientAccount().getId()
            );

            if (inventory.isEmpty()) {
                throw new InsufficientInventoryException(String.format("Product %s not found in inventory", item.getProduct().getName()));
            }

            if (inventory.get().getAvailableQuantity().compareTo(item.getQuantity()) < 0) {
                throw new InsufficientInventoryException(
                    String.format(
                        "Product %s has only %s units available",
                        item.getProduct().getName(),
                        inventory.get().getAvailableQuantity()
                    )
                );
            }
        }
    }

    private void reserveInventoryForOrder(SaleOrder saleOrder) {
        for (SaleOrderItem item : saleOrder.getOrderItems()) {
            inventoryTransactionService.save(item.getProduct(), item.getQuantity(), TransactionType.RESERVATION);
        }
    }

    private void releaseReservedInventory(SaleOrder saleOrder) {
        for (SaleOrderItem item : saleOrder.getOrderItems()) {
            inventoryTransactionService.save(item.getProduct(), item.getQuantity(), TransactionType.RESERVATION_RELEASE);
        }
    }

    private void completeInventoryTransaction(SaleOrder saleOrder) {
        for (SaleOrderItem item : saleOrder.getOrderItems()) {
            inventoryTransactionService.save(item.getProduct(), item.getQuantity(), TransactionType.SALE);
        }
    }
}
