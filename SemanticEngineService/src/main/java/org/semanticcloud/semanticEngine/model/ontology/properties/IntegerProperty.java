package org.semanticcloud.semanticEngine.model.ontology.properties;

import org.apache.jena.datatypes.xsd.XSDDatatype;

public class IntegerProperty extends OwlDatatypeProperty {

    public IntegerProperty(String namespace, String localName, String datatype) {
        super(namespace, localName, datatype);
    }

    public IntegerProperty(String namespace, String localName) {
        this(namespace, localName, XSDDatatype.XSDinteger.getURI());
    }
}
