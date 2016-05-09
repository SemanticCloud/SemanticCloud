package org.semanticcloud.semanticEngine.controll.owlGeneration;

import java.util.ArrayList;
import java.util.List;

import org.semanticcloud.semanticEngine.model.conditions.ClassCondition;
import org.semanticcloud.semanticEngine.model.ConfigurationException;
import org.semanticcloud.semanticEngine.model.conditions.PropertyCondition;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;

public class IndividualGenerator{
	
	private final OWLDataFactory factory;

	public IndividualGenerator(OWLDataFactory factory){
		this.factory = factory;
	}
	
	public List<OWLAxiom> convertToOntIndividual(String individualUri, ClassCondition condition) throws ConfigurationException {
		OWLIndividual individual = factory.getOWLNamedIndividual(IRI.create(individualUri));
		
		return createPropertyAxioms(individual, condition);
	}

	public List<OWLAxiom> createPropertyAxioms2(OWLIndividual individual, ClassCondition condition) throws ConfigurationException {
		OWLClass conditionClass = factory.getOWLClass(IRI.create(condition.getClassUri()));
		
		OWLClassAssertionAxiom classAssertionAxiom = factory.getOWLClassAssertionAxiom(conditionClass, individual);
				
		ArrayList<OWLAxiom> individualAxioms = new ArrayList<OWLAxiom>();
		individualAxioms.add(classAssertionAxiom);
		
		for (PropertyCondition cond : condition.getPropertyConditions()) {
			RestrictionFactory restrictionFactory = RestrictionFactory.getRestrictionFactory(cond);
			List<OWLAxiom> propertyAxioms = restrictionFactory.createIndividualValue(cond, individual);
			individualAxioms.addAll(propertyAxioms);
		}
		return individualAxioms;
	}

    public List<OWLAxiom> createPropertyAxioms(OWLIndividual individual, ClassCondition condition) throws ConfigurationException {
        ArrayList<OWLAxiom> individualAxioms = new ArrayList<OWLAxiom>();

        if (individual.isNamed()) {
            OWLDeclarationAxiom individualAxiom = factory.getOWLDeclarationAxiom(individual.asOWLNamedIndividual());
            individualAxioms.add(individualAxiom);
        }

        OWLClass conditionClass = factory.getOWLClass(IRI.create(condition.getClassUri()));

        OWLClassAssertionAxiom classAssertionAxiom = factory.getOWLClassAssertionAxiom(conditionClass, individual);

        individualAxioms.add(classAssertionAxiom);

        for (PropertyCondition cond : condition.getPropertyConditions()) {
            RestrictionFactory restrictionFactory = RestrictionFactory.getRestrictionFactory(cond);
            List<OWLAxiom> propertyAxioms = restrictionFactory.createIndividualValue(cond, individual);
            individualAxioms.addAll(propertyAxioms);
        }
        return individualAxioms;
    }
}