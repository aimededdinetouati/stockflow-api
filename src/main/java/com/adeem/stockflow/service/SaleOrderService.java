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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    private final InventoryService inventoryService;
    private ShipmentService shipmentService;

    public SaleOrderService(
        SaleOrderRepository saleOrderRepository,
        SaleOrderMapper saleOrderMapper,
        InventoryTransactionService inventoryTransactionService,
        CustomerRepository customerRepository,
        ClientAccountRepository clientAccountRepository,
        ProductRepository productRepository,
        InventoryService inventoryService
    ) {
        this.saleOrderRepository = saleOrderRepository;
        this.saleOrderMapper = saleOrderMapper;
        this.inventoryTransactionService = inventoryTransactionService;
        this.customerRepository = customerRepository;
        this.clientAccountRepository = clientAccountRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
    }

    @Autowired
    @Lazy
    public void setShipmentService(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    public void save(SaleOrder saleOrder) {
        saleOrderRepository.save(saleOrder);
    }

    /**
     * Create a saleOrder.
     */
    public SaleOrderDTO create(SaleOrderDTO saleOrderDTO) {
        LOG.debug("Request to save SaleOrder : {}", saleOrderDTO);

        ClientAccount clientAccount = getCurrentClientAccount();
        Customer customer = getAndValidateCustomer(saleOrderDTO.getCustomer().getId());
        validateOrderItems(saleOrderDTO.getOrderItems());

        Set<SaleOrderItem> orderItems = validateAndCreateOrderItems(saleOrderDTO.getOrderItems(), clientAccount.getId());

        SaleOrder saleOrder = buildNewSaleOrder(saleOrderDTO, clientAccount, customer, orderItems);
        calculateOrderTotals(saleOrder, orderItems);

        saleOrder = saleOrderRepository.save(saleOrder);
        return saleOrderMapper.toDto(saleOrder);
    }

    /**
     * Update a saleOrder with business rule validation.
     */
    public SaleOrderDTO update(SaleOrderDTO saleOrderDTO) {
        LOG.debug("Request to update SaleOrder : {}", saleOrderDTO);

        ClientAccount clientAccount = getCurrentClientAccount();
        SaleOrder existingOrder = getAndValidateOrder(saleOrderDTO.getId());
        validateCanModifyOrder(existingOrder);

        Set<SaleOrderItem> newOrderItems = validateAndCreateOrderItems(saleOrderDTO.getOrderItems(), clientAccount.getId());
        updateOrderFields(existingOrder, saleOrderDTO, clientAccount, newOrderItems);
        calculateOrderTotals(existingOrder, newOrderItems);

        existingOrder.setIsPersisted();
        existingOrder = saleOrderRepository.save(existingOrder);
        return saleOrderMapper.toDto(existingOrder);
    }

    /**
     * Get all saleOrders for current client account.
     */
    @Transactional(readOnly = true)
    public Page<SaleOrderDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SaleOrders");
        return findAllWithCriteria(pageable, null);
    }

    /**
     * Get all saleOrders with criteria filtering.
     */
    @Transactional(readOnly = true)
    public Page<SaleOrderDTO> findAllWithCriteria(Pageable pageable, Specification<SaleOrder> spec) {
        LOG.debug("Request to get SaleOrders with criteria");

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();
        Specification<SaleOrder> finalSpec = buildSpecificationWithClientFilter(currentClientAccountId, spec);

        return saleOrderRepository.findAll(finalSpec, pageable).map(saleOrderMapper::toDto);
    }

    /**
     * Get one saleOrder by id.
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

    public Optional<SaleOrder> findById(Long id) {
        return saleOrderRepository.findById(id);
    }

    /**
     * Delete the saleOrder by id.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SaleOrder : {}", id);

        SaleOrder saleOrder = getAndValidateOrder(id);
        validateCanDelete(saleOrder);

        saleOrderRepository.deleteById(id);
    }

    /**
     * Complete an order - transition from SHIPPED to COMPLETED.
     */
    public SaleOrderDTO completeOrder(Long id) {
        LOG.debug("Request to complete SaleOrder : {}", id);

        SaleOrder saleOrder = getAndValidateOrder(id);
        validateCanComplete(saleOrder);

        completeInventoryTransaction(saleOrder);
        updateOrderStatusToCompleted(saleOrder);

        saleOrder = saleOrderRepository.save(saleOrder);
        return saleOrderMapper.toDto(saleOrder);
    }

    /**
     * Confirm an order - transition from DRAFTED to CONFIRMED.
     */
    public SaleOrderDTO confirmOrder(Long id) {
        LOG.debug("Request to confirm SaleOrder : {}", id);

        SaleOrder saleOrder = getAndValidateOrder(id);
        validateCanConfirm(saleOrder);
        validateInventoryAvailability(saleOrder);

        reserveInventoryForOrder(saleOrder);
        updateOrderStatusToConfirmed(saleOrder);

        saleOrder = saleOrderRepository.save(saleOrder);
        return saleOrderMapper.toDto(saleOrder);
    }

    /**
     * Cancel an order.
     */
    public SaleOrderDTO cancelOrder(Long id, CancelOrderDTO cancelRequest) {
        LOG.debug("Request to cancel SaleOrder : {} with reason: {}", id, cancelRequest.getReason());

        SaleOrder saleOrder = getAndValidateOrder(id);
        validateCanCancel(saleOrder);

        handleInventoryOnCancellation(saleOrder);
        handleShipmentOnCancellation(saleOrder);
        updateOrderStatusToCancelled(saleOrder, cancelRequest.getReason());

        saleOrder = saleOrderRepository.save(saleOrder);
        return saleOrderMapper.toDto(saleOrder);
    }

    /**
     * Validate inventory availability for order items.
     */
    public InventoryValidationDTO validateOrderAvailability(List<OrderItemDTO> items) {
        LOG.debug("Request to validate inventory availability for {} items", items.size());

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();
        InventoryValidationDTO result = new InventoryValidationDTO();
        result.setValid(true);

        List<InventoryValidationDTO.InventoryValidationErrorDTO> errors = items
            .stream()
            .map(item -> validateSingleItemAvailability(item, currentClientAccountId))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        result.setErrors(errors);
        if (!errors.isEmpty()) {
            result.setValid(false);
        }

        return result;
    }

    /**
     * Get order statistics for current client account.
     */
    @Transactional(readOnly = true)
    public SaleOrderStatsDTO getOrderStatistics() {
        LOG.debug("Request to get order statistics");

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();
        List<SaleOrder> orders = saleOrderRepository.findByClientAccountId(currentClientAccountId);

        return buildOrderStatistics(orders);
    }

    // Private helper methods - Validation and Retrieval

    private ClientAccount getCurrentClientAccount() {
        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();
        return clientAccountRepository.findById(currentClientAccountId).orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));
    }

    private Customer getAndValidateCustomer(Long customerId) {
        return customerRepository.findById(customerId).orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));
    }

    private SaleOrder getAndValidateOrder(Long orderId) {
        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();
        SaleOrder saleOrder = saleOrderRepository.findById(orderId).orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));

        validateOwnership(saleOrder.getClientAccount().getId(), currentClientAccountId);
        return saleOrder;
    }

    private void validateOwnership(Long entityClientAccountId, Long currentClientAccountId) {
        if (!entityClientAccountId.equals(currentClientAccountId)) {
            throw new AccessDeniedException(Constants.NOT_ALLOWED);
        }
    }

    private void validateOrderItems(Set<SaleOrderItemDTO> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new BadRequestAlertException("order items cannot be null or empty", "", ErrorConstants.REQUIRED_ORDER_ITEMS);
        }
    }

    private void validateCanModifyOrder(SaleOrder order) {
        if (!canModifyOrder(order)) {
            throw new OrderModificationNotAllowedException("Order cannot be modified after confirmation");
        }
    }

    // Add validation method to SaleOrderService.java
    private void validateCanComplete(SaleOrder saleOrder) {
        if (saleOrder.getOrderType() == OrderType.DELIVERY) {
            if (saleOrder.getStatus() != OrderStatus.SHIPPED) {
                throw new InvalidOrderStatusTransitionException(
                    String.format("Cannot complete DELIVERY order with status %s. Order must be SHIPPED.", saleOrder.getStatus())
                );
            }
        } else {
            if (saleOrder.getStatus() != OrderStatus.CONFIRMED) {
                throw new InvalidOrderStatusTransitionException(
                    String.format("Cannot complete order with status %s. Order must be CONFIRMED.", saleOrder.getStatus())
                );
            }
        }
    }

    private void validateCanDelete(SaleOrder saleOrder) {
        if (saleOrder.getStatus() != OrderStatus.DRAFTED) {
            throw new BadRequestAlertException("Cannot delete confirmed orders", "SaleOrder", "cannotDeleteConfirmed");
        }
    }

    private void validateCanConfirm(SaleOrder saleOrder) {
        if (saleOrder.getStatus() != OrderStatus.DRAFTED) {
            throw new InvalidOrderStatusTransitionException(String.format("Cannot confirm order with status %s", saleOrder.getStatus()));
        }
    }

    private void validateCanCancel(SaleOrder saleOrder) {
        if (saleOrder.getStatus() == OrderStatus.COMPLETED) {
            throw new InvalidOrderStatusTransitionException("Cannot cancel completed orders");
        }

        if (saleOrder.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusTransitionException("Order is already canceled");
        }
    }

    // Private helper methods - Business Logic

    private SaleOrder buildNewSaleOrder(SaleOrderDTO dto, ClientAccount clientAccount, Customer customer, Set<SaleOrderItem> orderItems) {
        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setClientAccount(clientAccount);
        saleOrder.setReference(generateReference(clientAccount.getId()));
        saleOrder.setStatus(OrderStatus.DRAFTED);
        saleOrder.setCustomer(customer);
        saleOrder.setReservationExpiresAt(dto.getDate().plusHours(clientAccount.getReservationTimeoutHours()));

        updateOrderCommonFields(saleOrder, dto);
        setOrderItems(saleOrder, orderItems);

        return saleOrder;
    }

    private void updateOrderFields(
        SaleOrder existingOrder,
        SaleOrderDTO dto,
        ClientAccount clientAccount,
        Set<SaleOrderItem> newOrderItems
    ) {
        existingOrder.getOrderItems().clear();
        existingOrder.setReservationExpiresAt(dto.getDate().plusHours(clientAccount.getReservationTimeoutHours()));

        updateOrderCommonFields(existingOrder, dto);
        setOrderItems(existingOrder, newOrderItems);
    }

    private void updateOrderCommonFields(SaleOrder saleOrder, SaleOrderDTO dto) {
        saleOrder.setDate(dto.getDate());
        saleOrder.setNotes(dto.getNotes());
        saleOrder.setShippingCost(dto.getShippingCost());
        saleOrder.setOrderType(dto.getOrderType());
        saleOrder.setSaleType(dto.getSaleType());
        saleOrder.setTvaRate(dto.getTvaRate());
        saleOrder.setStampRate(dto.getStampRate());
        saleOrder.setDiscountRate(dto.getDiscountRate());
        saleOrder.setStampApplied(dto.isStampApplied());
        saleOrder.setTvaApplied(dto.isTvaApplied());
    }

    private void setOrderItems(SaleOrder saleOrder, Set<SaleOrderItem> orderItems) {
        orderItems.forEach(orderItem -> orderItem.setSaleOrder(saleOrder));
        saleOrder.setOrderItems(orderItems);
    }

    private void updateOrderStatusToConfirmed(SaleOrder saleOrder) {
        saleOrder.setStatus(OrderStatus.CONFIRMED);
        ClientAccount clientAccount = saleOrder.getClientAccount();
        if (clientAccount.getReservationTimeoutHours() != null) {
            saleOrder.setReservationExpiresAt(ZonedDateTime.now().plusHours(clientAccount.getReservationTimeoutHours()));
        }
    }

    private void updateOrderStatusToCancelled(SaleOrder saleOrder, String reason) {
        saleOrder.setStatus(OrderStatus.CANCELLED);
    }

    private void updateOrderStatusToCompleted(SaleOrder saleOrder) {
        saleOrder.setStatus(OrderStatus.COMPLETED);
    }

    private void handleInventoryOnCancellation(SaleOrder saleOrder) {
        if (saleOrder.getStatus() == OrderStatus.CONFIRMED || saleOrder.getStatus() == OrderStatus.SHIPPED) {
            releaseReservedInventory(saleOrder);
        }
    }

    private void handleShipmentOnCancellation(SaleOrder saleOrder) {
        if (saleOrder.getShipment() != null) {
            shipmentService.cancelShipment(saleOrder.getShipment().getId());
        }
    }

    private InventoryValidationDTO.InventoryValidationErrorDTO validateSingleItemAvailability(
        OrderItemDTO item,
        Long currentClientAccountId
    ) {
        Optional<Inventory> inventory = inventoryService.findByProductIdAndClientAccountId(item.getProductId(), currentClientAccountId);

        if (inventory.isEmpty()) {
            return createInventoryError(item.getProductId(), null, item.getQuantity(), BigDecimal.ZERO, "Product not found in inventory");
        }

        Inventory inv = inventory.get();
        if (inv.getAvailableQuantity().compareTo(item.getQuantity()) < 0) {
            return createInventoryError(
                item.getProductId(),
                inv.getProduct().getName(),
                item.getQuantity(),
                inv.getAvailableQuantity(),
                "Insufficient inventory"
            );
        }

        return null;
    }

    private InventoryValidationDTO.InventoryValidationErrorDTO createInventoryError(
        Long productId,
        String productName,
        BigDecimal requested,
        BigDecimal available,
        String message
    ) {
        InventoryValidationDTO.InventoryValidationErrorDTO error = new InventoryValidationDTO.InventoryValidationErrorDTO();
        error.setProductId(productId);
        error.setProductName(productName);
        error.setRequestedQuantity(requested);
        error.setAvailableQuantity(available);
        error.setMessage(message);
        return error;
    }

    private SaleOrderStatsDTO buildOrderStatistics(List<SaleOrder> orders) {
        SaleOrderStatsDTO stats = new SaleOrderStatsDTO();

        stats.setTotalOrders((long) orders.size());
        stats.setDraftedOrders(countOrdersByStatus(orders, OrderStatus.DRAFTED));
        stats.setConfirmedOrders(countOrdersByStatus(orders, OrderStatus.CONFIRMED));
        stats.setShippedOrders(countOrdersByStatus(orders, OrderStatus.SHIPPED));
        stats.setCompletedOrders(countOrdersByStatus(orders, OrderStatus.COMPLETED));
        stats.setCancelledOrders(countOrdersByStatus(orders, OrderStatus.CANCELLED));

        BigDecimal totalRevenue = calculateTotalRevenue(orders);
        stats.setTotalRevenue(totalRevenue);

        if (stats.getCompletedOrders() > 0) {
            stats.setAverageOrderValue(totalRevenue.divide(BigDecimal.valueOf(stats.getCompletedOrders()), 2, BigDecimal.ROUND_HALF_UP));
        }

        stats.setDeliveryOrders(countOrdersByType(orders, OrderType.DELIVERY));
        stats.setPickupOrders(countOrdersByType(orders, OrderType.STORE_PICKUP));

        return stats;
    }

    private long countOrdersByStatus(List<SaleOrder> orders, OrderStatus status) {
        return orders.stream().filter(o -> o.getStatus() == status).count();
    }

    private long countOrdersByType(List<SaleOrder> orders, OrderType type) {
        return orders.stream().filter(o -> o.getOrderType() == type).count();
    }

    private BigDecimal calculateTotalRevenue(List<SaleOrder> orders) {
        return orders
            .stream()
            .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
            .map(SaleOrder::getTotal)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Specification<SaleOrder> buildSpecificationWithClientFilter(Long clientAccountId, Specification<SaleOrder> additionalSpec) {
        Specification<SaleOrder> clientSpec = SaleOrderSpecification.withClientAccountId(clientAccountId);
        return additionalSpec != null ? clientSpec.and(additionalSpec) : clientSpec;
    }

    // Existing helper methods - preserved as-is

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
        Product product = productRepository
            .findById(itemDTO.getProduct().getId())
            .orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));

        if (!product.getClientAccount().getId().equals(clientAccountId)) {
            throw new AccessDeniedException(Constants.NOT_ALLOWED);
        }

        if (itemDTO.getQuantity() == null || itemDTO.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestAlertException("Quantity cannot be null or negative", "", ErrorConstants.QUANTITY_INVALID);
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
            .add(saleOrder.getStampAmount() != null ? saleOrder.getStampAmount() : BigDecimal.ZERO)
            .add(saleOrder.getShippingCost() != null ? saleOrder.getShippingCost() : BigDecimal.ZERO);

        saleOrder.setTotal(total);
    }

    private BigDecimal calculateTvaAmount(BigDecimal netTotal) {
        BigDecimal tvaRate = BigDecimal.valueOf(19);
        if (netTotal == null || netTotal.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        return netTotal.multiply(tvaRate).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal calculateStampAmount(BigDecimal netTotal, BigDecimal tvaAmount, BigDecimal stampRate) {
        if (netTotal == null || netTotal.compareTo(BigDecimal.ZERO) <= 0 || stampRate.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal stampBase = netTotal.add(tvaAmount != null ? tvaAmount : BigDecimal.ZERO);
        return stampBase.multiply(stampRate).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }

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
            Optional<Inventory> inventory = inventoryService.findByProductIdAndClientAccountId(
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
        updateInventoryQuantities(saleOrder, TransactionType.RESERVATION);
    }

    private void releaseReservedInventory(SaleOrder saleOrder) {
        updateInventoryQuantities(saleOrder, TransactionType.RESERVATION_RELEASE);
    }

    private void completeInventoryTransaction(SaleOrder saleOrder) {
        updateInventoryQuantities(saleOrder, TransactionType.SALE);
    }

    public void updateInventoryQuantities(SaleOrder saleOrder, TransactionType transactionType) {
        String transactionReference = inventoryTransactionService.generateReference(saleOrder.getClientAccount().getId());
        List<Inventory> inventoriesToSave = new ArrayList<>();
        List<InventoryTransaction> transactionsToSave = new ArrayList<>();

        for (SaleOrderItem item : saleOrder.getOrderItems()) {
            Inventory inventory = inventoryService
                .findByProductIdAndClientAccountId(item.getProduct().getId(), saleOrder.getClientAccount().getId())
                .orElseThrow();
            inventoryService.updateInventoryQuantities(
                inventory,
                item.getQuantity(),
                transactionReference,
                transactionType,
                inventoriesToSave,
                transactionsToSave
            );
            transactionReference = GlobalUtils.generateReference(transactionReference);
        }

        inventoryService.saveAll(inventoriesToSave);
        inventoryTransactionService.saveAll(transactionsToSave);
    }

    public List<SaleOrder> findExpiredReservations(ZonedDateTime time) {
        return saleOrderRepository.findExpiredReservations(time);
    }
}
