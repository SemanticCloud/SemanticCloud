package org.semanticcloud.semanticEngine.model.conditions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.semanticcloud.semanticEngine.model.ontology.OntoProperty;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
	property = "type"
)
@JsonSubTypes({
		@JsonSubTypes.Type(value = ClassPropertyCondition.class , name = "classConstraint"),
		@JsonSubTypes.Type(value = IndividualCondition.class , name = "individualValue"),
		@JsonSubTypes.Type(value = DatatypePropertyCondition.class , name = "datatypeValue"),
})
public class PropertyCondition<T extends OntoProperty>{
	private String propertyUri;
	private String type;
	//needed ?
	private T property;

	public PropertyCondition(){
		
	}
	
	public PropertyCondition(String propertyUri) {
		this.propertyUri = propertyUri;
	}

	public void setPropertyUri(String propertyUri) {
		this.propertyUri = propertyUri;
	}

	public String getPropertyUri() {
		return propertyUri;
	}

	public void setProperty(T property) {
		this.property = property;		
	}
	
	public T getProperty() {
		return property;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
