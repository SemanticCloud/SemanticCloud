package org.semanticcloud.semanticEngine.controll.reading.jena;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@ConfigurationProperties(prefix="jena")
@Configuration
public class JenaConfig {
	private HashMap<String, String> localMappings = new HashMap<String, String>();
	private boolean ignorePropsWithNoDomain;
    private String baseOntology;
	
	public JenaConfig useLocalMapping(String uri, String filePath) {
		localMappings.put(uri, filePath);
		return this;
	}
	
	public JenaConfig ignorePropertiesWithUnspecifiedDomain(){
		ignorePropsWithNoDomain = true;
		return this;
	}

	public boolean isIgnorePropsWithNoDomain(){
		return ignorePropsWithNoDomain;
	}
	
	public HashMap<String, String> getLocalMappings() {
		return localMappings;
	}

    public String getBaseOntology() {
        return baseOntology;
    }
}
