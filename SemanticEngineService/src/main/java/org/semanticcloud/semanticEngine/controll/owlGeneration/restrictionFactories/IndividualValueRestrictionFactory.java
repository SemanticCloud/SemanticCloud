package org.semanticcloud.semanticEngine.controll.owlGeneration.restrictionFactories;

import java.util.ArrayList;
import java.util.List;

import org.semanticcloud.semanticEngine.entity.models.ConfigurationException;
import org.semanticcloud.semanticEngine.controll.owlGeneration.RestrictionFactory;
import org.semanticcloud.semanticEngine.model.conditions.IndividualCondition;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;


public class IndividualValueRestrictionFactory extends RestrictionFactory<IndividualCondition> {
	private final OWLDataFactory factory;
	
	public IndividualValueRestrictionFactory(OWLDataFactory factory) {
		this.factory = factory;
	}
	
	@Override
	public OWLClassExpression createRestriction(IndividualCondition condition) {
		OWLObjectProperty conditionProperty = factory.getOWLObjectProperty(IRI.create(condition.getPropertyUri()));
		OWLNamedIndividual value = factory.getOWLNamedIndividual(IRI.create(condition.getIndividualValue()));
		OWLObjectHasValue restriction = factory.getOWLObjectHasValue(conditionProperty, value);
		return restriction;
	}

	@Override
	public List<OWLAxiom> createIndividualValue(IndividualCondition condition, OWLIndividual individual) throws ConfigurationException {
		OWLObjectProperty conditionProperty = factory.getOWLObjectProperty(IRI.create(condition.getPropertyUri()));
		OWLIndividual valueIndividual = factory.getOWLNamedIndividual(IRI.create(condition.getIndividualValue()));

		OWLObjectPropertyAssertionAxiom assertion = factory.getOWLObjectPropertyAssertionAxiom(conditionProperty, individual, valueIndividual);
		ArrayList<OWLAxiom> result = new ArrayList<OWLAxiom>();
		result.add(assertion);
		
		return result;
	}
	

}
