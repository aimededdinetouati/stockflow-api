package com.adeem.stockflow.service;

import static com.adeem.stockflow.domain.enumeration.TransactionType.RESERVATION_RELEASE;

import com.adeem.stockflow.domain.SaleOrder;
import com.adeem.stockflow.domain.Shipment;
import com.adeem.stockflow.domain.enumeration.OrderStatus;
import com.adeem.stockflow.domain.enumeration.ShippingStatus;
import com.adeem.stockflow.domain.enumeration.TransactionType;
import com.adeem.stockflow.repository.SaleOrderRepository;
import com.adeem.stockflow.repository.ShipmentRepository;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling scheduled background tasks related to orders and shipments.
 * Handles reservation timeouts, shipment status sync, and other maintenance tasks.
 */
@Service
@Transactional
public class SchedulerService {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerService.class);

    private final SaleOrderService saleOrderService;
    private final InventoryService inventoryService;

    public SchedulerService(SaleOrderService saleOrderService, InventoryService inventoryService) {
        this.saleOrderService = saleOrderService;
        this.inventoryService = inventoryService;
    }

    /**
     * Process expired reservations every 30 minutes.
     * Automatically cancels orders where reservation has expired.
     */
    @Scheduled(fixedRate = 30 * 60 * 1000) // Every 30 minutes
    public void processExpiredReservations() {
        LOG.debug("Processing expired reservations");

        try {
            ZonedDateTime currentTime = ZonedDateTime.now();
            List<SaleOrder> expiredOrders = saleOrderService.findExpiredReservations(currentTime);

            LOG.info("Found {} expired reservations to process", expiredOrders.size());

            for (SaleOrder order : expiredOrders) {
                try {
                    LOG.info("Auto-cancelling expired order: {}", order.getReference());

                    // Release reserved inventory
                    saleOrderService.updateInventoryQuantities(order, RESERVATION_RELEASE);

                    // Update order status
                    order.setStatus(OrderStatus.CANCELLED);
                    order.setNotes(order.getNotes() + "\nAuto-cancelled due to expired reservation at " + currentTime);

                    saleOrderService.save(order);

                    LOG.info("Successfully auto-cancelled order: {}", order.getReference());
                } catch (Exception e) {
                    LOG.error("Failed to process expired reservation for order {}: {}", order.getReference(), e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            LOG.error("Error processing expired reservations: {}", e.getMessage(), e);
        }
    }
    //    @Scheduled(fixedRate = 60 * 60 * 1000) // Every hour
    //    public void syncYalidineShipmentStatuses() {
    //        LOG.debug("Syncing Yalidine shipment statuses");
    //
    //        try {
    //            List<Shipment> yalidineShipments = shipmentRepository.findYalidineShipmentsNeedingSync();
    //
    //            LOG.info("Found {} Yalidine shipments to sync", yalidineShipments.size());
    //
    //            for (Shipment shipment : yalidineShipments) {
    //                try {
    //                    LOG.debug("Syncing Yalidine shipment: {}", shipment.getReference());
    //
    //                    // Get tracking info from Yalidine
    //                    YalidineTrackingResponse tracking = yalidineIntegrationService.getTrackingInfo(
    //                        shipment.getYalidineShipmentId());
    //
    //                    // Update shipment status based on Yalidine response
    //                    updateShipmentFromYalidineTracking(shipment, tracking);
    //
    //                    LOG.debug("Successfully synced shipment: {}", shipment.getReference());
    //
    //                } catch (Exception e) {
    //                    LOG.warn("Failed to sync Yalidine shipment {}: {}",
    //                        shipment.getReference(), e.getMessage());
    //                }
    //            }
    //
    //        } catch (Exception e) {
    //            LOG.error("Error syncing Yalidine shipment statuses: {}", e.getMessage(), e);
    //        }
    //    }

    //    private void updateShipmentFromYalidineTracking(Shipment shipment, YalidineTrackingResponse tracking) {
    //        try {
    //            boolean statusChanged = false;
    //
    //            // Map Yalidine status to our status
    //            ShippingStatus newStatus = mapYalidineStatusToShippingStatus(tracking.getStatus());
    //
    //            if (newStatus != null && !newStatus.equals(shipment.getStatus())) {
    //                LOG.info("Updating shipment {} status from {} to {}",
    //                    shipment.getReference(), shipment.getStatus(), newStatus);
    //
    //                shipment.setStatus(newStatus);
    //                statusChanged = true;
    //
    //                // Set delivery date if delivered
    //                if (newStatus == ShippingStatus.DELIVERED) {
    //                    shipment.setActualDeliveryDate(Instant.now());
    //
    //                    // Complete the associated order
    //                    if (shipment.getSaleOrder() != null) {
    //                        SaleOrder order = shipment.getSaleOrder();
    //                        order.setStatus(OrderStatus.COMPLETED);
    //                        saleOrderRepository.save(order);
    //
    //                        // Complete inventory transaction
    //                        completeInventoryTransactionForOrder(order);
    //                    }
    //                }
    //            }
    //
    //            // Update notes with tracking info
    //            if (tracking.getCurrentLocation() != null) {
    //                shipment.setNotes(shipment.getNotes() +
    //                    "\nYalidine sync: " + tracking.getCurrentLocation() + " at " + Instant.now());
    //                statusChanged = true;
    //            }
    //
    //            if (statusChanged) {
    //                shipmentRepository.save(shipment);
    //            }
    //
    //        } catch (Exception e) {
    //            LOG.error("Failed to update shipment from Yalidine tracking: {}", e.getMessage());
    //        }
    //    }

    //    private ShippingStatus mapYalidineStatusToShippingStatus(String yalidineStatus) {
    //        if (yalidineStatus == null) {
    //            return null;
    //        }
    //
    //        return switch (yalidineStatus.toUpperCase()) {
    //            case "PENDING", "CREATED" -> ShippingStatus.PENDING;
    //            case "PROCESSING", "PICKED_UP" -> ShippingStatus.PROCESSING;
    //            case "SHIPPED", "IN_TRANSIT" -> ShippingStatus.SHIPPED;
    //            case "DELIVERED", "COMPLETED" -> ShippingStatus.DELIVERED;
    //            case "FAILED", "CANCELLED", "RETURNED" -> ShippingStatus.FAILED;
    //            default -> null;
    //        };
    //    }
    //
    //    private void completeInventoryTransactionForOrder(SaleOrder order) {
    //        try {
    //            order.getOrderItems().forEach(item -> {
    //                try {
    //                    inventoryService.createTransaction(
    //                        item.getProduct().getId(),
    //                        TransactionType.SALE,
    //                        item.getQuantity(),
    //                        order.getReference(),
    //                        "Sale completed for order " + order.getReference()
    //                    );
    //                } catch (Exception e) {
    //                    LOG.error("Failed to complete inventory transaction for item {} in order {}: {}",
    //                        item.getProduct().getName(), order.getReference(), e.getMessage());
    //                }
    //            });
    //        } catch (Exception e) {
    //            LOG.error("Failed to complete inventory transactions for order {}: {}",
    //                order.getReference(), e.getMessage());
    //        }
    //    }
}
