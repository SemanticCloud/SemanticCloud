package org.semanticcloud.semanticEngine.model.ontology;


import lombok.Getter;

@Getter
public abstract class OntoProperty extends OwlElement {

    //private String datatype;

    private boolean functional;

    public OntoProperty(String namespace, String localName, boolean functional) {
        super(namespace, localName);
    }
}
