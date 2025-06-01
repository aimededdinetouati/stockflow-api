package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.ProductImportError;
import com.adeem.stockflow.domain.enumeration.ImportErrorType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductImportError entity.
 */
@Repository
public interface ProductImportErrorRepository
    extends JpaRepository<ProductImportError, Long>, JpaSpecificationExecutor<ProductImportError> {
    /**
     * Find errors by import job ID.
     */
    List<ProductImportError> findByImportJobIdOrderByRowNumberAsc(Long importJobId);

    /**
     * Find errors by import job ID with pagination.
     */
    Page<ProductImportError> findByImportJobIdOrderByRowNumberAsc(Long importJobId, Pageable pageable);

    /**
     * Find errors by import job ID and error type.
     */
    List<ProductImportError> findByImportJobIdAndErrorType(Long importJobId, ImportErrorType errorType);

    /**
     * Count errors by import job ID.
     */
    long countByImportJobId(Long importJobId);

    /**
     * Count errors by import job ID and error type.
     */
    long countByImportJobIdAndErrorType(Long importJobId, ImportErrorType errorType);

    /**
     * Find errors for a specific field.
     */
    List<ProductImportError> findByImportJobIdAndFieldName(Long importJobId, String fieldName);

    /**
     * Delete errors by import job ID.
     */
    void deleteByImportJobId(Long importJobId);

    /**
     * Get error statistics for a job.
     */
    @Query("SELECT e.errorType, COUNT(e) FROM ProductImportError e WHERE e.importJob.id = :importJobId GROUP BY e.errorType")
    List<Object[]> getErrorStatsByImportJob(@Param("importJobId") Long importJobId);

    /**
     * Find the most common errors for a client account (for analytics).
     */
    @Query(
        "SELECT e.fieldName, e.errorType, COUNT(e) as errorCount FROM ProductImportError e " +
        "JOIN e.importJob j WHERE j.clientAccount.id = :clientAccountId " +
        "GROUP BY e.fieldName, e.errorType ORDER BY errorCount DESC"
    )
    List<Object[]> findCommonErrorsByClientAccount(@Param("clientAccountId") Long clientAccountId, Pageable pageable);

    /**
     * Find duplicate code errors for better reporting.
     */
    @Query(
        "SELECT e FROM ProductImportError e WHERE e.importJob.id = :importJobId " +
        "AND e.errorType = 'DUPLICATE' AND e.fieldName = 'code' ORDER BY e.rowNumber"
    )
    List<ProductImportError> findDuplicateCodeErrors(@Param("importJobId") Long importJobId);
}
