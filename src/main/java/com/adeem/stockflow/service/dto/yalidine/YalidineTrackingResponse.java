package com.adeem.stockflow.service.dto.yalidine;

import java.io.Serializable;
import java.util.List;

/**
 * Response DTO from Yalidine tracking API.
 */
public class YalidineTrackingResponse implements Serializable {

    private String shipmentId;
    private String status;
    private String currentLocation;
    private String estimatedDelivery;
    private List<YalidineTrackingResponse.TrackingEvent> events;

    public String getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(String estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public List<YalidineTrackingResponse.TrackingEvent> getEvents() {
        return events;
    }

    public void setEvents(List<YalidineTrackingResponse.TrackingEvent> events) {
        this.events = events;
    }

    /**
     * Represents a tracking event.
     */
    public static class TrackingEvent implements Serializable {

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
