package com.adeem.stockflow.domain;

import com.adeem.stockflow.domain.enumeration.CartStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A Cart.
 */
@Entity
@Table(name = "cart")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Cart extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    // Inherited createdBy definition
    // Inherited lastModifiedBy definition
    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cart")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product", "cart" }, allowSetters = true)
    private Set<CartItem> cartItems = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", unique = true)
    @JsonIgnoreProperties(value = { "user", "addressLists", "cart", "clientAccount" }, allowSetters = true)
    private Customer customer;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Cart id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Cart createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public Cart lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    // Inherited createdBy methods
    public Cart createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited lastModifiedBy methods
    public Cart lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    @PostLoad
    @PostPersist
    public void updateEntityState() {
        this.setIsPersisted();
    }

    @org.springframework.data.annotation.Transient
    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public Cart setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Set<CartItem> getCartItems() {
        return this.cartItems;
    }

    public void setCartItems(Set<CartItem> cartItems) {
        if (this.cartItems != null) {
            this.cartItems.forEach(i -> i.setCart(null));
        }
        if (cartItems != null) {
            cartItems.forEach(i -> i.setCart(this));
        }
        this.cartItems = cartItems;
    }

    public Cart cartItems(Set<CartItem> cartItems) {
        this.setCartItems(cartItems);
        return this;
    }

    public Cart addCartItems(CartItem cartItem) {
        this.cartItems.add(cartItem);
        cartItem.setCart(this);
        return this;
    }

    public Cart removeCartItems(CartItem cartItem) {
        this.cartItems.remove(cartItem);
        cartItem.setCart(null);
        return this;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Cart customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cart)) {
            return false;
        }
        return getId() != null && getId().equals(((Cart) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cart{" +
            "id=" + getId() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            "}";
    }
}
