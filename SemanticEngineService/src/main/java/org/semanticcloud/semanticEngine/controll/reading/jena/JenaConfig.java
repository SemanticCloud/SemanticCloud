package org.semanticcloud.semanticEngine.controll.reading.jena;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@ConfigurationProperties(prefix="jena")
@Configuration
public class JenaConfig {
	private HashMap<String, String> localMappings = new HashMap<>();

    private boolean ignorePropsWithNoDomain;

    private String baseOntology;
	
	public HashMap<String, String> getLocalMappings() {
		return localMappings;
	}

    public String getBaseOntology() {
        return baseOntology;
    }

    public void setBaseOntology(String baseOntology) {
        this.baseOntology = baseOntology;
    }

    public boolean isIgnorePropsWithNoDomain() {
        return ignorePropsWithNoDomain;
    }

    public void setIgnorePropsWithNoDomain(boolean ignorePropsWithNoDomain) {
        this.ignorePropsWithNoDomain = ignorePropsWithNoDomain;
    }
}
