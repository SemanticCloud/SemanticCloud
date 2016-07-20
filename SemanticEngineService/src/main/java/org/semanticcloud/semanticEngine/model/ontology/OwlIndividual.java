package org.semanticcloud.semanticEngine.model.ontology;

import org.semanticcloud.semanticEngine.model.conditions.PropertyCondition;
import org.apache.commons.lang.NullArgumentException;
import org.apache.jena.ontology.Individual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.util.Iterator;
import java.util.List;

public class OwlIndividual extends OwlElement {
	private String uri;
	private String localName;
	private String classUri;
	private List<PropertyCondition> properties;
	
	public OwlIndividual(Individual individual, List<PropertyCondition> properties) {
		if(properties == null){
			throw new NullArgumentException("properties");
		}
		this.uri = individual.getURI();
		this.localName = individual.getLocalName();
		this.properties = properties;
	}
	
	public OwlIndividual(OWLNamedIndividual individual, List<PropertyCondition> properties) {
		if(properties == null){
			throw new NullArgumentException("properties");
		}
		this.uri = individual.getIRI().toString();
		this.localName = individual.getIRI().getFragment();
		this.properties = properties;
	}
	
	public String getUri(){
		return uri;
	}
	
	public String getClassUri(){
		return uri;
	}
	
	public List<PropertyCondition> getProperties() {
		return properties;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ uri.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OwlIndividual other = (OwlIndividual) obj;
		return uri.equals(other.getUri());
	}

	public PropertyCondition getProperty(String uri) {
		for (Iterator it = properties.iterator(); it.hasNext();) {
			PropertyCondition prop = (PropertyCondition) it.next();
			
			if(prop.getUri().equals(uri)){
				return prop;
			}
		}
		return null;
	}
	
	
}
