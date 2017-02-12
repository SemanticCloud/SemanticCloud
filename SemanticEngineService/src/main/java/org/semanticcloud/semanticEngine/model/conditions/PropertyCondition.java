package org.semanticcloud.semanticEngine.model.conditions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import org.semanticcloud.semanticEngine.model.ontology.OntoProperty;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
	property = "type"
)
@JsonSubTypes({
		@JsonSubTypes.Type(value = ClassPropertyCondition.class , name = "classConstraint"),
		@JsonSubTypes.Type(value = IndividualCondition.class , name = "individualValue"),
		@JsonSubTypes.Type(value = DatatypePropertyCondition.class , name = "datatypeValue"),
})
@Getter
@Setter
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
}
