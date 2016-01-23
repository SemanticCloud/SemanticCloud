package org.semanticcloud.repository;

import org.semanticcloud.domain.Credentials;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Credentials entity.
 */
public interface CredentialsRepository extends JpaRepository<Credentials,Long> {

    @Query("select credentials from Credentials credentials where credentials.owner.login = ?#{principal.username}")
    List<Credentials> findByOwnerIsCurrentUser();

}
