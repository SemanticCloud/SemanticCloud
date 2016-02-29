package org.semanticcloud.semanticEngine.model.conditions;

public class IndividualCondition extends PropertyCondition {
	private String individualValue;

	public IndividualCondition(){
	}
	
	public IndividualCondition(String propertyUri, String individualValue) {
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
