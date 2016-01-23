package org.semanticcloud.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.semanticcloud.domain.Credentials;
import org.semanticcloud.repository.CredentialsRepository;
import org.semanticcloud.repository.search.CredentialsSearchRepository;
import org.semanticcloud.web.rest.util.HeaderUtil;
import org.semanticcloud.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Credentials.
 */
@RestController
@RequestMapping("/api")
public class CredentialsResource {

    private final Logger log = LoggerFactory.getLogger(CredentialsResource.class);

    @Inject
    private CredentialsRepository credentialsRepository;

    @Inject
    private CredentialsSearchRepository credentialsSearchRepository;

    /**
     * POST  /credentialss -> Create a new credentials.
     */
    @RequestMapping(value = "/credentialss",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Credentials> createCredentials(@RequestBody Credentials credentials) throws URISyntaxException {
        log.debug("REST request to save Credentials : {}", credentials);
        if (credentials.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new credentials cannot already have an ID").body(null);
        }
        Credentials result = credentialsRepository.save(credentials);
        credentialsSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/credentialss/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("credentials", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /credentialss -> Updates an existing credentials.
     */
    @RequestMapping(value = "/credentialss",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Credentials> updateCredentials(@RequestBody Credentials credentials) throws URISyntaxException {
        log.debug("REST request to update Credentials : {}", credentials);
        if (credentials.getId() == null) {
            return createCredentials(credentials);
        }
        Credentials result = credentialsRepository.save(credentials);
        credentialsSearchRepository.save(credentials);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("credentials", credentials.getId().toString()))
                .body(result);
    }

    /**
     * GET  /credentialss -> get all the credentialss.
     */
    @RequestMapping(value = "/credentialss",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Credentials>> getAllCredentialss(Pageable pageable)
        throws URISyntaxException {
        Page<Credentials> page = credentialsRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/credentialss");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /credentialss/:id -> get the "id" credentials.
     */
    @RequestMapping(value = "/credentialss/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Credentials> getCredentials(@PathVariable Long id) {
        log.debug("REST request to get Credentials : {}", id);
        return Optional.ofNullable(credentialsRepository.findOne(id))
            .map(credentials -> new ResponseEntity<>(
                credentials,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /credentialss/:id -> delete the "id" credentials.
     */
    @RequestMapping(value = "/credentialss/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCredentials(@PathVariable Long id) {
        log.debug("REST request to delete Credentials : {}", id);
        credentialsRepository.delete(id);
        credentialsSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("credentials", id.toString())).build();
    }

    /**
     * SEARCH  /_search/credentialss/:query -> search for the credentials corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/credentialss/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Credentials> searchCredentialss(@PathVariable String query) {
        return StreamSupport
            .stream(credentialsSearchRepository.search(queryString(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
