package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.Address;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Address entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByIdAndCustomerId(Long id, Long customerId);
}
