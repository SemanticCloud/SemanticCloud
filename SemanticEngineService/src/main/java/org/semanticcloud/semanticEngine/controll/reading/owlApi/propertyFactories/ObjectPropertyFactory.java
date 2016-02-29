package org.semanticcloud.semanticEngine.controll.reading.owlApi.propertyFactories;

import org.semanticcloud.semanticEngine.model.ontology.OntoProperty;
import org.semanticcloud.semanticEngine.controll.reading.owlApi.OwlPropertyFactory;
import org.semanticcloud.semanticEngine.model.ontology.properties.OwlObjectProperty;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;

public class ObjectPropertyFactory extends OwlPropertyFactory {
	@Override
	public boolean canCreateProperty(OWLOntology onto, OWLProperty property) {
		return property.isOWLObjectProperty();
	}

	@Override
	public OntoProperty createProperty(OWLOntology onto, OWLProperty property) {
		return new OwlObjectProperty(property.getIRI().getNamespace(), property.getIRI().getFragment());
	}

}
