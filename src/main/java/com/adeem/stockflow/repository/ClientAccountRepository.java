package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.ClientAccount;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClientAccount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClientAccountRepository extends JpaRepository<ClientAccount, Long> {
    List<ClientAccount> findByCompanyName(String companyName);
}
