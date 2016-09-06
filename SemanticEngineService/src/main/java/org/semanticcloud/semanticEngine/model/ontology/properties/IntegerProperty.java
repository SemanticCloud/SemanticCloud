package org.semanticcloud.semanticEngine.model.ontology.properties;

import org.apache.jena.datatypes.xsd.XSDDatatype;

public class IntegerProperty extends OwlDatatypeProperty {

    public IntegerProperty(String namespace, String localName, String datatype, boolean functional) {
        super(namespace, localName, datatype, functional);
    }

    public IntegerProperty(String namespace, String localName, boolean functional) {
        this(namespace, localName, XSDDatatype.XSDinteger.getURI(), functional);
    }
}
