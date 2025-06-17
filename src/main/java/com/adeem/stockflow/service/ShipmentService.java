package com.adeem.stockflow.service;

import com.adeem.stockflow.config.Constants;
import com.adeem.stockflow.domain.*;
import com.adeem.stockflow.domain.enumeration.*;
import com.adeem.stockflow.repository.*;
import com.adeem.stockflow.security.SecurityUtils;
import com.adeem.stockflow.service.criteria.ShipmentSpecification;
import com.adeem.stockflow.service.dto.*;
import com.adeem.stockflow.service.dto.yalidine.CreateYalidineShipmentRequest;
import com.adeem.stockflow.service.dto.yalidine.YalidineShipmentResponse;
import com.adeem.stockflow.service.exceptions.*;
import com.adeem.stockflow.service.mapper.ShipmentMapper;
import com.adeem.stockflow.service.util.GlobalUtils;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Shipment}.
 */
@Service
@Transactional
public class ShipmentService {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentService.class);

    private final ShipmentRepository shipmentRepository;
    private final ShipmentMapper shipmentMapper;
    private final SaleOrderRepository saleOrderRepository;
    private final AddressRepository addressRepository;
    private final ClientAccountRepository clientAccountRepository;
    private final YalidineIntegrationService yalidineIntegrationService;

    public ShipmentService(
        ShipmentRepository shipmentRepository,
        ShipmentMapper shipmentMapper,
        SaleOrderRepository saleOrderRepository,
        AddressRepository addressRepository,
        ClientAccountRepository clientAccountRepository,
        YalidineIntegrationService yalidineIntegrationService
    ) {
        this.shipmentRepository = shipmentRepository;
        this.shipmentMapper = shipmentMapper;
        this.saleOrderRepository = saleOrderRepository;
        this.addressRepository = addressRepository;
        this.clientAccountRepository = clientAccountRepository;
        this.yalidineIntegrationService = yalidineIntegrationService;
    }

    /**
     * Create shipment for a specific order.
     *
     * @param orderId the order ID.
     * @param createShipmentDTO the shipment creation details.
     * @return the created shipment.
     */
    public ShipmentDTO createShipmentForOrder(Long orderId, ShipmentRequestDTO createShipmentDTO) {
        LOG.debug("Request to create Shipment for Order : {} with carrier: {}", orderId, createShipmentDTO.getCarrier());

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();

        SaleOrder saleOrder = saleOrderRepository.findById(orderId).orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));

        // Validate ownership
        if (!saleOrder.getClientAccount().getId().equals(currentClientAccountId)) {
            throw new AccessDeniedException(Constants.NOT_ALLOWED);
        }

        // Validate order can be shipped
        validateOrderCanBeShipped(saleOrder);

        // Check if shipment already exists
        if (saleOrder.getShipment() != null) {
            throw new BadRequestAlertException("Shipment already exists for this order", "Shipment", "shipmentExists");
        }

        // Create shipment
        Shipment shipment = new Shipment();
        shipment.setReference(generateReference(saleOrder.getClientAccount().getId()));
        shipment.setCarrier(createShipmentDTO.getCarrier());
        shipment.setNotes(createShipmentDTO.getNotes());
        shipment.setWeight(createShipmentDTO.getWeight());
        shipment.setStatus(ShippingStatus.PENDING);
        shipment.setSaleOrder(saleOrder);
        shipment.setClientAccount(saleOrder.getClientAccount());

        // Set address
        if (createShipmentDTO.getAddressId() != null) {
            Address address = addressRepository
                .findById(createShipmentDTO.getAddressId())
                .orElseThrow(() -> new BadRequestAlertException("Address not found", "Shipment", "addressNotFound"));
            shipment.setAddress(address);
        }

        // Handle Yalidine integration or manual carrier management
        //        if ("YALIDINE".equalsIgnoreCase(createShipmentDTO.getCarrier()) &&
        //            saleOrder.getClientAccount().getYalidineEnabled() != null &&
        //            saleOrder.getClientAccount().getYalidineEnabled()) {
        //
        //            // Create shipment with Yalidine API
        //            createYalidineShipment(shipment, saleOrder);
        //        } else {
        //            // Manual carrier management
        //            shipment.setShippingDate(Instant.now());
        //        }

        shipment.setShippingDate(Instant.now());
        shipment = shipmentRepository.save(shipment);

        // Update order status to SHIPPED
        saleOrder.setStatus(OrderStatus.SHIPPED);
        saleOrderRepository.save(saleOrder);

        return shipmentMapper.toDto(shipment);
    }

    /**
     * Update a shipment.
     *
     * @param shipmentDTO the entity to save.
     * @return the persisted entity.
     */
    public ShipmentDTO update(ShipmentRequestDTO shipmentDTO) {
        LOG.debug("Request to save Shipment : {}", shipmentDTO);

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();

        Shipment shipment = shipmentRepository
            .findById(shipmentDTO.getId())
            .orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));
        shipment.setCarrier(shipmentDTO.getCarrier());
        shipment.setNotes(shipmentDTO.getNotes());
        shipment.setWeight(shipmentDTO.getWeight());
        if (shipmentDTO.getAddressId() != null) {
            Address address = addressRepository
                .findById(shipmentDTO.getAddressId())
                .orElseThrow(() -> new BadRequestAlertException("Address not found", "Shipment", "addressNotFound"));
            shipment.setAddress(address);
        }

        shipment.setIsPersisted();
        shipment = shipmentRepository.save(shipment);
        return shipmentMapper.toDto(shipment);
    }

    /**
     * Get all shipments for current client account.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ShipmentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Shipments");

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();

        Specification<Shipment> spec = ShipmentSpecification.withClientAccountId(currentClientAccountId);
        return shipmentRepository.findAll(spec, pageable).map(shipmentMapper::toDto);
    }

    /**
     * Get all shipments with criteria filtering.
     *
     * @param pageable the pagination information.
     * @param spec the specification for filtering.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ShipmentDTO> findAllWithCriteria(Pageable pageable, Specification<Shipment> spec) {
        LOG.debug("Request to get Shipments with criteria");

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();

        // Always add client account filter
        Specification<Shipment> finalSpec = Specification.where(ShipmentSpecification.withClientAccountId(currentClientAccountId));
        if (spec != null) {
            finalSpec = finalSpec.and(spec);
        }

        return shipmentRepository.findAll(finalSpec, pageable).map(shipmentMapper::toDto);
    }

    /**
     * Get one shipment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ShipmentDTO> findOne(Long id) {
        LOG.debug("Request to get Shipment : {}", id);

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();

        return shipmentRepository
            .findById(id)
            .filter(shipment -> shipment.getClientAccount().getId().equals(currentClientAccountId))
            .map(shipmentMapper::toDto);
    }

    /**
     * Delete the shipment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Shipment : {}", id);

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();

        Shipment shipment = shipmentRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Shipment not found", "Shipment", "shipmentNotFound"));

        // Validate ownership
        if (!shipment.getClientAccount().getId().equals(currentClientAccountId)) {
            throw new AccessDeniedException(Constants.NOT_ALLOWED);
        }

        // Only allow deletion of pending shipments
        if (shipment.getStatus() != ShippingStatus.PENDING) {
            throw new BadRequestAlertException("Cannot delete processed shipments", "Shipment", "cannotDeleteProcessed");
        }

        shipmentRepository.deleteById(id);
    }

    /**
     * Update shipment status.
     *
     * @param id the shipment id.
     * @param request the status update request.
     * @return the updated shipment.
     */
    public ShipmentDTO updateShipmentStatus(Long id, UpdateShipmentStatusDTO request) {
        LOG.debug("Request to update Shipment status : {} to {}", id, request.getStatus());

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();

        Shipment shipment = shipmentRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Shipment not found", "Shipment", "shipmentNotFound"));

        // Validate ownership
        if (!shipment.getClientAccount().getId().equals(currentClientAccountId)) {
            throw new AccessDeniedException(Constants.NOT_ALLOWED);
        }

        // Update status and relevant fields
        shipment.setStatus(request.getStatus());
        if (request.getNotes() != null) {
            shipment.setNotes(shipment.getNotes() + "\n" + request.getNotes());
        }

        // Set delivery date if delivered
        if (request.getStatus() == ShippingStatus.DELIVERED) {
            shipment.setActualDeliveryDate(Instant.now());

            // Mark order as completed
            if (shipment.getSaleOrder() != null) {
                SaleOrder saleOrder = shipment.getSaleOrder();
                saleOrder.setStatus(OrderStatus.COMPLETED);
                saleOrderRepository.save(saleOrder);
                // Complete inventory transaction (move from reserved to sold)
                // This would be handled by the inventory service
            }
        }

        shipment = shipmentRepository.save(shipment);
        return shipmentMapper.toDto(shipment);
    }

    /**
     * Get shipment tracking information.
     *
     * @param id the shipment id.
     * @return tracking information.
     */
    //    @Transactional(readOnly = true)
    //    public ShipmentTrackingDTO getShipmentTracking(Long id) {
    //        LOG.debug("Request to get Shipment tracking : {}", id);
    //
    //        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();
    //
    //        Shipment shipment = shipmentRepository.findById(id)
    //            .orElseThrow(() -> new BadRequestAlertException("Shipment not found", "Shipment", "shipmentNotFound"));
    //
    //        // Validate ownership
    //        if (!shipment.getClientAccount().getId().equals(currentClientAccountId)) {
    //            throw new AccessDeniedException(Constants.NOT_ALLOWED);
    //        }
    //
    //        ShipmentTrackingDTO tracking = new ShipmentTrackingDTO();
    //        tracking.setTrackingNumber(shipment.getTrackingNumber());
    //        tracking.setStatus(shipment.getStatus());
    //        tracking.setCarrier(shipment.getCarrier());
    //
    //        // If Yalidine shipment, get tracking from API
    //        if (shipment.getYalidineShipmentId() != null) {
    //            tracking.setTrackingUrl(shipment.getYalidineTrackingUrl());
    //
    //            // Get real-time tracking from Yalidine
    //            try {
    //                YalidineTrackingResponse yalidineTracking = yalidineIntegrationService.getTrackingInfo(
    //                    shipment.getYalidineShipmentId());
    //                // Map Yalidine tracking to our DTO
    //                // Implementation would depend on Yalidine API response format
    //            } catch (Exception e) {
    //                LOG.warn("Failed to get Yalidine tracking for shipment {}: {}", id, e.getMessage());
    //            }
    //        }
    //
    //        return tracking;
    //    }
    //
    //    /**
    //     * Sync shipment with Yalidine API.
    //     *
    //     * @param id the shipment id.
    //     * @return the updated shipment.
    //     */
    //    public ShipmentDTO syncWithYalidine(Long id) {
    //        LOG.debug("Request to sync Shipment with Yalidine : {}", id);
    //
    //        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();
    //
    //        Shipment shipment = shipmentRepository.findById(id)
    //            .orElseThrow(() -> new BadRequestAlertException("Shipment not found", "Shipment", "shipmentNotFound"));
    //
    //        // Validate ownership
    //        if (!shipment.getClientAccount().getId().equals(currentClientAccountId)) {
    //            throw new AccessDeniedException(Constants.NOT_ALLOWED);
    //        }
    //
    //        // Validate it's a Yalidine shipment
    //        if (shipment.getYalidineShipmentId() == null) {
    //            throw new BadRequestAlertException("Not a Yalidine shipment", "Shipment", "notYalidineShipment");
    //        }
    //
    //        try {
    //            // Get latest status from Yalidine
    //            YalidineTrackingResponse tracking = yalidineIntegrationService.getTrackingInfo(
    //                shipment.getYalidineShipmentId());
    //
    //            // Update shipment status based on Yalidine response
    //            // Implementation would depend on Yalidine API response format
    //            // Example:
    //            // if ("DELIVERED".equals(tracking.getStatus())) {
    //            //     shipment.setStatus(ShippingStatus.DELIVERED);
    //            //     shipment.setActualDeliveryDate(Instant.now());
    //            // }
    //
    //            shipment = shipmentRepository.save(shipment);
    //            return shipmentMapper.toDto(shipment);
    //
    //        } catch (Exception e) {
    //            LOG.error("Failed to sync shipment {} with Yalidine: {}", id, e.getMessage());
    //            throw new YalidineApiException("Failed to sync with Yalidine: " + e.getMessage());
    //        }
    //    }

    /**
     * Cancel a shipment.
     *
     * @param id the shipment id.
     * @return the updated shipment.
     */
    public ShipmentDTO cancelShipment(Long id) {
        LOG.debug("Request to cancel Shipment : {}", id);

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();

        Shipment shipment = shipmentRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Shipment not found", "Shipment", "shipmentNotFound"));

        // Validate ownership
        if (!shipment.getClientAccount().getId().equals(currentClientAccountId)) {
            throw new AccessDeniedException(Constants.NOT_ALLOWED);
        }

        // Cannot cancel delivered shipments
        if (shipment.getStatus() == ShippingStatus.DELIVERED) {
            throw new BadRequestAlertException("Cannot cancel delivered shipments", "Shipment", "cannotCancelDelivered");
        }

        // Cancel with Yalidine if applicable
        //        if (shipment.getYalidineShipmentId() != null) {
        //            try {
        //                // Cancel with Yalidine API
        //                // yalidineIntegrationService.cancelShipment(shipment.getYalidineShipmentId());
        //            } catch (Exception e) {
        //                LOG.warn("Failed to cancel shipment with Yalidine: {}", e.getMessage());
        //            }
        //        }

        shipment.setStatus(ShippingStatus.FAILED);
        shipment.setNotes(shipment.getNotes() + "\nShipment cancelled");

        shipment = shipmentRepository.save(shipment);
        return shipmentMapper.toDto(shipment);
    }

    // Private helper methods

    private void validateOrderCanBeShipped(SaleOrder saleOrder) {
        // Validate order type
        if (saleOrder.getOrderType() != OrderType.DELIVERY) {
            throw new BadRequestAlertException("Order is not a delivery order", "Shipment", "notDeliveryOrder");
        }

        // Validate order status
        if (saleOrder.getStatus() != OrderStatus.CONFIRMED) {
            throw new BadRequestAlertException("Order must be confirmed before shipping", "Shipment", "orderNotConfirmed");
        }
    }

    private String generateReference(Long clientAccountId) {
        String reference = shipmentRepository.getLastReference(clientAccountId).orElse(null);
        return GlobalUtils.generateReference(reference);
    }

    private void createYalidineShipment(Shipment shipment, SaleOrder saleOrder) {
        try {
            // Prepare Yalidine request
            CreateYalidineShipmentRequest yalidineRequest = new CreateYalidineShipmentRequest();
            yalidineRequest.setReference(saleOrder.getReference());
            yalidineRequest.setCustomerPhone(saleOrder.getCustomer().getPhone());
            yalidineRequest.setCustomerName(saleOrder.getCustomer().getFirstName() + " " + saleOrder.getCustomer().getLastName());
            yalidineRequest.setCodAmount(saleOrder.getTotal());

            // Set addresses
            if (shipment.getAddress() != null) {
                yalidineRequest.setDeliveryAddress(shipment.getAddress());
            }
            // Set pickup address from company
            yalidineRequest.setPickupAddress(saleOrder.getClientAccount().getAddress());

            // Create shipment with Yalidine
            YalidineShipmentResponse yalidineResponse = yalidineIntegrationService.createShipment(yalidineRequest);

            // Update shipment with Yalidine data
            shipment.setYalidineShipmentId(yalidineResponse.getShipmentId());
            shipment.setTrackingNumber(yalidineResponse.getTrackingNumber());
            shipment.setYalidineTrackingUrl(yalidineResponse.getTrackingUrl());
            shipment.setStatus(ShippingStatus.PROCESSING);
            shipment.setShippingDate(Instant.now());
            // Store Yalidine response data
            // shipment.setYalidineResponseData(objectMapper.valueToTree(yalidineResponse));

        } catch (Exception e) {
            LOG.error("Failed to create Yalidine shipment for order {}: {}", saleOrder.getReference(), e.getMessage());
            throw new YalidineApiException("Failed to create shipment with Yalidine: " + e.getMessage());
        }
    }
}
