package org.semanticcloud.semanticEngine.controll.reading;

import org.semanticcloud.semanticEngine.model.ConfigurationException;
import org.semanticcloud.semanticEngine.model.ontology.OntoProperty;
import org.semanticcloud.semanticEngine.model.ontology.OwlClass;
import org.semanticcloud.semanticEngine.model.ontology.OwlIndividual;

import java.util.List;

public abstract class OntologyReader {
	
	public abstract OwlClass getOwlClass(String className);

	public abstract OntoProperty getProperty(String propertyUri) throws ConfigurationException;

	public abstract List<OwlIndividual> getIndividualsInRange(OwlClass owlClass, OntoProperty property);

	public abstract List<OwlClass> getClassesInRange(OwlClass owlClass, OntoProperty property);

    public abstract List<OwlClass> getOwlSubclasses(String classUri);
}