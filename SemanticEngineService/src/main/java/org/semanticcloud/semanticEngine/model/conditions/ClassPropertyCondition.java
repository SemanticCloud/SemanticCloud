package org.semanticcloud.semanticEngine.model.conditions;

public class ClassPropertyCondition extends PropertyCondition {
	private ClassCondition classConstraintValue;

	public ClassPropertyCondition(){
		
	}
	
	public ClassPropertyCondition(String propertyUri, ClassCondition classConstraintValue) {
		super(propertyUri);
		this.classConstraintValue = classConstraintValue;
	}

	public void setClassConstraintValue(ClassCondition classConstraintValue) {
		this.classConstraintValue = classConstraintValue;
	}

	public ClassCondition getClassConstraintValue() {
		return classConstraintValue;
	}
}
