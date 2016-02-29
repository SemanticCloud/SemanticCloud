package org.semanticcloud.semanticEngine.controll.reading.jena.propertyFactories;

import org.semanticcloud.semanticEngine.model.ontology.OntoProperty;
import org.semanticcloud.semanticEngine.controll.reading.jena.OwlPropertyFactory;
import org.semanticcloud.semanticEngine.model.ontology.properties.OwlObjectProperty;
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
