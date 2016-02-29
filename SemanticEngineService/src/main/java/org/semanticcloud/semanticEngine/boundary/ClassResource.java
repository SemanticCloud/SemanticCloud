package org.semanticcloud.semanticEngine.boundary;

import java.util.List;

import org.semanticcloud.semanticEngine.controll.OntologyService;
import org.semanticcloud.semanticEngine.controll.OperatorService;
import org.semanticcloud.semanticEngine.model.ontology.OwlClass;
import org.semanticcloud.semanticEngine.model.ontology.OntoProperty;
import org.semanticcloud.semanticEngine.model.conditions.ClassCondition;
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
    public OwlClass getClassInfo(@PathVariable String classUri) {
        OwlClass owlClass = ontologyService.getOwlClass(classUri);
        return owlClass;
    }

    @RequestMapping(value = "{classUri}/subclasses", method = RequestMethod.GET)
    public void getSubclasses(@PathVariable String classUri) {
        ontologyService.getOwlSubclasses(classUri);
    }

    @RequestMapping(value = "{classUri}/properties/{propertyUri}/operators", method = RequestMethod.GET)
    public List<PropertyOperator> getPropertyOperators(@PathVariable String classUri, @PathVariable String propertyUri) {
        OwlClass owlClass = ontologyService.getOwlClass(classUri);
        OntoProperty property = null;
        try {
            property = ontologyService.getProperty(propertyUri);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return operatorService.getOperators(property);
    }

    @RequestMapping(value = "{classUri}/properties/{propertyUri}/restrictions", method = RequestMethod.GET)
    public void getPropertyRestrictions(@PathVariable String classUri, @PathVariable String propertyUri) {

    }


    @RequestMapping(value = "conditions", method = RequestMethod.POST)
    public void updateConditions(List<ClassCondition> classConditions) {

    }
    @RequestMapping(value = "condition", method = RequestMethod.POST)
    public void updateCondition(ClassCondition classCondition) {

    }
}
