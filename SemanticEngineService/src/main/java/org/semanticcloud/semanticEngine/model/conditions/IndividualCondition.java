package org.semanticcloud.semanticEngine.model.conditions;

import org.semanticcloud.semanticEngine.model.ontology.OntoProperty;

public class IndividualCondition<T extends OntoProperty> extends PropertyCondition<String, T> {

	public IndividualCondition(){
	}
	
	public IndividualCondition(String propertyUri, String individualValue) {
		super(propertyUri);
		this.value = individualValue;
	}

	public void setIndividualValue(String individualValue) {
		this.value = individualValue;
	}

	public String getIndividualValue() {
		return value;
	}
}
