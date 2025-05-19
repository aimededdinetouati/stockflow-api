package com.adeem.stockflow.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adeem.stockflow.domain.ResourceLimit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ResourceLimitDTO implements Serializable {

    private Long id;

    @NotNull
    private String resourceType;

    private Integer maxAmount;

    @NotNull
    private Boolean isUnlimited;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private PlanFormulaDTO planFormula;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Integer getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Integer maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Boolean getIsUnlimited() {
        return isUnlimited;
    }

    public void setIsUnlimited(Boolean isUnlimited) {
        this.isUnlimited = isUnlimited;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public PlanFormulaDTO getPlanFormula() {
        return planFormula;
    }

    public void setPlanFormula(PlanFormulaDTO planFormula) {
        this.planFormula = planFormula;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourceLimitDTO)) {
            return false;
        }

        ResourceLimitDTO resourceLimitDTO = (ResourceLimitDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, resourceLimitDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResourceLimitDTO{" +
            "id=" + getId() +
            ", resourceType='" + getResourceType() + "'" +
            ", maxAmount=" + getMaxAmount() +
            ", isUnlimited='" + getIsUnlimited() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", planFormula=" + getPlanFormula() +
            "}";
    }
}
