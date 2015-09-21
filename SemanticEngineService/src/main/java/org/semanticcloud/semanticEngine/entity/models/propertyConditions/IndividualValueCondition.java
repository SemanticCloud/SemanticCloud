package org.semanticcloud.semanticEngine.entity.models.propertyConditions;

import org.semanticcloud.semanticEngine.entity.models.PropertyValueCondition;

public class IndividualValueCondition extends PropertyValueCondition {
	private String individualValue;

	public IndividualValueCondition(){		
	}
	
	public IndividualValueCondition(String propertyUri, String individualValue) {
		super(propertyUri);
		this.individualValue = individualValue;
	}

	public void setIndividualValue(String individualValue) {
		this.individualValue = individualValue;
	}

	public String getIndividualValue() {
		return individualValue;
	}
}
