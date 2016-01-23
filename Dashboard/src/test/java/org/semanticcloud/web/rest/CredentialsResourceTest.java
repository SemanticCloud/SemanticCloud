package org.semanticcloud.web.rest;

import org.semanticcloud.Application;
import org.semanticcloud.domain.Credentials;
import org.semanticcloud.repository.CredentialsRepository;
import org.semanticcloud.repository.search.CredentialsSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the CredentialsResource REST controller.
 *
 * @see CredentialsResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class CredentialsResourceTest {

    private static final String DEFAULT_IDENTITY = "SAMPLE_TEXT";
    private static final String UPDATED_IDENTITY = "UPDATED_TEXT";
    private static final String DEFAULT_CREDENTIAL = "SAMPLE_TEXT";
    private static final String UPDATED_CREDENTIAL = "UPDATED_TEXT";

    @Inject
    private CredentialsRepository credentialsRepository;

    @Inject
    private CredentialsSearchRepository credentialsSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCredentialsMockMvc;

    private Credentials credentials;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CredentialsResource credentialsResource = new CredentialsResource();
        ReflectionTestUtils.setField(credentialsResource, "credentialsRepository", credentialsRepository);
        ReflectionTestUtils.setField(credentialsResource, "credentialsSearchRepository", credentialsSearchRepository);
        this.restCredentialsMockMvc = MockMvcBuilders.standaloneSetup(credentialsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        credentials = new Credentials();
        credentials.setIdentity(DEFAULT_IDENTITY);
        credentials.setCredential(DEFAULT_CREDENTIAL);
    }

    @Test
    @Transactional
    public void createCredentials() throws Exception {
        int databaseSizeBeforeCreate = credentialsRepository.findAll().size();

        // Create the Credentials

        restCredentialsMockMvc.perform(post("/api/credentialss")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(credentials)))
                .andExpect(status().isCreated());

        // Validate the Credentials in the database
        List<Credentials> credentialss = credentialsRepository.findAll();
        assertThat(credentialss).hasSize(databaseSizeBeforeCreate + 1);
        Credentials testCredentials = credentialss.get(credentialss.size() - 1);
        assertThat(testCredentials.getIdentity()).isEqualTo(DEFAULT_IDENTITY);
        assertThat(testCredentials.getCredential()).isEqualTo(DEFAULT_CREDENTIAL);
    }

    @Test
    @Transactional
    public void getAllCredentialss() throws Exception {
        // Initialize the database
        credentialsRepository.saveAndFlush(credentials);

        // Get all the credentialss
        restCredentialsMockMvc.perform(get("/api/credentialss"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(credentials.getId().intValue())))
                .andExpect(jsonPath("$.[*].identity").value(hasItem(DEFAULT_IDENTITY.toString())))
                .andExpect(jsonPath("$.[*].credential").value(hasItem(DEFAULT_CREDENTIAL.toString())));
    }

    @Test
    @Transactional
    public void getCredentials() throws Exception {
        // Initialize the database
        credentialsRepository.saveAndFlush(credentials);

        // Get the credentials
        restCredentialsMockMvc.perform(get("/api/credentialss/{id}", credentials.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(credentials.getId().intValue()))
            .andExpect(jsonPath("$.identity").value(DEFAULT_IDENTITY.toString()))
            .andExpect(jsonPath("$.credential").value(DEFAULT_CREDENTIAL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCredentials() throws Exception {
        // Get the credentials
        restCredentialsMockMvc.perform(get("/api/credentialss/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCredentials() throws Exception {
        // Initialize the database
        credentialsRepository.saveAndFlush(credentials);

		int databaseSizeBeforeUpdate = credentialsRepository.findAll().size();

        // Update the credentials
        credentials.setIdentity(UPDATED_IDENTITY);
        credentials.setCredential(UPDATED_CREDENTIAL);
        

        restCredentialsMockMvc.perform(put("/api/credentialss")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(credentials)))
                .andExpect(status().isOk());

        // Validate the Credentials in the database
        List<Credentials> credentialss = credentialsRepository.findAll();
        assertThat(credentialss).hasSize(databaseSizeBeforeUpdate);
        Credentials testCredentials = credentialss.get(credentialss.size() - 1);
        assertThat(testCredentials.getIdentity()).isEqualTo(UPDATED_IDENTITY);
        assertThat(testCredentials.getCredential()).isEqualTo(UPDATED_CREDENTIAL);
    }

    @Test
    @Transactional
    public void deleteCredentials() throws Exception {
        // Initialize the database
        credentialsRepository.saveAndFlush(credentials);

		int databaseSizeBeforeDelete = credentialsRepository.findAll().size();

        // Get the credentials
        restCredentialsMockMvc.perform(delete("/api/credentialss/{id}", credentials.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Credentials> credentialss = credentialsRepository.findAll();
        assertThat(credentialss).hasSize(databaseSizeBeforeDelete - 1);
    }
}
