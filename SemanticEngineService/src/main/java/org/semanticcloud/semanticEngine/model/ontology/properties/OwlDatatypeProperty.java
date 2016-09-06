package org.semanticcloud.semanticEngine.model.ontology.properties;

import lombok.Getter;
import org.semanticcloud.semanticEngine.model.ontology.OntoProperty;

@Getter
public class OwlDatatypeProperty extends OntoProperty {
    private String datatype;

    public OwlDatatypeProperty(String namespace, String localName, String datatype, boolean functional) {
        super(namespace, localName, functional);
        this.datatype = datatype;
    }

}
