package org.semanticcloud.semanticEngine.model.conditions;

import org.semanticcloud.semanticEngine.model.ontology.OntoProperty;

public class ClassPropertyCondition<T extends OntoProperty> extends PropertyCondition<ClassCondition,T> {
	public ClassPropertyCondition(){
		
	}
	
	public ClassPropertyCondition(String propertyUri, ClassCondition classConstraintValue) {
		super(propertyUri);
		this.value = classConstraintValue;
	}

	public void setClassConstraintValue(ClassCondition classConstraintValue) {
		this.value = classConstraintValue;
	}

	public ClassCondition getClassConstraintValue() {
		return value;
	}
}
