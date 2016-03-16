package org.semanticcloud.semanticEngine.model.conditions;

import java.util.ArrayList;
import java.util.List;

public class ClassCondition {
	private String classUri;
	private List<PropertyCondition> propertyConditions = new ArrayList<>();
		
	public String getClassUri() {
		return classUri;
	}

	public void setClassUri(String classUri) {
		this.classUri = classUri;
	}

	public List<PropertyCondition> getPropertyConditions() {
		return propertyConditions;
	}

	public void setPropertyConditions(List<PropertyCondition> propertyConditions) {
		this.propertyConditions = propertyConditions;
	}

	public void addProperty(PropertyCondition propertyCondition) {
		propertyConditions.add(propertyCondition);
	}
}
