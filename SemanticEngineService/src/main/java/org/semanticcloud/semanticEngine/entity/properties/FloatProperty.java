package org.semanticcloud.semanticEngine.entity.properties;

import org.apache.jena.datatypes.xsd.XSDDatatype;

public class FloatProperty extends OwlDatatypeProperty {

    public FloatProperty(String namespace, String localName) {
        super(namespace, localName, XSDDatatype.XSDfloat.getURI());
    }

    public FloatProperty(String namespace, String localName, String datatypeUri) {
        super(namespace, localName, datatypeUri);
    }
}
