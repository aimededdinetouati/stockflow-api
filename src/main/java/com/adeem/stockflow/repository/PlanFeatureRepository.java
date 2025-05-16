package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.PlanFeature;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PlanFeature entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlanFeatureRepository extends JpaRepository<PlanFeature, Long> {}
