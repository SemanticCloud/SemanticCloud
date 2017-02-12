package org.semanticcloud.semanticEngine.boundary;

import java.util.List;

import org.semanticcloud.semanticEngine.controll.OntologyService;
import org.semanticcloud.semanticEngine.controll.OperatorService;
import org.semanticcloud.semanticEngine.model.ontology.OwlClass;
import org.semanticcloud.semanticEngine.model.ontology.OntoProperty;
import org.semanticcloud.semanticEngine.model.ConfigurationException;
import org.semanticcloud.semanticEngine.model.PropertyOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("class/{classUri}")
public class ClassResource {

    @Autowired
    private OntologyService ontologyService;
    @Autowired
    private OperatorService operatorService;

    @RequestMapping(method = RequestMethod.GET)
    public OwlClass getClassInfo(@PathVariable String classUri) {
        OwlClass owlClass = ontologyService.getOwlClass(classUri);
        return owlClass;
    }

    @RequestMapping(value = "property",method = RequestMethod.GET)
    public List<OntoProperty> getClassProperties(@PathVariable String classUri) {
        OwlClass owlClass = ontologyService.getOwlClass(classUri);
        return owlClass.getProperties();
    }

    @RequestMapping(value = "subclass", method = RequestMethod.GET)
    public List<OwlClass> getSubclasses(@PathVariable String classUri) {
        return ontologyService.getOwlSubclasses(classUri);
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
}
