package org.semanticcloud.semanticEngine.boundary;

import org.semanticcloud.semanticEngine.controll.OntologyService;
import org.semanticcloud.semanticEngine.entity.models.ConfigurationException;
import org.semanticcloud.semanticEngine.controll.owlGeneration.OntologyGenerator;
import org.semanticcloud.semanticEngine.model.conditions.ClassCondition;
import org.semanticcloud.semanticEngine.model.conditions.ClassPropertyCondition;
import org.semanticcloud.semanticEngine.model.conditions.PropertyCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("condition")
public class ConditionResource {
    @Autowired
    private OntologyService ontologyService;

    @RequestMapping(method = RequestMethod.POST)
    public String updateConditions(@RequestBody List<ClassCondition> classConditions) {
        for (ClassCondition classCondition : classConditions) {
            fill(classCondition);

        }
        OntologyGenerator globalInstance = OntologyGenerator.getGlobalInstance();
        String test = globalInstance.convertToOwlClass2("test", classConditions.get(0));
        return test;
    }

    private void fillConditionProperty(PropertyCondition condition) {
        try {
            condition.setProperty(ontologyService.getProperty(condition.getPropertyUri()));
        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    private void fill(ClassCondition classCondition){
            for (PropertyCondition propertyCondition : classCondition.getPropertyConditions()) {
                fillConditionProperty(propertyCondition);
                if(propertyCondition instanceof ClassPropertyCondition){
                    ClassCondition classConstraintValue = ((ClassPropertyCondition) propertyCondition).getClassConstraintValue();
                    fill(classConstraintValue);

                }
            }
    }
}
