package com.adeem.stockflow.service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adeem.stockflow.domain.GuestCartItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GuestCartItemDTO implements Serializable {

    private Long id;

    @NotNull
    private String sessionId;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal quantity;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal priceAtTime;

    @NotNull
    private Instant addedDate;

    @NotNull
    private Long productId;

    private BigDecimal totalPrice;

    public GuestCartItemDTO() {
        // Empty constructor needed for Jackson.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceAtTime() {
        return priceAtTime;
    }

    public void setPriceAtTime(BigDecimal priceAtTime) {
        this.priceAtTime = priceAtTime;
    }

    public Instant getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Instant addedDate) {
        this.addedDate = addedDate;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GuestCartItemDTO)) {
            return false;
        }

        GuestCartItemDTO guestCartItemDTO = (GuestCartItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, guestCartItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GuestCartItemDTO{" +
            "id=" + getId() +
            ", sessionId='" + getSessionId() + "'" +
            ", quantity=" + getQuantity() +
            ", priceAtTime=" + getPriceAtTime() +
            ", addedDate='" + getAddedDate() + "'" +
            ", totalPrice=" + getTotalPrice() +
            "}";
    }
}
