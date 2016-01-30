package org.semanticcloud.semanticEngine.controll.reading;

import java.util.List;

import org.semanticcloud.semanticEngine.entity.models.ConfigurationException;
import org.semanticcloud.semanticEngine.entity.models.PropertyProvider;
import org.semanticcloud.semanticEngine.entity.OwlClass;
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
	
	public abstract OwlClass getOwlClass(String className);

	public abstract OntoProperty getProperty(String propertyUri) throws ConfigurationException;

	public abstract List<OwlIndividual> getIndividualsInRange(OwlClass owlClass, OntoProperty property);

	public abstract List<OwlClass> getClassesInRange(OwlClass owlClass, OntoProperty property);

    public abstract List<OwlClass> getOwlSubclasses(String classUri);
}