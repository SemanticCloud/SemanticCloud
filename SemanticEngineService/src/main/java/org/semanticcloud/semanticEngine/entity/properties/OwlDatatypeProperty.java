package org.semanticcloud.semanticEngine.entity.properties;

import lombok.Getter;
import org.semanticcloud.semanticEngine.entity.OntoProperty;

@Getter
public class OwlDatatypeProperty extends OntoProperty {
    private String datatype;

    public OwlDatatypeProperty(String namespace, String localName, String datatype) {
        this.namespace = namespace;
        this.localName = localName;
        this.datatype = datatype;
    }

}
