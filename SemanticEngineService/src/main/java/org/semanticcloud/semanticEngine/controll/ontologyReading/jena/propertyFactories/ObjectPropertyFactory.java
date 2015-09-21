package org.semanticcloud.semanticEngine.controll.ontologyReading.jena.propertyFactories;

import org.semanticcloud.semanticEngine.entity.OntoProperty;
import org.semanticcloud.semanticEngine.controll.ontologyReading.jena.OwlPropertyFactory;
import org.semanticcloud.semanticEngine.entity.properties.OwlObjectProperty;
import org.apache.jena.ontology.OntProperty;


public class ObjectPropertyFactory extends OwlPropertyFactory {

	@Override
	public boolean canCreateProperty(OntProperty ontProperty) {
		return ontProperty.isObjectProperty();
	}

	@Override
	public OntoProperty createProperty(OntProperty ontProperty) {
		return new OwlObjectProperty(ontProperty.getNameSpace(), ontProperty.getLocalName());
	}

}
