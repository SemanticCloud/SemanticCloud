package org.semanticcloud.semanticEngine.model.conditions;

import java.util.ArrayList;
import java.util.List;

public class ClassCondition {
	private String uri;
	private List<PropertyCondition> propertyConditions = new ArrayList<>();
		
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
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
