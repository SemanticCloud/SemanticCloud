package org.semanticcloud.semanticEngine.entity;

import lombok.Getter;

@Getter
public abstract class OwlElement {
    protected String localName;
    protected String namespace;

    public String getUri() {
        return String.format("%s%s", namespace, localName);
    }
}
