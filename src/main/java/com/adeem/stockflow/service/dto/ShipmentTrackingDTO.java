package com.adeem.stockflow.service.dto;

import com.adeem.stockflow.domain.enumeration.ShippingStatus;
import java.io.Serializable;
import java.util.List;

/**
 * DTO for shipment tracking information.
 */
public class ShipmentTrackingDTO implements Serializable {

    private String trackingNumber;
    private String trackingUrl;
    private ShippingStatus status;
    private String carrier;
    private String estimatedDelivery;
    private List<TrackingEventDTO> events;

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
    }

    public ShippingStatus getStatus() {
        return status;
    }

    public void setStatus(ShippingStatus status) {
        this.status = status;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(String estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public List<TrackingEventDTO> getEvents() {
        return events;
    }

    public void setEvents(List<TrackingEventDTO> events) {
        this.events = events;
    }

    public static class TrackingEventDTO {

        private String timestamp;
        private String status;
        private String description;
        private String location;

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
}
