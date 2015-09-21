package org.semanticcloud.semanticEngine.controll.ontologyReading.jena;

import java.util.ArrayList;
import java.util.List;

import org.semanticcloud.semanticEngine.entity.models.InvalidConfigurationException;
import org.semanticcloud.semanticEngine.entity.OntoProperty;
import org.apache.jena.ontology.OntProperty;

public abstract class OwlPropertyFactory {
	private static List<OwlPropertyFactory> factories = new ArrayList<OwlPropertyFactory>();
	public static void registerPropertyFactory(
			OwlPropertyFactory datatypePropertyFactory) {
		factories.add(datatypePropertyFactory);		
	}
	
	public static OntoProperty createOwlProperty(OntProperty ontProperty){
		for(OwlPropertyFactory fact : factories){
			if(fact.canCreateProperty(ontProperty))
				return fact.createProperty(ontProperty);
		}
		throw new InvalidConfigurationException(String.format("Failed to create a property description object for property: %s. No property factory has been found to handle the property type: %s.", ontProperty.getURI(), ontProperty.getRange()));
	}
	
	public abstract boolean canCreateProperty(OntProperty ontProperty);
	public abstract OntoProperty createProperty(OntProperty ontProperty);	
}
