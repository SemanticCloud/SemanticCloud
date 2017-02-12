package org.semanticcloud.semanticEngine.controll;

import java.util.List;

import org.semanticcloud.semanticEngine.controll.reading.OntologyReader;
import org.semanticcloud.semanticEngine.model.ontology.OwlClass;
import org.semanticcloud.semanticEngine.model.ontology.OntoProperty;
import org.semanticcloud.semanticEngine.model.ontology.OwlIndividual;
import org.semanticcloud.semanticEngine.model.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OntologyService {
    @Autowired
    private OntologyReader ontologyReader;


    public OwlClass getOwlClass(String classUri) {
        return ontologyReader.getOwlClass(classUri);
    }

    public OntoProperty getProperty(String propertyUri) throws ConfigurationException {
        return ontologyReader.getProperty(propertyUri);
    }

    public OntoProperty getProperty(String classUri, String propertyUri) {
        OwlClass owlClass = ontologyReader.getOwlClass(classUri);
        return owlClass.findProperty(propertyUri);
    }

    public List<OwlClass> getOwlSubclasses(String classUri) {
        return ontologyReader.getOwlSubclasses(classUri);
    }

    public List<OwlIndividual> getIndividualsInRange(String classUri, String propertyUri) {
        try {
            OwlClass owlClass = ontologyReader.getOwlClass(classUri);
            OntoProperty property;
            property = ontologyReader.getProperty(propertyUri);
            return ontologyReader.getIndividualsInRange(owlClass, property);
        } catch (ConfigurationException e) {
            e.printStackTrace();
            return null;
        }

    }

    public List<OwlClass> getClassesInRange(String classUri, String propertyUri) {
        try {
            OwlClass owlClass = ontologyReader.getOwlClass(classUri);
            OntoProperty property;
            property = ontologyReader.getProperty(propertyUri);
            return ontologyReader.getClassesInRange(owlClass, property);
        } catch (ConfigurationException e) {
            e.printStackTrace();
            return null;
        }

    }
}
