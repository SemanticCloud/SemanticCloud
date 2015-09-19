package org.semanticcloud.semanticEngine.entity.models;

import org.semanticcloud.semanticEngine.entity.OntoProperty;


public interface PropertyProvider {

	OntoProperty getProperty(String propertyUri)  throws ConfigurationException;

}