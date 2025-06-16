package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.Customer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Customer entity.
 * Enhanced with multi-tenant security and comprehensive querying capabilities.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    // Multi-tenant queries
    /**
     * Find all customers created by a specific client account with pagination
     */
    Page<Customer> findAllByCreatedByClientAccountIdAndEnabledTrue(Long clientAccountId, Pageable pageable);

    /**
     * Find all customers created by a specific client account (including disabled)
     */
    Page<Customer> findAllByCreatedByClientAccountId(Long clientAccountId, Pageable pageable);

    /**
     * Find customer by ID and creator client account
     */
    Optional<Customer> findByIdAndCreatedByClientAccountId(Long id, Long clientAccountId);

    /**
     * Find customer by ID that can be viewed by client account (created by OR has association with)
     */
    @Query(
        "SELECT DISTINCT c FROM Customer c " +
        "LEFT JOIN c.associations a " +
        "WHERE c.id = :customerId AND " +
        "(c.createdByClientAccount.id = :clientAccountId OR " +
        "(a.clientAccount.id = :clientAccountId AND a.status = 'ACTIVE'))"
    )
    Optional<Customer> findByIdAndViewableByClientAccount(
        @Param("customerId") Long customerId,
        @Param("clientAccountId") Long clientAccountId
    );

    // Validation queries
    /**
     * Check if phone exists within client account scope
     */
    boolean existsByPhoneAndCreatedByClientAccountId(String phone, Long clientAccountId);

    /**
     * Check if tax ID exists within client account scope
     */
    boolean existsByTaxIdAndCreatedByClientAccountId(String taxId, Long clientAccountId);

    /**
     * Check if phone exists within client account scope excluding a specific customer
     */
    boolean existsByPhoneAndCreatedByClientAccountIdAndIdNot(String phone, Long clientAccountId, Long excludeId);

    /**
     * Check if tax ID exists within client account scope excluding a specific customer
     */
    boolean existsByTaxIdAndCreatedByClientAccountIdAndIdNot(String taxId, Long clientAccountId, Long excludeId);

    // User relationship queries
    /**
     * Find customer by associated user ID
     */
    Optional<Customer> findByUserId(Long userId);

    /**
     * Check if customer exists with user ID
     */
    boolean existsByUserId(Long userId);

    /**
     * Find customer by user email
     */
    @Query("SELECT c FROM Customer c WHERE c.user.email = :email")
    Optional<Customer> findByUserEmail(@Param("email") String email);

    // Search queries
    /**
     * Search customers within client account scope
     */
    @Query(
        "SELECT c FROM Customer c WHERE " +
        "c.createdByClientAccount.id = :clientAccountId AND " +
        "c.enabled = true AND " +
        "(LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
        "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
        "LOWER(c.phone) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
        "LOWER(c.taxId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
        "LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :query, '%')))"
    )
    Page<Customer> searchByClientAccount(@Param("query") String query, @Param("clientAccountId") Long clientAccountId, Pageable pageable);

    /**
     * Search all customers including those with associations
     */
    @Query(
        "SELECT DISTINCT c FROM Customer c " +
        "LEFT JOIN c.associations a " +
        "WHERE (c.createdByClientAccount.id = :clientAccountId OR " +
        "(a.clientAccount.id = :clientAccountId AND a.status = 'ACTIVE')) AND " +
        "c.enabled = true AND " +
        "(LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
        "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
        "LOWER(c.phone) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
        "LOWER(c.taxId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
        "LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :query, '%')))"
    )
    Page<Customer> searchAllAccessibleByClientAccount(
        @Param("query") String query,
        @Param("clientAccountId") Long clientAccountId,
        Pageable pageable
    );

    // Statistics queries using projections
    /**
     * Count customers by client account
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdByClientAccount.id = :clientAccountId")
    Long countByClientAccount(@Param("clientAccountId") Long clientAccountId);

    /**
     * Count enabled customers by client account
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdByClientAccount.id = :clientAccountId AND c.enabled = true")
    Long countEnabledByClientAccount(@Param("clientAccountId") Long clientAccountId);

    /**
     * Count disabled customers by client account
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdByClientAccount.id = :clientAccountId AND c.enabled = false")
    Long countDisabledByClientAccount(@Param("clientAccountId") Long clientAccountId);

    /**
     * Count customers with user accounts by client account
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdByClientAccount.id = :clientAccountId AND c.user IS NOT NULL")
    Long countWithUserAccountByClientAccount(@Param("clientAccountId") Long clientAccountId);

    /**
     * Count customers without user accounts by client account
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdByClientAccount.id = :clientAccountId AND c.user IS NULL")
    Long countWithoutUserAccountByClientAccount(@Param("clientAccountId") Long clientAccountId);

    /**
     * Count managed customers (created by client, no user account)
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdByClientAccount.id = :clientAccountId AND c.user IS NULL")
    Long countManagedCustomersByClientAccount(@Param("clientAccountId") Long clientAccountId);

    /**
     * Count independent customers (have user accounts)
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdByClientAccount.id = :clientAccountId AND c.user IS NOT NULL")
    Long countIndependentCustomersByClientAccount(@Param("clientAccountId") Long clientAccountId);

    // Association counting
    /**
     * Count total associations for client account
     */
    @Query("SELECT COUNT(a) FROM CustomerClientAssociation a WHERE a.clientAccount.id = :clientAccountId AND a.status = 'ACTIVE'")
    Long countAssociationsByClientAccount(@Param("clientAccountId") Long clientAccountId);

    // Soft delete queries
    /**
     * Find all customers including soft deleted ones
     */
    Page<Customer> findAllByCreatedByClientAccountIdOrderByEnabledDescCreatedDateDesc(Long clientAccountId, Pageable pageable);

    /**
     * Find soft deleted customers
     */
    Page<Customer> findAllByCreatedByClientAccountIdAndEnabledFalse(Long clientAccountId, Pageable pageable);

    // Phone and email lookup for account creation
    /**
     * Find customers by phone for account creation
     */
    List<Customer> findByPhoneAndUserIsNull(String phone);

    /**
     * Check if independent customer exists (has user account)
     */
    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.user IS NOT NULL AND " + "(c.phone = :phone OR c.user.email = :email)")
    boolean existsIndependentCustomerByPhoneOrEmail(@Param("phone") String phone, @Param("email") String email);
}
