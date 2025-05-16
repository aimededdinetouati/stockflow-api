package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.PlanFormula;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PlanFormula entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlanFormulaRepository extends JpaRepository<PlanFormula, Long> {}
