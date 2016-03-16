package org.semanticcloud.semanticEngine.model.ontology;


import org.semanticcloud.semanticEngine.model.ontology.OwlElement;

public abstract class OntoProperty extends OwlElement {

    private String datatype;

    public String getDatatype() {
        return datatype;
    }
}
