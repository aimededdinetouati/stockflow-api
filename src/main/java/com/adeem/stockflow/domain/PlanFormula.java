package com.adeem.stockflow.domain;

import com.adeem.stockflow.domain.enumeration.BillingCycle;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A PlanFormula.
 */
@Entity
@Table(name = "plan_formula")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlanFormula extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

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
    @Column(name = "base_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal basePrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false)
    private BillingCycle billingCycle;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "planFormula")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "planFormula" }, allowSetters = true)
    private Set<PlanFeature> planFeatures = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "planFormula")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "planFormula" }, allowSetters = true)
    private Set<ResourceLimit> resourceLimits = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PlanFormula id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public PlanFormula name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public PlanFormula description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBasePrice() {
        return this.basePrice;
    }

    public PlanFormula basePrice(BigDecimal basePrice) {
        this.setBasePrice(basePrice);
        return this;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BillingCycle getBillingCycle() {
        return this.billingCycle;
    }

    public PlanFormula billingCycle(BillingCycle billingCycle) {
        this.setBillingCycle(billingCycle);
        return this;
    }

    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public PlanFormula isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    // Inherited createdBy methods
    public PlanFormula createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public PlanFormula createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public PlanFormula lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public PlanFormula lastModifiedDate(Instant lastModifiedDate) {
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

    public PlanFormula setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Set<PlanFeature> getPlanFeatures() {
        return this.planFeatures;
    }

    public void setPlanFeatures(Set<PlanFeature> planFeatures) {
        if (this.planFeatures != null) {
            this.planFeatures.forEach(i -> i.setPlanFormula(null));
        }
        if (planFeatures != null) {
            planFeatures.forEach(i -> i.setPlanFormula(this));
        }
        this.planFeatures = planFeatures;
    }

    public PlanFormula planFeatures(Set<PlanFeature> planFeatures) {
        this.setPlanFeatures(planFeatures);
        return this;
    }

    public PlanFormula addPlanFeatures(PlanFeature planFeature) {
        this.planFeatures.add(planFeature);
        planFeature.setPlanFormula(this);
        return this;
    }

    public PlanFormula removePlanFeatures(PlanFeature planFeature) {
        this.planFeatures.remove(planFeature);
        planFeature.setPlanFormula(null);
        return this;
    }

    public Set<ResourceLimit> getResourceLimits() {
        return this.resourceLimits;
    }

    public void setResourceLimits(Set<ResourceLimit> resourceLimits) {
        if (this.resourceLimits != null) {
            this.resourceLimits.forEach(i -> i.setPlanFormula(null));
        }
        if (resourceLimits != null) {
            resourceLimits.forEach(i -> i.setPlanFormula(this));
        }
        this.resourceLimits = resourceLimits;
    }

    public PlanFormula resourceLimits(Set<ResourceLimit> resourceLimits) {
        this.setResourceLimits(resourceLimits);
        return this;
    }

    public PlanFormula addResourceLimits(ResourceLimit resourceLimit) {
        this.resourceLimits.add(resourceLimit);
        resourceLimit.setPlanFormula(this);
        return this;
    }

    public PlanFormula removeResourceLimits(ResourceLimit resourceLimit) {
        this.resourceLimits.remove(resourceLimit);
        resourceLimit.setPlanFormula(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlanFormula)) {
            return false;
        }
        return getId() != null && getId().equals(((PlanFormula) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlanFormula{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", basePrice=" + getBasePrice() +
            ", billingCycle='" + getBillingCycle() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
