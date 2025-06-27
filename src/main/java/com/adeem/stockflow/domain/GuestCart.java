package com.adeem.stockflow.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Guest Cart entity for marketplace anonymous shopping.
 * Allows non-authenticated users to build shopping carts using session-based storage.
 */
@Entity
@Table(name = "guest_cart")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GuestCart implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getSessionId() {
        return this.sessionId;
    }

    public GuestCart sessionId(String sessionId) {
        this.setSessionId(sessionId);
        return this;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public GuestCart createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }

    public GuestCart expiresAt(Instant expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GuestCart)) {
            return false;
        }
        return getSessionId() != null && getSessionId().equals(((GuestCart) o).getSessionId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GuestCart{" +
            "sessionId='" + getSessionId() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            "}";
    }
}
