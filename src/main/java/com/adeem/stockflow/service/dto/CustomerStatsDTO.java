package com.adeem.stockflow.service.dto;

import com.adeem.stockflow.domain.enumeration.AssociationType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A DTO for customer statistics.
 * Provides comprehensive statistics about customers and their associations.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerStatsDTO implements Serializable {

    // Core customer counts
    private Long totalCustomers = 0L;
    private Long enabledCustomers = 0L;
    private Long disabledCustomers = 0L;

    // Customer type breakdown
    private Long managedCustomers = 0L; // Created by company, no user account
    private Long independentCustomers = 0L; // Have user accounts

    // Account status breakdown
    private Long customersWithAccounts = 0L;
    private Long customersWithoutAccounts = 0L;

    // Association statistics
    private Long totalAssociations = 0L;
    private Map<AssociationType, Long> associationsByType = new HashMap<>();

    // Computed percentages
    private Double enabledPercentage = 0.0;
    private Double managedPercentage = 0.0;
    private Double independentPercentage = 0.0;
    private Double withAccountsPercentage = 0.0;

    // Constructors
    public CustomerStatsDTO() {}

    public CustomerStatsDTO(
        Long totalCustomers,
        Long managedCustomers,
        Long independentCustomers,
        Long enabledCustomers,
        Long disabledCustomers,
        Long customersWithAccounts,
        Long customersWithoutAccounts,
        Long totalAssociations
    ) {
        this.totalCustomers = totalCustomers != null ? totalCustomers : 0L;
        this.managedCustomers = managedCustomers != null ? managedCustomers : 0L;
        this.independentCustomers = independentCustomers != null ? independentCustomers : 0L;
        this.enabledCustomers = enabledCustomers != null ? enabledCustomers : 0L;
        this.disabledCustomers = disabledCustomers != null ? disabledCustomers : 0L;
        this.customersWithAccounts = customersWithAccounts != null ? customersWithAccounts : 0L;
        this.customersWithoutAccounts = customersWithoutAccounts != null ? customersWithoutAccounts : 0L;
        this.totalAssociations = totalAssociations != null ? totalAssociations : 0L;

        calculatePercentages();
    }

    // Getters and Setters
    public Long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(Long totalCustomers) {
        this.totalCustomers = totalCustomers != null ? totalCustomers : 0L;
        calculatePercentages();
    }

    public Long getEnabledCustomers() {
        return enabledCustomers;
    }

    public void setEnabledCustomers(Long enabledCustomers) {
        this.enabledCustomers = enabledCustomers != null ? enabledCustomers : 0L;
        calculatePercentages();
    }

    public Long getDisabledCustomers() {
        return disabledCustomers;
    }

    public void setDisabledCustomers(Long disabledCustomers) {
        this.disabledCustomers = disabledCustomers != null ? disabledCustomers : 0L;
        calculatePercentages();
    }

    public Long getManagedCustomers() {
        return managedCustomers;
    }

    public void setManagedCustomers(Long managedCustomers) {
        this.managedCustomers = managedCustomers != null ? managedCustomers : 0L;
        calculatePercentages();
    }

    public Long getIndependentCustomers() {
        return independentCustomers;
    }

    public void setIndependentCustomers(Long independentCustomers) {
        this.independentCustomers = independentCustomers != null ? independentCustomers : 0L;
        calculatePercentages();
    }

    public Long getCustomersWithAccounts() {
        return customersWithAccounts;
    }

    public void setCustomersWithAccounts(Long customersWithAccounts) {
        this.customersWithAccounts = customersWithAccounts != null ? customersWithAccounts : 0L;
        calculatePercentages();
    }

    public Long getCustomersWithoutAccounts() {
        return customersWithoutAccounts;
    }

    public void setCustomersWithoutAccounts(Long customersWithoutAccounts) {
        this.customersWithoutAccounts = customersWithoutAccounts != null ? customersWithoutAccounts : 0L;
        calculatePercentages();
    }

    public Long getTotalAssociations() {
        return totalAssociations;
    }

    public void setTotalAssociations(Long totalAssociations) {
        this.totalAssociations = totalAssociations != null ? totalAssociations : 0L;
    }

    public Map<AssociationType, Long> getAssociationsByType() {
        return associationsByType;
    }

    public void setAssociationsByType(Map<AssociationType, Long> associationsByType) {
        this.associationsByType = associationsByType != null ? associationsByType : new HashMap<>();
    }

    public Double getEnabledPercentage() {
        return enabledPercentage;
    }

    public Double getManagedPercentage() {
        return managedPercentage;
    }

    public Double getIndependentPercentage() {
        return independentPercentage;
    }

    public Double getWithAccountsPercentage() {
        return withAccountsPercentage;
    }

    // Helper methods
    private void calculatePercentages() {
        if (totalCustomers > 0) {
            this.enabledPercentage = (enabledCustomers * 100.0) / totalCustomers;
            this.managedPercentage = (managedCustomers * 100.0) / totalCustomers;
            this.independentPercentage = (independentCustomers * 100.0) / totalCustomers;
            this.withAccountsPercentage = (customersWithAccounts * 100.0) / totalCustomers;
        } else {
            this.enabledPercentage = 0.0;
            this.managedPercentage = 0.0;
            this.independentPercentage = 0.0;
            this.withAccountsPercentage = 0.0;
        }
    }

    public void addAssociationType(AssociationType type, Long count) {
        this.associationsByType.put(type, count != null ? count : 0L);
    }

    public Long getAssociationCount(AssociationType type) {
        return this.associationsByType.getOrDefault(type, 0L);
    }

    public Long getFollowedCount() {
        return getAssociationCount(AssociationType.FOLLOWED);
    }

    public Long getPreferredSupplierCount() {
        return getAssociationCount(AssociationType.PREFERRED_SUPPLIER);
    }

    public Long getBusinessPartnerCount() {
        return getAssociationCount(AssociationType.BUSINESS_PARTNER);
    }

    // Validation method
    public boolean isValid() {
        return (
            totalCustomers.equals(enabledCustomers + disabledCustomers) &&
            totalCustomers.equals(managedCustomers + independentCustomers) &&
            totalCustomers.equals(customersWithAccounts + customersWithoutAccounts)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerStatsDTO)) {
            return false;
        }

        CustomerStatsDTO that = (CustomerStatsDTO) o;
        return (
            Objects.equals(totalCustomers, that.totalCustomers) &&
            Objects.equals(managedCustomers, that.managedCustomers) &&
            Objects.equals(independentCustomers, that.independentCustomers)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalCustomers, managedCustomers, independentCustomers);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerStatsDTO{" +
            "totalCustomers=" + totalCustomers +
            ", enabledCustomers=" + enabledCustomers +
            ", disabledCustomers=" + disabledCustomers +
            ", managedCustomers=" + managedCustomers +
            ", independentCustomers=" + independentCustomers +
            ", customersWithAccounts=" + customersWithAccounts +
            ", customersWithoutAccounts=" + customersWithoutAccounts +
            ", totalAssociations=" + totalAssociations +
            ", associationsByType=" + associationsByType +
            ", enabledPercentage=" + enabledPercentage +
            ", managedPercentage=" + managedPercentage +
            ", independentPercentage=" + independentPercentage +
            ", withAccountsPercentage=" + withAccountsPercentage +
            "}";
    }
}
