package org.semanticcloud.semanticEngine.controll.reading.owlApi.propertyFactories;



import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;

import org.semanticcloud.semanticEngine.entity.OntoProperty;
import org.semanticcloud.semanticEngine.entity.properties.IntegerProperty;

public class IntegerPropertyFactory extends SimpleDatatypePropertyFactory {
	public IntegerPropertyFactory(){
		super("http://www.w3.org/2001/XMLSchema#integer", "http://www.w3.org/2001/XMLSchema#int");
	}

	@Override
	public OntoProperty createProperty(OWLOntology onto, OWLProperty property) {
		return new IntegerProperty(property.getIRI().getNamespace(), property.getIRI().getFragment());
	}
}
