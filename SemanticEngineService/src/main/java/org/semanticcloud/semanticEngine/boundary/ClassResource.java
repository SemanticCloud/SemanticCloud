package org.semanticcloud.semanticEngine.boundary;

import java.util.List;

import org.semanticcloud.semanticEngine.controll.OntologyService;
import org.semanticcloud.semanticEngine.controll.OperatorService;
import org.semanticcloud.semanticEngine.entity.OntoClass;
import org.semanticcloud.semanticEngine.entity.OntoProperty;
import org.semanticcloud.semanticEngine.entity.OwlIndividual;
import org.semanticcloud.semanticEngine.entity.models.ConfigurationException;
import org.semanticcloud.semanticEngine.entity.models.PropertyOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("class")
public class ClassResource {

    @Autowired
    private OntologyService ontologyService;
    @Autowired
    private OperatorService operatorService;

    @RequestMapping(value = "{classUri}", method = RequestMethod.GET)
    public OntoClass getClassInfo(@PathVariable("classUri") String classUri) {
        OntoClass owlClass = ontologyService.getOwlClass(classUri);
        return owlClass;
    }

    @RequestMapping(value = "{classUri}/subclasses", method = RequestMethod.GET)
    public void getSubclasses(@PathVariable("classUri") String classUri) {
        ontologyService.getOwlSubclasses(classUri);
    }

    @RequestMapping(value = "{classUri}/property/{propertyUri}", method = RequestMethod.GET)
    public OntoProperty getPropertyCondition(@PathVariable("classUri") String classUri, @PathVariable("propertyUri") String propertyUri) {
        OntoClass owlClass = ontologyService.getOwlClass(classUri);
        OntoProperty property = null;
        try {
            property = ontologyService.getProperty(propertyUri);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return property;
    }

    @RequestMapping(value = "{classUri}/property/{propertyUri}/operators", method = RequestMethod.GET)
    public List<PropertyOperator> getPropertyOperators(@PathVariable("classUri") String classUri, @PathVariable("propertyUri") String propertyUri) {
        OntoClass owlClass = ontologyService.getOwlClass(classUri);
        OntoProperty property = null;
        try {
            property = ontologyService.getProperty(propertyUri);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return operatorService.getOperators(property);
    }

    @RequestMapping(value = "{classUri}/property/{propertyUri}/individuals", method = RequestMethod.GET)
    public List<OwlIndividual> getIndividualsInRange(@PathVariable("classUri") String classUri, @PathVariable("propertyUri") String propertyUri) {
        return ontologyService.getIndividualsInRange(classUri, propertyUri);
    }

    @RequestMapping(value = "{classUri}/property/{propertyUri}/classes", method = RequestMethod.GET)
    public List<OntoClass> getClassesInRange(@PathVariable("classUri") String classUri, @PathVariable("propertyUri") String propertyUri) {
        return ontologyService.getClassesInRange(classUri, propertyUri);
    }
}
