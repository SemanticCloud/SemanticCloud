package org.semanticcloud.semanticEngine.controll.owlGeneration.restrictionFactories;

import java.util.ArrayList;
import java.util.List;

import org.semanticcloud.semanticEngine.entity.models.ConfigurationException;
import org.semanticcloud.semanticEngine.controll.owlGeneration.IndividualGenerator;
import org.semanticcloud.semanticEngine.controll.owlGeneration.RestrictionFactory;
import org.semanticcloud.semanticEngine.controll.owlGeneration.ClassRestrictionGenerator;
import org.semanticcloud.semanticEngine.model.conditions.ClassPropertyCondition;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;


public class ClassValueRestrictionFactory extends RestrictionFactory<ClassPropertyCondition> {
	private final OWLDataFactory factory;
	private final ClassRestrictionGenerator classRestrictionGenerator;
	private final IndividualGenerator individualGenerator;
	
	//TODO: Extract some small interface from OWLApiKB just for generating class restrictions
	public ClassValueRestrictionFactory(ClassRestrictionGenerator classRestrictionGenerator, IndividualGenerator individualGenerator, 
			OWLDataFactory factory) {
		this.classRestrictionGenerator = classRestrictionGenerator;
		this.individualGenerator = individualGenerator;
		this.factory = factory;
	}
	
	@Override
	public OWLClassExpression createRestriction(ClassPropertyCondition condition) throws ConfigurationException {
		OWLObjectProperty conditionProperty = factory.getOWLObjectProperty(IRI.create(condition.getPropertyUri()));

		OWLClassExpression nestedCondition = classRestrictionGenerator.convertToOntClass(null, condition.getClassConstraintValue());
		OWLObjectSomeValuesFrom propertyRestriction = factory.getOWLObjectSomeValuesFrom(conditionProperty, nestedCondition);
		return propertyRestriction;
	}

	@Override
	public List<OWLAxiom> createIndividualValue(ClassPropertyCondition condition, OWLIndividual individual) throws ConfigurationException {
		OWLObjectProperty conditionProperty = factory.getOWLObjectProperty(IRI.create(condition.getPropertyUri()));
		OWLIndividual nestedIndividual = factory.getOWLAnonymousIndividual();

		List<OWLAxiom> nestedAxioms = individualGenerator.createPropertyAxioms(nestedIndividual, condition.getClassConstraintValue());
		OWLObjectPropertyAssertionAxiom assertion = factory.getOWLObjectPropertyAssertionAxiom(conditionProperty, individual, nestedIndividual);
		ArrayList<OWLAxiom> result = new ArrayList<OWLAxiom>();
		result.addAll(nestedAxioms);
		result.add(assertion);
		
		return result;
	}
	

}
