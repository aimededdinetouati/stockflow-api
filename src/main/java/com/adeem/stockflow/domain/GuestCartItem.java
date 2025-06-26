package com.adeem.stockflow.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Guest Cart Item entity for marketplace anonymous shopping.
 * Represents individual products added to a guest cart session.
 */
@Entity
@Table(name = "guest_cart_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GuestCartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "quantity", precision = 21, scale = 2, nullable = false)
    private BigDecimal quantity;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "price_at_time", precision = 21, scale = 2, nullable = false)
    private BigDecimal priceAtTime;

    @NotNull
    @Column(name = "added_date", nullable = false)
    private Instant addedDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "category", "clientAccount", "inventories", "cartItems", "guestCartItems" }, allowSetters = true)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "items" }, allowSetters = true)
    private GuestCart guestCart;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public GuestCartItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public GuestCartItem sessionId(String sessionId) {
        this.setSessionId(sessionId);
        return this;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public BigDecimal getQuantity() {
        return this.quantity;
    }

    public GuestCartItem quantity(BigDecimal quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceAtTime() {
        return this.priceAtTime;
    }

    public GuestCartItem priceAtTime(BigDecimal priceAtTime) {
        this.setPriceAtTime(priceAtTime);
        return this;
    }

    public void setPriceAtTime(BigDecimal priceAtTime) {
        this.priceAtTime = priceAtTime;
    }

    public Instant getAddedDate() {
        return this.addedDate;
    }

    public GuestCartItem addedDate(Instant addedDate) {
        this.setAddedDate(addedDate);
        return this;
    }

    public void setAddedDate(Instant addedDate) {
        this.addedDate = addedDate;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public GuestCartItem product(Product product) {
        this.setProduct(product);
        return this;
    }

    public GuestCart getGuestCart() {
        return this.guestCart;
    }

    public void setGuestCart(GuestCart guestCart) {
        this.guestCart = guestCart;
    }

    public GuestCartItem guestCart(GuestCart guestCart) {
        this.setGuestCart(guestCart);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GuestCartItem)) {
            return false;
        }
        return getId() != null && getId().equals(((GuestCartItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GuestCartItem{" +
            "id=" + getId() +
            ", sessionId='" + getSessionId() + "'" +
            ", quantity=" + getQuantity() +
            ", priceAtTime=" + getPriceAtTime() +
            ", addedDate='" + getAddedDate() + "'" +
            "}";
    }
}
