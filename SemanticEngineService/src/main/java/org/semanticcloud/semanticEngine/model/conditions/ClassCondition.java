package org.semanticcloud.semanticEngine.model.conditions;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ClassCondition {
	private String uri;
	private List<PropertyCondition> propertyConditions = new ArrayList<>();

	public void addProperty(PropertyCondition propertyCondition) {
		propertyConditions.add(propertyCondition);
	}
}
