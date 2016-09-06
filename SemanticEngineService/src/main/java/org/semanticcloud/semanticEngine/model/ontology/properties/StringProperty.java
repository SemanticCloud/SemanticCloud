package org.semanticcloud.semanticEngine.model.ontology.properties;

import org.apache.jena.datatypes.xsd.XSDDatatype;

public class StringProperty extends OwlDatatypeProperty{

    public StringProperty(String namespace, String localName, boolean functional) {
        this(namespace, localName, XSDDatatype.XSDstring.getURI(), functional);
    }


    public StringProperty(String namespace, String localName, String datatype, boolean functional) {
        super(namespace, localName, datatype, functional);

    }


}
