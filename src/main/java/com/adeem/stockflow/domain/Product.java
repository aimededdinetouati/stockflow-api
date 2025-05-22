package com.adeem.stockflow.domain;

import com.adeem.stockflow.domain.enumeration.ProductCategory;
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
 * A Product.
 */
@Entity
@Table(name = "product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Product extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "manufacturer_code")
    private String manufacturerCode;

    @Column(name = "upc")
    private String upc;

    @Column(name = "selling_price", precision = 21, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "cost_price", precision = 21, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "profit_margin", precision = 21, scale = 2)
    private BigDecimal profitMargin;

    @Column(name = "minimum_stock_level", precision = 21, scale = 2)
    private BigDecimal minimumStockLevel;

    @NotNull
    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @NotNull
    @Column(name = "apply_tva", columnDefinition = "boolean default false")
    private Boolean applyTva;

    @Column(name = "is_visible_to_customers", columnDefinition = "boolean default true")
    private Boolean isVisibleToCustomers;

    @Column(name = "expiration_date")
    private ZonedDateTime expirationDate;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "clientAccount", "user", "payment", "product" }, allowSetters = true)
    private Set<Attachment> images = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    private Set<Inventory> inventories = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "address", "quota", "subscriptions" }, allowSetters = true)
    private ClientAccount clientAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductFamily productFamily;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Product id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Product name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Product description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return this.code;
    }

    public Product code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getManufacturerCode() {
        return this.manufacturerCode;
    }

    public Product manufacturerCode(String manufacturerCode) {
        this.setManufacturerCode(manufacturerCode);
        return this;
    }

    public void setManufacturerCode(String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    public String getUpc() {
        return this.upc;
    }

    public Product upc(String upc) {
        this.setUpc(upc);
        return this;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public BigDecimal getSellingPrice() {
        return this.sellingPrice;
    }

    public Product sellingPrice(BigDecimal sellingPrice) {
        this.setSellingPrice(sellingPrice);
        return this;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public BigDecimal getCostPrice() {
        return this.costPrice;
    }

    public Product costPrice(BigDecimal costPrice) {
        this.setCostPrice(costPrice);
        return this;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getProfitMargin() {
        return this.profitMargin;
    }

    public Product profitMargin(BigDecimal profitMargin) {
        this.setProfitMargin(profitMargin);
        return this;
    }

    public void setProfitMargin(BigDecimal profitMargin) {
        this.profitMargin = profitMargin;
    }

    public BigDecimal getMinimumStockLevel() {
        return this.minimumStockLevel;
    }

    public Product minimumStockLevel(BigDecimal minimumStockLevel) {
        this.setMinimumStockLevel(minimumStockLevel);
        return this;
    }

    public void setMinimumStockLevel(BigDecimal minimumStockLevel) {
        this.minimumStockLevel = minimumStockLevel;
    }

    public ProductCategory getCategory() {
        return this.category;
    }

    public Product category(ProductCategory category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public Boolean getApplyTva() {
        return this.applyTva;
    }

    public Product applyTva(Boolean applyTva) {
        this.setApplyTva(applyTva);
        return this;
    }

    public void setApplyTva(Boolean applyTva) {
        this.applyTva = applyTva;
    }

    public Boolean getIsVisibleToCustomers() {
        return this.isVisibleToCustomers;
    }

    public Product isVisibleToCustomers(Boolean isVisibleToCustomers) {
        this.setIsVisibleToCustomers(isVisibleToCustomers);
        return this;
    }

    public void setIsVisibleToCustomers(Boolean isVisibleToCustomers) {
        this.isVisibleToCustomers = isVisibleToCustomers;
    }

    public ZonedDateTime getExpirationDate() {
        return this.expirationDate;
    }

    public Product expirationDate(ZonedDateTime expirationDate) {
        this.setExpirationDate(expirationDate);
        return this;
    }

    public void setExpirationDate(ZonedDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    // Inherited createdBy methods
    public Product createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public Product createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public Product lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public Product lastModifiedDate(Instant lastModifiedDate) {
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

    public Product setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Set<Attachment> getImages() {
        return this.images;
    }

    public void setImages(Set<Attachment> attachments) {
        if (this.images != null) {
            this.images.forEach(i -> i.setProduct(null));
        }
        if (attachments != null) {
            attachments.forEach(i -> i.setProduct(this));
        }
        this.images = attachments;
    }

    public Product images(Set<Attachment> attachments) {
        this.setImages(attachments);
        return this;
    }

    public Product addImages(Attachment attachment) {
        this.images.add(attachment);
        attachment.setProduct(this);
        return this;
    }

    public Product removeImages(Attachment attachment) {
        this.images.remove(attachment);
        attachment.setProduct(null);
        return this;
    }

    public Set<Inventory> getInventories() {
        return this.inventories;
    }

    public void setInventories(Set<Inventory> inventories) {
        if (this.inventories != null) {
            this.inventories.forEach(i -> i.setProduct(null));
        }
        if (inventories != null) {
            inventories.forEach(i -> i.setProduct(this));
        }
        this.inventories = inventories;
    }

    public Product inventories(Set<Inventory> inventories) {
        this.setInventories(inventories);
        return this;
    }

    public Product addInventories(Inventory inventory) {
        this.inventories.add(inventory);
        inventory.setProduct(this);
        return this;
    }

    public Product removeInventories(Inventory inventory) {
        this.inventories.remove(inventory);
        inventory.setProduct(null);
        return this;
    }

    public ClientAccount getClientAccount() {
        return this.clientAccount;
    }

    public void setClientAccount(ClientAccount clientAccount) {
        this.clientAccount = clientAccount;
    }

    public Product clientAccount(ClientAccount clientAccount) {
        this.setClientAccount(clientAccount);
        return this;
    }

    public ProductFamily getProductFamily() {
        return this.productFamily;
    }

    public void setProductFamily(ProductFamily productFamily) {
        this.productFamily = productFamily;
    }

    public Product productFamily(ProductFamily productFamily) {
        this.setProductFamily(productFamily);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return getId() != null && getId().equals(((Product) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", code='" + getCode() + "'" +
            ", manufacturerCode='" + getManufacturerCode() + "'" +
            ", upc='" + getUpc() + "'" +
            ", sellingPrice=" + getSellingPrice() +
            ", costPrice=" + getCostPrice() +
            ", profitMargin=" + getProfitMargin() +
            ", minimumStockLevel=" + getMinimumStockLevel() +
            ", category='" + getCategory() + "'" +
            ", applyTva='" + getApplyTva() + "'" +
            ", isVisibleToCustomers='" + getIsVisibleToCustomers() + "'" +
            ", expirationDate='" + getExpirationDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
