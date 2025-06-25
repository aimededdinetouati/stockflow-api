package com.adeem.stockflow.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the {@link com.adeem.stockflow.domain.GuestCart} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GuestCartDTO implements Serializable {

    private String sessionId;
    private Instant createdDate;
    private Instant expiresAt;
    private List<GuestCartItemDTO> items;
    private BigDecimal totalAmount;
    private Integer totalItems;
    private Boolean isExpired;

    public GuestCartDTO() {
        // Empty constructor needed for Jackson.
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public List<GuestCartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<GuestCartItemDTO> items) {
        this.items = items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public Boolean getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(Boolean isExpired) {
        this.isExpired = isExpired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GuestCartDTO)) {
            return false;
        }

        GuestCartDTO guestCartDTO = (GuestCartDTO) o;
        if (this.sessionId == null) {
            return false;
        }
        return Objects.equals(this.sessionId, guestCartDTO.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.sessionId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GuestCartDTO{" +
            "sessionId='" + getSessionId() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", totalAmount=" + getTotalAmount() +
            ", totalItems=" + getTotalItems() +
            ", isExpired=" + getIsExpired() +
            "}";
    }
}
