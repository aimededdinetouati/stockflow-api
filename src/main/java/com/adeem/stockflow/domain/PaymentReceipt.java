package com.adeem.stockflow.domain;

import com.adeem.stockflow.domain.enumeration.ReceiptStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A PaymentReceipt.
 */
@Entity
@Table(name = "payment_receipt")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentReceipt extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "receipt_number", nullable = false, unique = true)
    private String receiptNumber;

    @NotNull
    @Column(name = "submission_date", nullable = false)
    private ZonedDateTime submissionDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReceiptStatus status;

    @Column(name = "review_date")
    private ZonedDateTime reviewDate;

    @Column(name = "review_notes")
    private String reviewNotes;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "attachments", "clientAccount", "customer", "saleOrder" }, allowSetters = true)
    private Payment payment;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PaymentReceipt id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiptNumber() {
        return this.receiptNumber;
    }

    public PaymentReceipt receiptNumber(String receiptNumber) {
        this.setReceiptNumber(receiptNumber);
        return this;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public ZonedDateTime getSubmissionDate() {
        return this.submissionDate;
    }

    public PaymentReceipt submissionDate(ZonedDateTime submissionDate) {
        this.setSubmissionDate(submissionDate);
        return this;
    }

    public void setSubmissionDate(ZonedDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public ReceiptStatus getStatus() {
        return this.status;
    }

    public PaymentReceipt status(ReceiptStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ReceiptStatus status) {
        this.status = status;
    }

    public ZonedDateTime getReviewDate() {
        return this.reviewDate;
    }

    public PaymentReceipt reviewDate(ZonedDateTime reviewDate) {
        this.setReviewDate(reviewDate);
        return this;
    }

    public void setReviewDate(ZonedDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getReviewNotes() {
        return this.reviewNotes;
    }

    public PaymentReceipt reviewNotes(String reviewNotes) {
        this.setReviewNotes(reviewNotes);
        return this;
    }

    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }

    // Inherited createdBy methods
    public PaymentReceipt createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public PaymentReceipt createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public PaymentReceipt lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public PaymentReceipt lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
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

    public PaymentReceipt setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Payment getPayment() {
        return this.payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public PaymentReceipt payment(Payment payment) {
        this.setPayment(payment);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentReceipt)) {
            return false;
        }
        return getId() != null && getId().equals(((PaymentReceipt) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentReceipt{" +
            "id=" + getId() +
            ", receiptNumber='" + getReceiptNumber() + "'" +
            ", submissionDate='" + getSubmissionDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", reviewDate='" + getReviewDate() + "'" +
            ", reviewNotes='" + getReviewNotes() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
