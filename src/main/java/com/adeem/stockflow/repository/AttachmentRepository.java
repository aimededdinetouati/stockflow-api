package com.adeem.stockflow.repository;

import com.adeem.stockflow.domain.Attachment;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Attachment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    @Query("select attachment from Attachment attachment where attachment.user.login = ?#{authentication.name}")
    List<Attachment> findByUserIsCurrentUser();
}
