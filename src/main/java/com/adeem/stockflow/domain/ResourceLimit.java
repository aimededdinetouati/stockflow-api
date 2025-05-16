package com.adeem.stockflow.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A ResourceLimit.
 */
@Entity
@Table(name = "resource_limit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ResourceLimit extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "resource_type", nullable = false)
    private String resourceType;

    @NotNull
    @Column(name = "max_amount", nullable = false)
    private Integer maxAmount;

    @NotNull
    @Column(name = "is_unlimited", nullable = false)
    private Boolean isUnlimited;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition
    @org.springframework.data.annotation.Transient
    @Transient
    private boolean isPersisted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "planFeatures", "resourceLimits" }, allowSetters = true)
    private PlanFormula planFormula;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ResourceLimit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResourceType() {
        return this.resourceType;
    }

    public ResourceLimit resourceType(String resourceType) {
        this.setResourceType(resourceType);
        return this;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Integer getMaxAmount() {
        return this.maxAmount;
    }

    public ResourceLimit maxAmount(Integer maxAmount) {
        this.setMaxAmount(maxAmount);
        return this;
    }

    public void setMaxAmount(Integer maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Boolean getIsUnlimited() {
        return this.isUnlimited;
    }

    public ResourceLimit isUnlimited(Boolean isUnlimited) {
        this.setIsUnlimited(isUnlimited);
        return this;
    }

    public void setIsUnlimited(Boolean isUnlimited) {
        this.isUnlimited = isUnlimited;
    }

    // Inherited createdBy methods
    public ResourceLimit createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public ResourceLimit createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public ResourceLimit lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public ResourceLimit lastModifiedDate(Instant lastModifiedDate) {
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

    public ResourceLimit setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public PlanFormula getPlanFormula() {
        return this.planFormula;
    }

    public void setPlanFormula(PlanFormula planFormula) {
        this.planFormula = planFormula;
    }

    public ResourceLimit planFormula(PlanFormula planFormula) {
        this.setPlanFormula(planFormula);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourceLimit)) {
            return false;
        }
        return getId() != null && getId().equals(((ResourceLimit) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResourceLimit{" +
            "id=" + getId() +
            ", resourceType='" + getResourceType() + "'" +
            ", maxAmount=" + getMaxAmount() +
            ", isUnlimited='" + getIsUnlimited() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
