package org.semanticcloud.semanticEngine.model.ontology;

import lombok.Getter;

@Getter
public abstract class OwlElement {
    protected String localName;
    protected String namespace;

    public OwlElement(String namespace, String localName) {
        this.namespace = namespace;
        this.localName = localName;
    }

    public String getUri() {
        return String.format("%s%s", namespace, localName);
    }


}
