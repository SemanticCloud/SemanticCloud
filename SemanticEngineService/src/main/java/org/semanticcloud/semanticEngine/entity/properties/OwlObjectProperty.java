package org.semanticcloud.semanticEngine.entity.properties;

import lombok.Getter;
import org.semanticcloud.semanticEngine.entity.OntoProperty;

@Getter
public class OwlObjectProperty extends OntoProperty {

    public OwlObjectProperty(String namespace, String localName) {
        this.namespace = namespace;
        this.localName = localName;
    }
}
