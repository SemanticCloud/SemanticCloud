package org.semanticcloud.semanticEngine.controll.reading.owlApi.propertyFactories;


import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;

import org.semanticcloud.semanticEngine.model.ontology.OntoProperty;
import org.semanticcloud.semanticEngine.model.ontology.properties.StringProperty;

public class StringPropertyFactory extends SimpleDatatypePropertyFactory {
	public StringPropertyFactory(){
		super("http://www.w3.org/2001/XMLSchema#string", "http://www.w3.org/2001/XMLSchema#boolean");
	}
	
	@Override
	public OntoProperty createProperty(OWLOntology onto, OWLProperty property) {
		return new StringProperty(property.getIRI().getNamespace(), property.getIRI().getFragment());
	}

}
