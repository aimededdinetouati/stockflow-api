package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.CustomerClientAssociation;
import com.adeem.stockflow.domain.enumeration.AssociationStatus;
import com.adeem.stockflow.domain.enumeration.AssociationType;
import com.adeem.stockflow.repository.projection.AssociationStatsProjection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CustomerClientAssociation entity.
 * Manages customer-company association relationships.
 */
@Repository
public interface CustomerClientAssociationRepository
    extends JpaRepository<CustomerClientAssociation, Long>, JpaSpecificationExecutor<CustomerClientAssociation> {
    // Customer-side association queries
    /**
     * Find all associations for a specific customer
     */
    Page<CustomerClientAssociation> findAllByCustomerId(Long customerId, Pageable pageable);

    /**
     * Find all active associations for a specific customer
     */
    Page<CustomerClientAssociation> findAllByCustomerIdAndStatus(Long customerId, AssociationStatus status, Pageable pageable);

    /**
     * Find association between customer and client account
     */
    Optional<CustomerClientAssociation> findByCustomerIdAndClientAccountId(Long customerId, Long clientAccountId);

    /**
     * Check if association exists between customer and client account with specific type
     */
    boolean existsByCustomerIdAndClientAccountIdAndAssociationType(Long customerId, Long clientAccountId, AssociationType associationType);

    /**
     * Check if any active association exists between customer and client account
     */
    boolean existsByCustomerIdAndClientAccountIdAndStatus(Long customerId, Long clientAccountId, AssociationStatus status);

    /**
     * Find all associations for customer by type
     */
    List<CustomerClientAssociation> findAllByCustomerIdAndAssociationType(Long customerId, AssociationType associationType);

    /**
     * Find active associations for customer by type
     */
    List<CustomerClientAssociation> findAllByCustomerIdAndAssociationTypeAndStatus(
        Long customerId,
        AssociationType associationType,
        AssociationStatus status
    );

    // Company-side association queries
    /**
     * Find all associations for a specific client account
     */
    Page<CustomerClientAssociation> findAllByClientAccountId(Long clientAccountId, Pageable pageable);

    /**
     * Find all active associations for a specific client account
     */
    Page<CustomerClientAssociation> findAllByClientAccountIdAndStatus(Long clientAccountId, AssociationStatus status, Pageable pageable);

    /**
     * Find associations for client account by type
     */
    Page<CustomerClientAssociation> findAllByClientAccountIdAndAssociationType(
        Long clientAccountId,
        AssociationType associationType,
        Pageable pageable
    );

    /**
     * Find active associations for client account by type
     */
    Page<CustomerClientAssociation> findAllByClientAccountIdAndAssociationTypeAndStatus(
        Long clientAccountId,
        AssociationType associationType,
        AssociationStatus status,
        Pageable pageable
    );

    // Statistics queries using projections
    /**
     * Count associations by type for a client account
     */
    @Query(
        "SELECT a.associationType as associationType, COUNT(a) as count " +
        "FROM CustomerClientAssociation a " +
        "WHERE a.clientAccount.id = :clientAccountId AND a.status = :status " +
        "GROUP BY a.associationType"
    )
    List<AssociationStatsProjection> countAssociationsByTypeAndClientAccount(
        @Param("clientAccountId") Long clientAccountId,
        @Param("status") AssociationStatus status
    );

    /**
     * Count total active associations for client account
     */
    @Query("SELECT COUNT(a) FROM CustomerClientAssociation a " + "WHERE a.clientAccount.id = :clientAccountId AND a.status = 'ACTIVE'")
    Long countActiveAssociationsByClientAccount(@Param("clientAccountId") Long clientAccountId);

    /**
     * Count total associations for client account (all statuses)
     */
    @Query("SELECT COUNT(a) FROM CustomerClientAssociation a " + "WHERE a.clientAccount.id = :clientAccountId")
    Long countAllAssociationsByClientAccount(@Param("clientAccountId") Long clientAccountId);

    /**
     * Count associations by status for client account
     */
    @Query(
        "SELECT a.status as status, COUNT(a) as count " +
        "FROM CustomerClientAssociation a " +
        "WHERE a.clientAccount.id = :clientAccountId " +
        "GROUP BY a.status"
    )
    List<Object[]> countAssociationsByStatusAndClientAccount(@Param("clientAccountId") Long clientAccountId);

    /**
     * Count total active associations for customer
     */
    @Query("SELECT COUNT(a) FROM CustomerClientAssociation a " + "WHERE a.customer.id = :customerId AND a.status = 'ACTIVE'")
    Long countActiveAssociationsByCustomer(@Param("customerId") Long customerId);

    /**
     * Count associations by type for customer
     */
    @Query(
        "SELECT a.associationType as associationType, COUNT(a) as count " +
        "FROM CustomerClientAssociation a " +
        "WHERE a.customer.id = :customerId AND a.status = :status " +
        "GROUP BY a.associationType"
    )
    List<AssociationStatsProjection> countAssociationsByTypeAndCustomer(
        @Param("customerId") Long customerId,
        @Param("status") AssociationStatus status
    );

    // Search and filtering queries
    /**
     * Search associations by customer name for client account
     */
    @Query(
        "SELECT a FROM CustomerClientAssociation a " +
        "WHERE a.clientAccount.id = :clientAccountId AND " +
        "(LOWER(a.customer.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
        "LOWER(a.customer.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
        "LOWER(CONCAT(a.customer.firstName, ' ', a.customer.lastName)) LIKE LOWER(CONCAT('%', :query, '%')))"
    )
    Page<CustomerClientAssociation> searchByCustomerNameAndClientAccount(
        @Param("query") String query,
        @Param("clientAccountId") Long clientAccountId,
        Pageable pageable
    );

    /**
     * Search associations by company name for customer
     */
    @Query(
        "SELECT a FROM CustomerClientAssociation a " +
        "WHERE a.customer.id = :customerId AND " +
        "LOWER(a.clientAccount.companyName) LIKE LOWER(CONCAT('%', :query, '%'))"
    )
    Page<CustomerClientAssociation> searchByCompanyNameAndCustomer(
        @Param("query") String query,
        @Param("customerId") Long customerId,
        Pageable pageable
    );

    // Bulk operations
    /**
     * Find all associations for bulk status update
     */
    @Query("SELECT a FROM CustomerClientAssociation a WHERE a.id IN :ids")
    List<CustomerClientAssociation> findAllByIdIn(@Param("ids") List<Long> ids);

    /**
     * Delete all associations for a customer
     */
    void deleteAllByCustomerId(Long customerId);

    /**
     * Delete all associations for a client account
     */
    void deleteAllByClientAccountId(Long clientAccountId);

    // Recent activity queries
    /**
     * Find recent associations for client account
     */
    @Query("SELECT a FROM CustomerClientAssociation a " + "WHERE a.clientAccount.id = :clientAccountId " + "ORDER BY a.createdDate DESC")
    Page<CustomerClientAssociation> findRecentAssociationsByClientAccount(
        @Param("clientAccountId") Long clientAccountId,
        Pageable pageable
    );

    /**
     * Find recent associations for customer
     */
    @Query("SELECT a FROM CustomerClientAssociation a " + "WHERE a.customer.id = :customerId " + "ORDER BY a.createdDate DESC")
    Page<CustomerClientAssociation> findRecentAssociationsByCustomer(@Param("customerId") Long customerId, Pageable pageable);

    // Validation queries
    /**
     * Check if customer can create association (prevent duplicate types)
     */
    @Query(
        "SELECT COUNT(a) FROM CustomerClientAssociation a " +
        "WHERE a.customer.id = :customerId AND a.clientAccount.id = :clientAccountId AND " +
        "a.associationType = :associationType AND a.status IN ('ACTIVE', 'PENDING')"
    )
    Long countExistingAssociations(
        @Param("customerId") Long customerId,
        @Param("clientAccountId") Long clientAccountId,
        @Param("associationType") AssociationType associationType
    );

    /**
     * Find companies that customer can associate with (not already associated)
     */
    @Query(
        "SELECT ca FROM ClientAccount ca " +
        "WHERE ca.id NOT IN (" +
        "    SELECT a.clientAccount.id FROM CustomerClientAssociation a " +
        "    WHERE a.customer.id = :customerId AND a.status = 'ACTIVE'" +
        ") AND ca.status = 'ACTIVE'"
    )
    Page<Object> findAvailableCompaniesForCustomer(@Param("customerId") Long customerId, Pageable pageable);
}
