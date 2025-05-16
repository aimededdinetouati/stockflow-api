package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.ResourceLimit;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ResourceLimit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResourceLimitRepository extends JpaRepository<ResourceLimit, Long> {}
