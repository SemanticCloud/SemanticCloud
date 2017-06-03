package org.semanticcloud.semanticEngine.boundary;

import org.semanticcloud.semanticEngine.controll.OntologyService;
import org.semanticcloud.semanticEngine.model.ontology.OntoProperty;
import org.semanticcloud.semanticEngine.model.ontology.OwlClass;
import org.semanticcloud.semanticEngine.model.ontology.OwlIndividual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("class/{classUri}/property/{propertyUri}")
public class PropertyResource {
    @Autowired
    private OntologyService ontologyService;

    @RequestMapping(method = RequestMethod.GET)
    public OntoProperty getProperty(@PathVariable String classUri, @PathVariable String propertyUri) {
        return ontologyService.getProperty(classUri,propertyUri);
    }

    @RequestMapping(value = "classes", method = RequestMethod.GET)
    public List<OwlClass> getClassesInRange(@PathVariable String classUri, @PathVariable String propertyUri){
        return ontologyService.getClassesInRange(classUri,propertyUri);

    }

    @RequestMapping(value = "individuals", method = RequestMethod.GET)
    public List<OwlIndividual> getIndividualsInRange(@PathVariable String classUri, @PathVariable String propertyUri){
        return ontologyService.getIndividualsInRange(classUri,propertyUri);
    }
}
