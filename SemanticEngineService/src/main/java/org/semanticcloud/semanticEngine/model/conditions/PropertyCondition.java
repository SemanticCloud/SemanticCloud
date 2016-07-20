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
public class PropertyCondition<V,T extends OntoProperty>{
	private String uri;
	private String type;
	protected V value;

	//needed ?
	private T property;

	public PropertyCondition(){
		
	}
	
	public PropertyCondition(String uri) {
		this.uri = uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
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

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}
}
