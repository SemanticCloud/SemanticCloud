package org.semanticcloud.repository.search;

import org.semanticcloud.domain.Credentials;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Credentials entity.
 */
public interface CredentialsSearchRepository extends ElasticsearchRepository<Credentials, Long> {
}
