package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.ProductImportJob;
import com.adeem.stockflow.domain.enumeration.ImportStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductImportJob entity.
 */
@Repository
public interface ProductImportJobRepository extends JpaRepository<ProductImportJob, Long>, JpaSpecificationExecutor<ProductImportJob> {
    /**
     * Find import job by ID and client account ID.
     */
    Optional<ProductImportJob> findByIdAndClientAccountId(Long id, Long clientAccountId);

    /**
     * Find import job by job execution ID.
     */
    Optional<ProductImportJob> findByJobExecutionId(Long jobExecutionId);

    /**
     * Find import jobs by client account ID.
     */
    Page<ProductImportJob> findByClientAccountIdOrderByStartTimeDesc(Long clientAccountId, Pageable pageable);

    /**
     * Find import jobs by client account ID and status.
     */
    List<ProductImportJob> findByClientAccountIdAndStatus(Long clientAccountId, ImportStatus status);

    /**
     * Find running import jobs for a client account.
     */
    @Query("SELECT j FROM ProductImportJob j WHERE j.clientAccount.id = :clientAccountId AND j.status IN ('STARTED', 'PROCESSING')")
    List<ProductImportJob> findRunningJobsByClientAccount(@Param("clientAccountId") Long clientAccountId);

    /**
     * Find jobs that should be cleaned up (older than specified time).
     */
    @Query("SELECT j FROM ProductImportJob j WHERE j.endTime < :cutoffTime OR (j.startTime < :cutoffTime AND j.status = 'FAILED')")
    List<ProductImportJob> findJobsForCleanup(@Param("cutoffTime") Instant cutoffTime);

    /**
     * Update job status.
     */
    @Modifying
    @Query("UPDATE ProductImportJob j SET j.status = :status, j.currentPhase = :phase WHERE j.id = :jobId")
    void updateJobStatus(@Param("jobId") Long jobId, @Param("status") ImportStatus status, @Param("phase") String phase);

    /**
     * Update job progress.
     */
    @Modifying
    @Query(
        "UPDATE ProductImportJob j SET j.totalRows = :totalRows, j.successfulRows = :successfulRows, " +
        "j.failedRows = :failedRows, j.currentPhase = :phase WHERE j.id = :jobId"
    )
    void updateJobProgress(
        @Param("jobId") Long jobId,
        @Param("totalRows") Integer totalRows,
        @Param("successfulRows") Integer successfulRows,
        @Param("failedRows") Integer failedRows,
        @Param("phase") String phase
    );

    /**
     * Complete job.
     */
    @Modifying
    @Query("UPDATE ProductImportJob j SET j.status = :status, j.endTime = :endTime, j.currentPhase = :phase " + "WHERE j.id = :jobId")
    void completeJob(
        @Param("jobId") Long jobId,
        @Param("status") ImportStatus status,
        @Param("endTime") Instant endTime,
        @Param("phase") String phase
    );

    /**
     * Count import jobs by client account and status.
     */
    long countByClientAccountIdAndStatus(Long clientAccountId, ImportStatus status);

    /**
     * Count total import jobs by client account.
     */
    long countByClientAccountId(Long clientAccountId);

    /**
     * Find recent successful imports for statistics.
     */
    @Query(
        "SELECT j FROM ProductImportJob j WHERE j.clientAccount.id = :clientAccountId AND j.status = 'COMPLETED' " +
        "AND j.endTime >= :since ORDER BY j.endTime DESC"
    )
    List<ProductImportJob> findRecentSuccessfulImports(@Param("clientAccountId") Long clientAccountId, @Param("since") Instant since);
}
