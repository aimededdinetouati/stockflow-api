package com.adeem.stockflow.service;

import com.adeem.stockflow.config.Constants;
import com.adeem.stockflow.domain.*;
import com.adeem.stockflow.domain.enumeration.*;
import com.adeem.stockflow.repository.*;
import com.adeem.stockflow.security.SecurityUtils;
import com.adeem.stockflow.service.criteria.ShipmentSpecification;
import com.adeem.stockflow.service.dto.*;
import com.adeem.stockflow.service.exceptions.*;
import com.adeem.stockflow.service.mapper.ShipmentMapper;
import com.adeem.stockflow.service.util.GlobalUtils;
import java.time.LocalDateTime;
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
    private final AddressRepository addressRepository;
    private final SaleOrderService saleOrderService;

    public ShipmentService(
        ShipmentRepository shipmentRepository,
        ShipmentMapper shipmentMapper,
        AddressRepository addressRepository,
        SaleOrderService saleOrderService
    ) {
        this.shipmentRepository = shipmentRepository;
        this.shipmentMapper = shipmentMapper;
        this.addressRepository = addressRepository;
        this.saleOrderService = saleOrderService;
    }

    /**
     * Create shipment for a specific order.
     */
    public ShipmentDTO createShipmentForOrder(Long orderId, ShipmentRequestDTO createShipmentDTO) {
        LOG.debug("Request to create Shipment for Order : {} with carrier: {}", orderId, createShipmentDTO.getCarrier());

        SaleOrder saleOrder = getAndValidateOrder(orderId);
        validateOrderCanBeShipped(saleOrder);
        validateShipmentDoesNotExist(saleOrder);

        Shipment shipment = buildNewShipment(createShipmentDTO, saleOrder);
        setShipmentAddress(shipment, saleOrder.getCustomer(), createShipmentDTO.getAddressId());

        shipment.setShippingDate(createShipmentDTO.getShippingDate() != null ? createShipmentDTO.getShippingDate() : LocalDateTime.now());

        shipment = shipmentRepository.save(shipment);
        updateOrderStatus(saleOrder, OrderStatus.SHIPPED);

        return shipmentMapper.toDto(shipment);
    }

    /**
     * Update a shipment.
     */
    public ShipmentDTO update(ShipmentRequestDTO shipmentDTO) {
        LOG.debug("Request to save Shipment : {}", shipmentDTO);

        Shipment shipment = getAndValidateShipment(shipmentDTO.getId());
        SaleOrder saleOrder = shipment.getSaleOrder();

        validateStatusTransition(shipment.getStatus(), shipmentDTO.getStatus());
        updateShipmentFields(shipment, shipmentDTO);
        setShipmentAddress(shipment, saleOrder.getCustomer(), shipmentDTO.getAddressId());

        handleDeliveryStatusChange(shipment, shipmentDTO.getStatus(), shipmentDTO.getActualDeliveryDate());

        shipment.setStatus(shipmentDTO.getStatus());
        shipment.setIsPersisted();
        shipment = shipmentRepository.save(shipment);

        return shipmentMapper.toDto(shipment);
    }

    /**
     * Cancel a shipment.
     */
    public ShipmentDTO cancelShipment(Long id) {
        LOG.debug("Request to cancel Shipment : {}", id);

        Shipment shipment = getAndValidateShipment(id);
        validateCanCancel(shipment);

        shipment.setStatus(ShippingStatus.FAILED);
        shipment = shipmentRepository.save(shipment);

        return shipmentMapper.toDto(shipment);
    }

    /**
     * Update shipment status.
     */
    public ShipmentDTO updateShipmentStatus(Long id, UpdateShipmentStatusDTO request) {
        LOG.debug("Request to update Shipment status : {} to {}", id, request.getStatus());

        Shipment shipment = getAndValidateShipment(id);
        validateStatusTransition(shipment.getStatus(), request.getStatus());

        handleDeliveryStatusChange(shipment, request.getStatus(), request.getActualDeliveryDate());

        shipment.setStatus(request.getStatus());
        shipment.setIsPersisted();
        shipment = shipmentRepository.save(shipment);

        return shipmentMapper.toDto(shipment);
    }

    /**
     * Get all shipments for current client account.
     */
    @Transactional(readOnly = true)
    public Page<ShipmentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Shipments");
        return findAllWithCriteria(pageable, null);
    }

    /**
     * Get all shipments with criteria filtering.
     */
    @Transactional(readOnly = true)
    public Page<ShipmentDTO> findAllWithCriteria(Pageable pageable, Specification<Shipment> spec) {
        LOG.debug("Request to get Shipments with criteria");

        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();
        Specification<Shipment> finalSpec = buildSpecificationWithClientFilter(currentClientAccountId, spec);

        return shipmentRepository.findAll(finalSpec, pageable).map(shipmentMapper::toDto);
    }

    /**
     * Get one shipment by id.
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
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Shipment : {}", id);

        Shipment shipment = getAndValidateShipment(id);
        validateCanDelete(shipment);

        shipmentRepository.deleteById(id);
    }

    // Private helper methods - Validation and Retrieval

    private SaleOrder getAndValidateOrder(Long orderId) {
        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();
        SaleOrder saleOrder = saleOrderService.findById(orderId).orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));

        validateOwnership(saleOrder.getClientAccount().getId(), currentClientAccountId);
        return saleOrder;
    }

    private Shipment getAndValidateShipment(Long shipmentId) {
        Long currentClientAccountId = SecurityUtils.getCurrentClientAccountId();
        Shipment shipment = shipmentRepository.findById(shipmentId).orElseThrow(() -> new AccessDeniedException(Constants.NOT_ALLOWED));

        validateOwnership(shipment.getClientAccount().getId(), currentClientAccountId);
        return shipment;
    }

    private void validateOwnership(Long entityClientAccountId, Long currentClientAccountId) {
        if (!entityClientAccountId.equals(currentClientAccountId)) {
            throw new AccessDeniedException(Constants.NOT_ALLOWED);
        }
    }

    private void validateOrderCanBeShipped(SaleOrder saleOrder) {
        if (saleOrder.getOrderType() != OrderType.DELIVERY) {
            throw new BadRequestAlertException("Order is not a delivery order", "Shipment", "notDeliveryOrder");
        }
        if (saleOrder.getStatus() != OrderStatus.CONFIRMED) {
            throw new BadRequestAlertException("Order must be confirmed before shipping", "Shipment", "orderNotConfirmed");
        }
    }

    private void validateShipmentDoesNotExist(SaleOrder saleOrder) {
        if (saleOrder.getShipment() != null) {
            throw new BadRequestAlertException("Shipment already exists for this order", "Shipment", "shipmentExists");
        }
    }

    private void validateCanCancel(Shipment shipment) {
        if (shipment.getStatus() == ShippingStatus.DELIVERED) {
            throw new BadRequestAlertException("Cannot cancel delivered shipments", "Shipment", "cannotCancelDelivered");
        }
    }

    private void validateCanDelete(Shipment shipment) {
        if (shipment.getStatus() != ShippingStatus.PENDING) {
            throw new BadRequestAlertException("Cannot delete processed shipments", "Shipment", "cannotDeleteProcessed");
        }
    }

    private void validateStatusTransition(ShippingStatus currentStatus, ShippingStatus newStatus) {
        if (currentStatus == ShippingStatus.DELIVERED) {
            throw new BadRequestAlertException("Cannot change status from DELIVERED", "Shipment", "invalidStatusTransition");
        }
        if (currentStatus == ShippingStatus.FAILED && newStatus != ShippingStatus.PENDING) {
            throw new BadRequestAlertException("Failed shipments can only be reset to PENDING", "Shipment", "invalidStatusTransition");
        }
    }

    // Private helper methods - Business Logic

    private Shipment buildNewShipment(ShipmentRequestDTO dto, SaleOrder saleOrder) {
        Shipment shipment = new Shipment();
        shipment.setReference(generateReference(saleOrder.getClientAccount().getId()));
        shipment.setCarrier(dto.getCarrier());
        shipment.setNotes(dto.getNotes());
        shipment.setWeight(dto.getWeight());
        shipment.setStatus(ShippingStatus.PENDING);
        shipment.setSaleOrder(saleOrder);
        shipment.setClientAccount(saleOrder.getClientAccount());
        return shipment;
    }

    private void updateShipmentFields(Shipment shipment, ShipmentRequestDTO dto) {
        shipment.setCarrier(dto.getCarrier());
        shipment.setNotes(dto.getNotes());
        shipment.setWeight(dto.getWeight());
    }

    private void setShipmentAddress(Shipment shipment, Customer customer, Long addressId) {
        if (customer == null) {
            throw new BadRequestAlertException("Customer not found for order", "Shipment", ErrorConstants.REQUIRED_CUSTOMER);
        }

        Address address = addressRepository
            .findByIdAndCustomerId(addressId, customer.getId())
            .orElseThrow(() -> new BadRequestAlertException("Address not found", "Customer", ErrorConstants.REQUIRED_ADDRESS));

        shipment.setAddress(address);
    }

    private void handleDeliveryStatusChange(Shipment shipment, ShippingStatus newStatus, LocalDateTime providedActualDeliveryDate) {
        boolean isBecomingDelivered = shipment.getStatus() != ShippingStatus.DELIVERED && newStatus == ShippingStatus.DELIVERED;

        if (isBecomingDelivered) {
            shipment.setActualDeliveryDate(providedActualDeliveryDate != null ? providedActualDeliveryDate : LocalDateTime.now());
            completeOrderIfNeeded(shipment.getSaleOrder());
        }
    }

    private void completeOrderIfNeeded(SaleOrder saleOrder) {
        if (!OrderStatus.COMPLETED.equals(saleOrder.getStatus())) {
            saleOrder.setStatus(OrderStatus.COMPLETED);
            saleOrderService.updateInventoryQuantities(saleOrder, TransactionType.SALE);
            saleOrderService.save(saleOrder);
        }
    }

    private void updateOrderStatus(SaleOrder saleOrder, OrderStatus status) {
        saleOrder.setStatus(status);
        saleOrderService.save(saleOrder);
    }

    private Specification<Shipment> buildSpecificationWithClientFilter(Long clientAccountId, Specification<Shipment> additionalSpec) {
        Specification<Shipment> clientSpec = ShipmentSpecification.withClientAccountId(clientAccountId);
        return additionalSpec != null ? clientSpec.and(additionalSpec) : clientSpec;
    }

    private String generateReference(Long clientAccountId) {
        String reference = shipmentRepository.getLastReference(clientAccountId).orElse(null);
        return GlobalUtils.generateReference(reference);
    }
    // Commented out methods for Yalidine integration - keeping for reference
    /*
    private void createYalidineShipment(Shipment shipment, SaleOrder saleOrder) {
        try {
            CreateYalidineShipmentRequest yalidineRequest = buildYalidineRequest(saleOrder, shipment);
            YalidineShipmentResponse yalidineResponse = yalidineIntegrationService.createShipment(yalidineRequest);
            updateShipmentWithYalidineData(shipment, yalidineResponse);
        } catch (Exception e) {
            LOG.error("Failed to create Yalidine shipment for order {}: {}", saleOrder.getReference(), e.getMessage());
            throw new YalidineApiException("Failed to create shipment with Yalidine: " + e.getMessage());
        }
    }

    private CreateYalidineShipmentRequest buildYalidineRequest(SaleOrder saleOrder, Shipment shipment) {
        CreateYalidineShipmentRequest request = new CreateYalidineShipmentRequest();
        request.setReference(saleOrder.getReference());
        request.setCustomerPhone(saleOrder.getCustomer().getPhone());
        request.setCustomerName(saleOrder.getCustomer().getFirstName() + " " + saleOrder.getCustomer().getLastName());
        request.setCodAmount(saleOrder.getTotal());

        if (shipment.getAddress() != null) {
            request.setDeliveryAddress(shipment.getAddress());
        }
        request.setPickupAddress(saleOrder.getClientAccount().getAddress());

        return request;
    }

    private void updateShipmentWithYalidineData(Shipment shipment, YalidineShipmentResponse response) {
        shipment.setYalidineShipmentId(response.getShipmentId());
        shipment.setTrackingNumber(response.getTrackingNumber());
        shipment.setYalidineTrackingUrl(response.getTrackingUrl());
        shipment.setStatus(ShippingStatus.PROCESSING);
        shipment.setShippingDate(LocalDateTime.now());
    }
    */
}
