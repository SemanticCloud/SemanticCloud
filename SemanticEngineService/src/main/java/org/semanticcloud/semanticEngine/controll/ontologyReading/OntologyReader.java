package org.semanticcloud.semanticEngine.controll.ontologyReading;

import java.util.List;

import org.semanticcloud.semanticEngine.entity.models.ConfigurationException;
import org.semanticcloud.semanticEngine.entity.models.PropertyProvider;
import org.semanticcloud.semanticEngine.entity.OntoClass;
import org.semanticcloud.semanticEngine.entity.OntoProperty;
import org.semanticcloud.semanticEngine.entity.OwlIndividual;

public abstract class OntologyReader implements PropertyProvider{
	
	private static OntologyReader instance;
	
	public static void setGlobalInstance(OntologyReader reader) {
		instance = reader;
	}

	public static OntologyReader getGlobalInstance() {
		return instance;
	}
	
	public abstract OntoClass getOwlClass(String className);

	public abstract OntoProperty getProperty(String propertyUri) throws ConfigurationException;

	public abstract List<OwlIndividual> getIndividualsInRange(OntoClass owlClass, OntoProperty property);

	public abstract List<OntoClass> getClassesInRange(OntoClass owlClass, OntoProperty property);

    public abstract List<OntoClass> getOwlSubclasses(String classUri);
}