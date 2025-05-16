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
 * A PlanFeature.
 */
@Entity
@Table(name = "plan_feature")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlanFeature extends AbstractAuditingEntity<Long> implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "feature_name", nullable = false)
    private String featureName;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "is_included", nullable = false)
    private Boolean isIncluded;

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

    public PlanFeature id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFeatureName() {
        return this.featureName;
    }

    public PlanFeature featureName(String featureName) {
        this.setFeatureName(featureName);
        return this;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getDescription() {
        return this.description;
    }

    public PlanFeature description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsIncluded() {
        return this.isIncluded;
    }

    public PlanFeature isIncluded(Boolean isIncluded) {
        this.setIsIncluded(isIncluded);
        return this;
    }

    public void setIsIncluded(Boolean isIncluded) {
        this.isIncluded = isIncluded;
    }

    // Inherited createdBy methods
    public PlanFeature createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public PlanFeature createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public PlanFeature lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public PlanFeature lastModifiedDate(Instant lastModifiedDate) {
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

    public PlanFeature setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public PlanFormula getPlanFormula() {
        return this.planFormula;
    }

    public void setPlanFormula(PlanFormula planFormula) {
        this.planFormula = planFormula;
    }

    public PlanFeature planFormula(PlanFormula planFormula) {
        this.setPlanFormula(planFormula);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlanFeature)) {
            return false;
        }
        return getId() != null && getId().equals(((PlanFeature) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlanFeature{" +
            "id=" + getId() +
            ", featureName='" + getFeatureName() + "'" +
            ", description='" + getDescription() + "'" +
            ", isIncluded='" + getIsIncluded() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
