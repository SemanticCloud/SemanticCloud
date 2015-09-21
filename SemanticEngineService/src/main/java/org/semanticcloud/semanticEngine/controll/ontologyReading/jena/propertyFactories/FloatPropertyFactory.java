package org.semanticcloud.semanticEngine.controll.ontologyReading.jena.propertyFactories;

import org.semanticcloud.semanticEngine.entity.OntoProperty;
import org.semanticcloud.semanticEngine.controll.ontologyReading.jena.OwlPropertyFactory;
import org.semanticcloud.semanticEngine.entity.properties.FloatProperty;
import org.apache.jena.ontology.OntProperty;

public class FloatPropertyFactory extends OwlPropertyFactory {

    @Override
    public boolean canCreateProperty(OntProperty ontProperty) {
        if(!ontProperty.isDatatypeProperty())
            return false;
        if(ontProperty.getRange() == null)
            return false;
        if(ontProperty.getRange().getURI() == null)
            return false;

        String rangeUri = ontProperty.getRange().getURI();
        return rangeUri.equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#float") ||
                rangeUri.equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#decimal");
    }

    @Override
    public OntoProperty createProperty(OntProperty ontProperty) {
        return new FloatProperty(ontProperty.getNameSpace(), ontProperty.getLocalName(), ontProperty.getRange().getURI());
    }

}
