package org.semanticcloud.semanticEngine.entity.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.semanticcloud.semanticEngine.entity.OntoProperty;

@AllArgsConstructor
@Getter
public class OwlObjectProperty implements OntoProperty {

	private String namespace;
	private String localName;

	public String getUri(){
		return String.format("%s%s", namespace, localName);
	}

}
