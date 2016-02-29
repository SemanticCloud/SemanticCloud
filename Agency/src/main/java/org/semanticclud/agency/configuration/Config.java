package org.semanticclud.agency.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix="agency")
@Configuration
public class Config {

    private List<ProviderConfiguration> providers = new ArrayList<ProviderConfiguration>();


    public List<ProviderConfiguration> getProviders() {
        return providers;
    }

    public void setProviders(List<ProviderConfiguration> providers) {
        this.providers = providers;
    }
}
