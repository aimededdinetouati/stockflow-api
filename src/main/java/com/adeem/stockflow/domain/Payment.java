package com.adeem.stockflow.domain;

import com.adeem.stockflow.domain.enumeration.PaymentGateway;
import com.adeem.stockflow.domain.enumeration.PaymentMethod;
import com.adeem.stockflow.domain.enumeration.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A Payment.
 */
@Entity
@Table(name = "payment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Payment extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(name = "date", nullable = false)
    private ZonedDateTime date;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private PaymentMethod method;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "gateway", nullable = false)
    private PaymentGateway gateway;

    @Column(name = "chargily_checkout_url")
    private String chargilyCheckoutUrl;

    @Column(name = "chargily_transaction_id")
    private String chargilyTransactionId;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "transfer_reference_number")
    private String transferReferenceNumber;

    @NotNull
    @Column(name = "reconciled", nullable = false)
    private Boolean reconciled;

    @Column(name = "reconciled_date")
    private ZonedDateTime reconciledDate;

    @Column(name = "reconciled_by")
    private String reconciledBy;

    @Column(name = "notes")
    private String notes;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "payment")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "clientAccount", "user", "payment", "product" }, allowSetters = true)
    private Set<Attachment> attachments = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "address", "quota", "subscriptions" }, allowSetters = true)
    private ClientAccount clientAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "addressLists", "carts", "clientAccount" }, allowSetters = true)
    private Customer customer;

    @JsonIgnoreProperties(value = { "payment", "orderItems", "clientAccount", "customer", "shipment" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "payment")
    private SaleOrder saleOrder;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Payment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public Payment reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Payment amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ZonedDateTime getDate() {
        return this.date;
    }

    public Payment date(ZonedDateTime date) {
        this.setDate(date);
        return this;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public PaymentMethod getMethod() {
        return this.method;
    }

    public Payment method(PaymentMethod method) {
        this.setMethod(method);
        return this;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return this.status;
    }

    public Payment status(PaymentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public PaymentGateway getGateway() {
        return this.gateway;
    }

    public Payment gateway(PaymentGateway gateway) {
        this.setGateway(gateway);
        return this;
    }

    public void setGateway(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public String getChargilyCheckoutUrl() {
        return this.chargilyCheckoutUrl;
    }

    public Payment chargilyCheckoutUrl(String chargilyCheckoutUrl) {
        this.setChargilyCheckoutUrl(chargilyCheckoutUrl);
        return this;
    }

    public void setChargilyCheckoutUrl(String chargilyCheckoutUrl) {
        this.chargilyCheckoutUrl = chargilyCheckoutUrl;
    }

    public String getChargilyTransactionId() {
        return this.chargilyTransactionId;
    }

    public Payment chargilyTransactionId(String chargilyTransactionId) {
        this.setChargilyTransactionId(chargilyTransactionId);
        return this;
    }

    public void setChargilyTransactionId(String chargilyTransactionId) {
        this.chargilyTransactionId = chargilyTransactionId;
    }

    public String getBankName() {
        return this.bankName;
    }

    public Payment bankName(String bankName) {
        this.setBankName(bankName);
        return this;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public Payment accountNumber(String accountNumber) {
        this.setAccountNumber(accountNumber);
        return this;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getTransferReferenceNumber() {
        return this.transferReferenceNumber;
    }

    public Payment transferReferenceNumber(String transferReferenceNumber) {
        this.setTransferReferenceNumber(transferReferenceNumber);
        return this;
    }

    public void setTransferReferenceNumber(String transferReferenceNumber) {
        this.transferReferenceNumber = transferReferenceNumber;
    }

    public Boolean getReconciled() {
        return this.reconciled;
    }

    public Payment reconciled(Boolean reconciled) {
        this.setReconciled(reconciled);
        return this;
    }

    public void setReconciled(Boolean reconciled) {
        this.reconciled = reconciled;
    }

    public ZonedDateTime getReconciledDate() {
        return this.reconciledDate;
    }

    public Payment reconciledDate(ZonedDateTime reconciledDate) {
        this.setReconciledDate(reconciledDate);
        return this;
    }

    public void setReconciledDate(ZonedDateTime reconciledDate) {
        this.reconciledDate = reconciledDate;
    }

    public String getReconciledBy() {
        return this.reconciledBy;
    }

    public Payment reconciledBy(String reconciledBy) {
        this.setReconciledBy(reconciledBy);
        return this;
    }

    public void setReconciledBy(String reconciledBy) {
        this.reconciledBy = reconciledBy;
    }

    public String getNotes() {
        return this.notes;
    }

    public Payment notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Inherited createdBy methods
    public Payment createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public Payment createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public Payment lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public Payment lastModifiedDate(Instant lastModifiedDate) {
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

    public Payment setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Set<Attachment> getAttachments() {
        return this.attachments;
    }

    public void setAttachments(Set<Attachment> attachments) {
        if (this.attachments != null) {
            this.attachments.forEach(i -> i.setPayment(null));
        }
        if (attachments != null) {
            attachments.forEach(i -> i.setPayment(this));
        }
        this.attachments = attachments;
    }

    public Payment attachments(Set<Attachment> attachments) {
        this.setAttachments(attachments);
        return this;
    }

    public Payment addAttachments(Attachment attachment) {
        this.attachments.add(attachment);
        attachment.setPayment(this);
        return this;
    }

    public Payment removeAttachments(Attachment attachment) {
        this.attachments.remove(attachment);
        attachment.setPayment(null);
        return this;
    }

    public ClientAccount getClientAccount() {
        return this.clientAccount;
    }

    public void setClientAccount(ClientAccount clientAccount) {
        this.clientAccount = clientAccount;
    }

    public Payment clientAccount(ClientAccount clientAccount) {
        this.setClientAccount(clientAccount);
        return this;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Payment customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public SaleOrder getSaleOrder() {
        return this.saleOrder;
    }

    public void setSaleOrder(SaleOrder saleOrder) {
        if (this.saleOrder != null) {
            this.saleOrder.setPayment(null);
        }
        if (saleOrder != null) {
            saleOrder.setPayment(this);
        }
        this.saleOrder = saleOrder;
    }

    public Payment saleOrder(SaleOrder saleOrder) {
        this.setSaleOrder(saleOrder);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment)) {
            return false;
        }
        return getId() != null && getId().equals(((Payment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Payment{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", amount=" + getAmount() +
            ", date='" + getDate() + "'" +
            ", method='" + getMethod() + "'" +
            ", status='" + getStatus() + "'" +
            ", gateway='" + getGateway() + "'" +
            ", chargilyCheckoutUrl='" + getChargilyCheckoutUrl() + "'" +
            ", chargilyTransactionId='" + getChargilyTransactionId() + "'" +
            ", bankName='" + getBankName() + "'" +
            ", accountNumber='" + getAccountNumber() + "'" +
            ", transferReferenceNumber='" + getTransferReferenceNumber() + "'" +
            ", reconciled='" + getReconciled() + "'" +
            ", reconciledDate='" + getReconciledDate() + "'" +
            ", reconciledBy='" + getReconciledBy() + "'" +
            ", notes='" + getNotes() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
