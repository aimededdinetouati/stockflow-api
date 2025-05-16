package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.ProductFamily;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductFamily entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductFamilyRepository extends JpaRepository<ProductFamily, Long> {}
