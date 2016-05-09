package org.semanticcloud.semanticEngine.controll.owlGeneration;

import java.util.HashMap;
import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;

import org.semanticcloud.semanticEngine.model.ConfigurationException;
import org.semanticcloud.semanticEngine.model.conditions.PropertyCondition;


public abstract class RestrictionFactory<T extends PropertyCondition> {
	private static HashMap<Class, Object> factories = new HashMap<Class, Object>();
	
	public static <U extends PropertyCondition> void registerRestrictionTypeFactory(Class<U> type, RestrictionFactory<U> factory) {
		factories.put(type, factory);		
	}

	public static <U extends PropertyCondition> RestrictionFactory<U> getRestrictionFactory(U condition) throws ConfigurationException {
		if(!factories.containsKey(condition.getClass())){
			throw new ConfigurationException(String.format("RestrictionFactory for class %s has not been configured.", condition.getClass().getName()));
		}
		return (RestrictionFactory<U>)  factories.get(condition.getClass());
	}
	
	public abstract OWLClassExpression createRestriction(T condition) throws ConfigurationException;
	public abstract List<OWLAxiom> createIndividualValue(T condition, OWLIndividual individual) throws ConfigurationException;
}
