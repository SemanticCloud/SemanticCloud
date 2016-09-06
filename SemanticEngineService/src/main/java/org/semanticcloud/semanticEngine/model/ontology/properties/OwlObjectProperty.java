package org.semanticcloud.semanticEngine.model.ontology.properties;

import lombok.Getter;
import org.semanticcloud.semanticEngine.model.ontology.OntoProperty;

@Getter
public class OwlObjectProperty extends OntoProperty {
    public OwlObjectProperty(String namespace, String localName, boolean functional) {
        super(namespace, localName, functional);
    }
}
