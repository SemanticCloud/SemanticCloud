package org.semanticcloud.semanticEngine.entity.models.propertyConditions;

import org.semanticcloud.semanticEngine.entity.models.ClassCondition;
import org.semanticcloud.semanticEngine.entity.models.PropertyValueCondition;

public class ClassValueCondition extends PropertyValueCondition {
	private ClassCondition classConstraintValue;

	public ClassValueCondition(){
		
	}
	
	public ClassValueCondition(String propertyUri, ClassCondition classConstraintValue) {
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
