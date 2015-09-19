package org.semanticcloud.semanticEngine.controll;

import java.util.List;

import org.semanticcloud.semanticEngine.controll.ontologyReading.OntologyReader;
import org.semanticcloud.semanticEngine.entity.OntoClass;
import org.semanticcloud.semanticEngine.entity.OntoProperty;
import org.semanticcloud.semanticEngine.entity.OwlIndividual;
import org.semanticcloud.semanticEngine.entity.models.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OntologyService {
    @Autowired
    private OntologyReader ontologyReader;


    public OntoClass getOwlClass(String classUri) {
        return ontologyReader.getOwlClass(classUri);
    }

    public OntoProperty getProperty(String propertyUri) throws ConfigurationException {
        return ontologyReader.getProperty(propertyUri);
    }

    public void getOwlSubclasses(String classUri) {
        ontologyReader.getOwlSubclasses(classUri);
    }

    public List<OwlIndividual> getIndividualsInRange(String classUri, String propertyUri) {
        try {
            OntoClass owlClass = ontologyReader.getOwlClass(classUri);
            OntoProperty property;
            property = ontologyReader.getProperty(propertyUri);
            return ontologyReader.getIndividualsInRange(owlClass, property);
        } catch (ConfigurationException e) {
            e.printStackTrace();
            return null;
        }

    }

    public List<OntoClass> getClassesInRange(String classUri, String propertyUri) {
        try {
            OntoClass owlClass = ontologyReader.getOwlClass(classUri);
            OntoProperty property;
            property = ontologyReader.getProperty(propertyUri);
            return ontologyReader.getClassesInRange(owlClass, property);
        } catch (ConfigurationException e) {
            e.printStackTrace();
            return null;
        }

    }
}
