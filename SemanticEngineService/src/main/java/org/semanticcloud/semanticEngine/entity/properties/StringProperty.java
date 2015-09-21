package org.semanticcloud.semanticEngine.entity.properties;

import org.apache.jena.datatypes.xsd.XSDDatatype;

public class StringProperty extends OwlDatatypeProperty{

    public StringProperty(String namespace, String localName) {
        this(namespace, localName, XSDDatatype.XSDstring.getURI());
    }


    public StringProperty(String namespace, String localName, String datatype) {
        super(namespace, localName, datatype);

    }


}
