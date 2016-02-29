package org.semanticcloud.semanticEngine.model.ontology.properties;

import org.apache.jena.datatypes.xsd.XSDDatatype;

public class DateTimeProperty extends OwlDatatypeProperty {

    public DateTimeProperty(String namespace, String localName) {
        super(namespace, localName, XSDDatatype.XSDdateTime.getURI());
    }
}
