package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.Address;
import com.adeem.stockflow.domain.ClientAccount;
import com.adeem.stockflow.service.dto.yalidine.*;
import com.adeem.stockflow.service.dto.yalidine.CreateYalidineShipmentRequest;
import com.adeem.stockflow.service.dto.yalidine.YalidineShipmentResponse;
import com.adeem.stockflow.service.exceptions.YalidineApiException;
import com.adeem.stockflow.service.exceptions.YalidineConfigurationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service for integrating with Yalidine delivery API.
 * Handles shipment creation, tracking, and status updates.
 */
@Service
public class YalidineIntegrationService {

    private static final Logger LOG = LoggerFactory.getLogger(YalidineIntegrationService.class);

    @Value("${app.yalidine.api.base-url:https://api.yalidine.app}")
    private String yalidineBaseUrl;

    @Value("${app.yalidine.api.timeout:30000}")
    private int apiTimeout;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public YalidineIntegrationService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Create a shipment in Yalidine system.
     *
     * @param request the shipment creation request.
     * @return the Yalidine shipment response.
     */
    public YalidineShipmentResponse createShipment(CreateYalidineShipmentRequest request) {
        LOG.debug("Creating Yalidine shipment for reference: {}", request.getReference());

        validateYalidineConfiguration(request.getClientAccount());

        try {
            String url = yalidineBaseUrl + "/api/v1/packages";

            HttpHeaders headers = createAuthHeaders(request.getClientAccount());
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Map our request to Yalidine API format
            Map<String, Object> yalidinePayload = mapToYalidineRequest(request);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(yalidinePayload, headers);

            ResponseEntity<YalidineApiResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                YalidineApiResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                YalidineApiResponse apiResponse = response.getBody();

                if (apiResponse.isSuccess()) {
                    return mapToShipmentResponse(apiResponse.getData());
                } else {
                    throw new YalidineApiException("Yalidine API error: " + apiResponse.getMessage());
                }
            } else {
                throw new YalidineApiException("Failed to create Yalidine shipment: HTTP " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            LOG.error("Error calling Yalidine API for shipment creation: {}", e.getMessage());
            throw new YalidineApiException("Failed to communicate with Yalidine API: " + e.getMessage());
        } catch (Exception e) {
            LOG.error("Unexpected error creating Yalidine shipment: {}", e.getMessage());
            throw new YalidineApiException("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Get tracking information from Yalidine.
     *
     * @param yalidineShipmentId the Yalidine shipment ID.
     * @return the tracking response.
     */
    public YalidineTrackingResponse getTrackingInfo(String yalidineShipmentId) {
        LOG.debug("Getting Yalidine tracking info for shipment: {}", yalidineShipmentId);

        try {
            String url = yalidineBaseUrl + "/api/v1/packages/" + yalidineShipmentId + "/tracking";

            // Note: For tracking, we might need different auth or public access
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<YalidineTrackingApiResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                YalidineTrackingApiResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                YalidineTrackingApiResponse apiResponse = response.getBody();

                if (apiResponse.isSuccess()) {
                    return mapToTrackingResponse(apiResponse.getData());
                } else {
                    throw new YalidineApiException("Yalidine tracking API error: " + apiResponse.getMessage());
                }
            } else {
                throw new YalidineApiException("Failed to get Yalidine tracking: HTTP " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            LOG.error("Error calling Yalidine tracking API: {}", e.getMessage());
            throw new YalidineApiException("Failed to get tracking from Yalidine API: " + e.getMessage());
        } catch (Exception e) {
            LOG.error("Unexpected error getting Yalidine tracking: {}", e.getMessage());
            throw new YalidineApiException("Unexpected tracking error: " + e.getMessage());
        }
    }

    /**
     * Handle webhook updates from Yalidine.
     *
     * @param payload the webhook payload.
     */
    public void handleStatusUpdate(YalidineWebhookPayload payload) {
        LOG.debug("Handling Yalidine webhook for shipment: {}", payload.getShipmentId());

        try {
            // Validate webhook signature if configured
            // validateWebhookSignature(payload);

            // Process the status update
            // This would typically update the shipment status in the database
            // and trigger any necessary business logic (like completing orders)

            LOG.info("Yalidine status update: shipment {} is now {}", payload.getShipmentId(), payload.getStatus());
            // Implementation would depend on your business requirements
            // Example: update shipment status, complete order if delivered, etc.

        } catch (Exception e) {
            LOG.error("Error processing Yalidine webhook: {}", e.getMessage());
            // Don't throw exception for webhooks - log and continue
        }
    }

    /**
     * Calculate shipping cost using Yalidine rates.
     *
     * @param fromAddress the pickup address.
     * @param toAddress the delivery address.
     * @param weight the package weight.
     * @return the calculated shipping cost.
     */
    public BigDecimal calculateShippingCost(Address fromAddress, Address toAddress, Double weight) {
        LOG.debug("Calculating Yalidine shipping cost from {} to {}", fromAddress.getCity(), toAddress.getCity());

        try {
            String url = yalidineBaseUrl + "/api/v1/rates";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("from_city", fromAddress.getCity());
            requestPayload.put("to_city", toAddress.getCity());
            requestPayload.put("weight", weight != null ? weight : 1.0);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, headers);

            ResponseEntity<YalidineRateResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                YalidineRateResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().getRate();
            } else {
                LOG.warn("Failed to get Yalidine rate, using default");
                return BigDecimal.valueOf(500); // Default rate in DZD
            }
        } catch (Exception e) {
            LOG.warn("Error calculating Yalidine rate: {}, using default", e.getMessage());
            return BigDecimal.valueOf(500); // Default rate in DZD
        }
    }

    /**
     * Cancel a shipment with Yalidine.
     *
     * @param yalidineShipmentId the Yalidine shipment ID.
     */
    public void cancelShipment(String yalidineShipmentId, ClientAccount clientAccount) {
        LOG.debug("Cancelling Yalidine shipment: {}", yalidineShipmentId);

        try {
            String url = yalidineBaseUrl + "/api/v1/packages/" + yalidineShipmentId + "/cancel";

            HttpHeaders headers = createAuthHeaders(clientAccount);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<YalidineApiResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                YalidineApiResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                LOG.warn("Failed to cancel Yalidine shipment: HTTP {}", response.getStatusCode());
            }
        } catch (Exception e) {
            LOG.warn("Error cancelling Yalidine shipment: {}", e.getMessage());
            // Don't throw exception - cancellation is best effort
        }
    }

    // Private helper methods

    private void validateYalidineConfiguration(ClientAccount clientAccount) {
        if (clientAccount.getYalidineEnabled() == null || !clientAccount.getYalidineEnabled()) {
            throw new YalidineConfigurationException("Yalidine integration not enabled for company");
        }

        if (clientAccount.getYalidineApiKey() == null || clientAccount.getYalidineApiKey().trim().isEmpty()) {
            throw new YalidineConfigurationException("Yalidine API key not configured");
        }

        if (clientAccount.getYalidineApiSecret() == null || clientAccount.getYalidineApiSecret().trim().isEmpty()) {
            throw new YalidineConfigurationException("Yalidine API secret not configured");
        }
    }

    private HttpHeaders createAuthHeaders(ClientAccount clientAccount) {
        HttpHeaders headers = new HttpHeaders();

        // Yalidine typically uses API key authentication
        headers.set("X-API-Key", clientAccount.getYalidineApiKey());

        // If they use OAuth or JWT, implement accordingly
        // headers.set("Authorization", "Bearer " + getAccessToken(clientAccount));

        return headers;
    }

    private Map<String, Object> mapToYalidineRequest(CreateYalidineShipmentRequest request) {
        Map<String, Object> payload = new HashMap<>();

        // Basic shipment info
        payload.put("reference", request.getReference());
        payload.put("cod_amount", request.getCodAmount());

        // Customer info
        Map<String, Object> recipient = new HashMap<>();
        recipient.put("name", request.getCustomerName());
        recipient.put("phone", request.getCustomerPhone());
        payload.put("recipient", recipient);

        // Delivery address
        if (request.getDeliveryAddress() != null) {
            Map<String, Object> deliveryAddr = new HashMap<>();
            deliveryAddr.put("city", request.getDeliveryAddress().getCity());
            deliveryAddr.put("address", request.getDeliveryAddress().getStreetAddress());
            deliveryAddr.put("postal_code", request.getDeliveryAddress().getPostalCode());
            payload.put("delivery_address", deliveryAddr);
        }

        // Pickup address
        if (request.getPickupAddress() != null) {
            Map<String, Object> pickupAddr = new HashMap<>();
            pickupAddr.put("city", request.getPickupAddress().getCity());
            pickupAddr.put("address", request.getPickupAddress().getStreetAddress());
            pickupAddr.put("postal_code", request.getPickupAddress().getPostalCode());
            payload.put("pickup_address", pickupAddr);
        }

        // Items
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            payload.put("items", request.getItems());
        }

        return payload;
    }

    private YalidineShipmentResponse mapToShipmentResponse(Object data) {
        // Map Yalidine API response to our response DTO
        // Implementation depends on actual Yalidine API response format
        YalidineShipmentResponse response = new YalidineShipmentResponse();

        if (data instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) data;

            response.setShipmentId((String) dataMap.get("id"));
            response.setTrackingNumber((String) dataMap.get("tracking_number"));
            response.setTrackingUrl((String) dataMap.get("tracking_url"));
            response.setStatus((String) dataMap.get("status"));
        }

        return response;
    }

    private YalidineTrackingResponse mapToTrackingResponse(Object data) {
        // Map Yalidine tracking API response to our response DTO
        YalidineTrackingResponse response = new YalidineTrackingResponse();

        if (data instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) data;

            response.setShipmentId((String) dataMap.get("id"));
            response.setStatus((String) dataMap.get("status"));
            response.setCurrentLocation((String) dataMap.get("current_location"));
            response.setEstimatedDelivery((String) dataMap.get("estimated_delivery"));
            // Map events, history, etc.
        }

        return response;
    }
}
