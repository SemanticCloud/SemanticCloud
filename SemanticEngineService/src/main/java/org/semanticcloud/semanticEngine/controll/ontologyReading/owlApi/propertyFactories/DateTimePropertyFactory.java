package org.semanticcloud.semanticEngine.controll.ontologyReading.owlApi.propertyFactories;

import org.semanticcloud.semanticEngine.entity.OntoProperty;
import org.semanticcloud.semanticEngine.entity.properties.DateTimeProperty;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;

public class DateTimePropertyFactory extends SimpleDatatypePropertyFactory {
	public DateTimePropertyFactory(){
		super("http://www.w3.org/2001/XMLSchema#datetime");
	}

	@Override
	public OntoProperty createProperty(OWLOntology onto, OWLProperty property) {
		return new DateTimeProperty(property.getIRI().getNamespace(), property.getIRI().getFragment());
	}

}
