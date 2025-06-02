package com.adeem.stockflow.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for Supplier statistics.
 */
public class SupplierStatsDTO implements Serializable {

    private Long totalSuppliers;
    private Long activeSuppliers;
    private Long inactiveSuppliers;
    private Long suppliersAddedThisMonth;
    private Long suppliersAddedThisWeek;
    private Long suppliersWithAddresses;
    private Long suppliersWithoutAddresses;
    private Long suppliersWithPurchaseOrders;
    private BigDecimal totalPurchaseOrderValue;
    private Instant lastSupplierCreated;
    private Instant lastSupplierModified;

    private List<SupplierActivityDTO> recentActivities;
    private List<TopSupplierDTO> topSuppliersByPurchaseOrders;
    private List<TopSupplierDTO> topSuppliersByValue;

    public SupplierStatsDTO() {}

    public Long getTotalSuppliers() {
        return totalSuppliers;
    }

    public void setTotalSuppliers(Long totalSuppliers) {
        this.totalSuppliers = totalSuppliers;
    }

    public Long getActiveSuppliers() {
        return activeSuppliers;
    }

    public void setActiveSuppliers(Long activeSuppliers) {
        this.activeSuppliers = activeSuppliers;
    }

    public Long getInactiveSuppliers() {
        return inactiveSuppliers;
    }

    public void setInactiveSuppliers(Long inactiveSuppliers) {
        this.inactiveSuppliers = inactiveSuppliers;
    }

    public Long getSuppliersAddedThisMonth() {
        return suppliersAddedThisMonth;
    }

    public void setSuppliersAddedThisMonth(Long suppliersAddedThisMonth) {
        this.suppliersAddedThisMonth = suppliersAddedThisMonth;
    }

    public Long getSuppliersAddedThisWeek() {
        return suppliersAddedThisWeek;
    }

    public void setSuppliersAddedThisWeek(Long suppliersAddedThisWeek) {
        this.suppliersAddedThisWeek = suppliersAddedThisWeek;
    }

    public Long getSuppliersWithAddresses() {
        return suppliersWithAddresses;
    }

    public void setSuppliersWithAddresses(Long suppliersWithAddresses) {
        this.suppliersWithAddresses = suppliersWithAddresses;
    }

    public Long getSuppliersWithoutAddresses() {
        return suppliersWithoutAddresses;
    }

    public void setSuppliersWithoutAddresses(Long suppliersWithoutAddresses) {
        this.suppliersWithoutAddresses = suppliersWithoutAddresses;
    }

    public Long getSuppliersWithPurchaseOrders() {
        return suppliersWithPurchaseOrders;
    }

    public void setSuppliersWithPurchaseOrders(Long suppliersWithPurchaseOrders) {
        this.suppliersWithPurchaseOrders = suppliersWithPurchaseOrders;
    }

    public BigDecimal getTotalPurchaseOrderValue() {
        return totalPurchaseOrderValue;
    }

    public void setTotalPurchaseOrderValue(BigDecimal totalPurchaseOrderValue) {
        this.totalPurchaseOrderValue = totalPurchaseOrderValue;
    }

    public Instant getLastSupplierCreated() {
        return lastSupplierCreated;
    }

    public void setLastSupplierCreated(Instant lastSupplierCreated) {
        this.lastSupplierCreated = lastSupplierCreated;
    }

    public Instant getLastSupplierModified() {
        return lastSupplierModified;
    }

    public void setLastSupplierModified(Instant lastSupplierModified) {
        this.lastSupplierModified = lastSupplierModified;
    }

    public List<SupplierActivityDTO> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<SupplierActivityDTO> recentActivities) {
        this.recentActivities = recentActivities;
    }

    public List<TopSupplierDTO> getTopSuppliersByPurchaseOrders() {
        return topSuppliersByPurchaseOrders;
    }

    public void setTopSuppliersByPurchaseOrders(List<TopSupplierDTO> topSuppliersByPurchaseOrders) {
        this.topSuppliersByPurchaseOrders = topSuppliersByPurchaseOrders;
    }

    public List<TopSupplierDTO> getTopSuppliersByValue() {
        return topSuppliersByValue;
    }

    public void setTopSuppliersByValue(List<TopSupplierDTO> topSuppliersByValue) {
        this.topSuppliersByValue = topSuppliersByValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SupplierStatsDTO)) return false;
        SupplierStatsDTO that = (SupplierStatsDTO) o;
        return Objects.equals(totalSuppliers, that.totalSuppliers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalSuppliers);
    }

    @Override
    public String toString() {
        return (
            "SupplierStatsDTO{" +
            "totalSuppliers=" +
            totalSuppliers +
            ", activeSuppliers=" +
            activeSuppliers +
            ", inactiveSuppliers=" +
            inactiveSuppliers +
            ", suppliersAddedThisMonth=" +
            suppliersAddedThisMonth +
            ", suppliersAddedThisWeek=" +
            suppliersAddedThisWeek +
            ", suppliersWithAddresses=" +
            suppliersWithAddresses +
            ", suppliersWithoutAddresses=" +
            suppliersWithoutAddresses +
            ", suppliersWithPurchaseOrders=" +
            suppliersWithPurchaseOrders +
            ", totalPurchaseOrderValue=" +
            totalPurchaseOrderValue +
            ", lastSupplierCreated=" +
            lastSupplierCreated +
            ", lastSupplierModified=" +
            lastSupplierModified +
            "}"
        );
    }

    /**
     * DTO for supplier activity entries.
     */
    public static class SupplierActivityDTO implements Serializable {

        private String action;
        private String supplierName;
        private Instant date;
        private String notes;

        public SupplierActivityDTO() {}

        public SupplierActivityDTO(String action, String supplierName, Instant date, String notes) {
            this.action = action;
            this.supplierName = supplierName;
            this.date = date;
            this.notes = notes;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getSupplierName() {
            return supplierName;
        }

        public void setSupplierName(String supplierName) {
            this.supplierName = supplierName;
        }

        public Instant getDate() {
            return date;
        }

        public void setDate(Instant date) {
            this.date = date;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        @Override
        public String toString() {
            return (
                "SupplierActivityDTO{" +
                "action='" +
                action +
                "'" +
                ", supplierName='" +
                supplierName +
                "'" +
                ", date=" +
                date +
                ", notes='" +
                notes +
                "'" +
                "}"
            );
        }
    }

    /**
     * DTO for top supplier entries.
     */
    public static class TopSupplierDTO implements Serializable {

        private Long id;
        private String name;
        private Long orderCount;
        private BigDecimal totalValue;

        public TopSupplierDTO() {}

        public TopSupplierDTO(Long id, String name, Long orderCount, BigDecimal totalValue) {
            this.id = id;
            this.name = name;
            this.orderCount = orderCount;
            this.totalValue = totalValue;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(Long orderCount) {
            this.orderCount = orderCount;
        }

        public BigDecimal getTotalValue() {
            return totalValue;
        }

        public void setTotalValue(BigDecimal totalValue) {
            this.totalValue = totalValue;
        }

        @Override
        public String toString() {
            return (
                "TopSupplierDTO{" + "id=" + id + ", name='" + name + "'" + ", orderCount=" + orderCount + ", totalValue=" + totalValue + "}"
            );
        }
    }
}
