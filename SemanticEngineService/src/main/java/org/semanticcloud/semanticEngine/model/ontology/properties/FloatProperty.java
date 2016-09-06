package org.semanticcloud.semanticEngine.model.ontology.properties;

import org.apache.jena.datatypes.xsd.XSDDatatype;

public class FloatProperty extends OwlDatatypeProperty {

    public FloatProperty(String namespace, String localName, boolean functional) {
        super(namespace, localName, XSDDatatype.XSDfloat.getURI(), functional);
    }

    public FloatProperty(String namespace, String localName, String datatypeUri, boolean functional) {
        super(namespace, localName, datatypeUri, functional);
    }
}
