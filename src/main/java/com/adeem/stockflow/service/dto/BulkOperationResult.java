package com.adeem.stockflow.service.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for representing the result of bulk operations.
 */
public class BulkOperationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private int successCount;
    private int failedCount;
    private List<Long> failedIds;
    private List<Object> updatedEntities;

    public BulkOperationResult() {
        this.successCount = 0;
        this.failedCount = 0;
        this.failedIds = new ArrayList<>();
        this.updatedEntities = new ArrayList<>();
    }

    public BulkOperationResult(int successCount, int failedCount, List<Long> failedIds) {
        this.successCount = successCount;
        this.failedCount = failedCount;
        this.failedIds = failedIds != null ? failedIds : new ArrayList<>();
        this.updatedEntities = new ArrayList<>();
    }

    public void incrementSuccess() {
        this.successCount++;
    }

    public void incrementFailed(Long id) {
        this.failedCount++;
        this.failedIds.add(id);
    }

    public void addUpdatedEntity(Object entity) {
        this.updatedEntities.add(entity);
    }

    // Getters and Setters
    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public List<Long> getFailedIds() {
        return failedIds;
    }

    public void setFailedIds(List<Long> failedIds) {
        this.failedIds = failedIds;
    }

    public List<Object> getUpdatedEntities() {
        return updatedEntities;
    }

    public void setUpdatedEntities(List<Object> updatedEntities) {
        this.updatedEntities = updatedEntities;
    }

    @Override
    public String toString() {
        return (
            "BulkOperationResult{" +
            "successCount=" +
            successCount +
            ", failedCount=" +
            failedCount +
            ", failedIds=" +
            failedIds +
            ", updatedEntitiesCount=" +
            (updatedEntities != null ? updatedEntities.size() : 0) +
            '}'
        );
    }
}
