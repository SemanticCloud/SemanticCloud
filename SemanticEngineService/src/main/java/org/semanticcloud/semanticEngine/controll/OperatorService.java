package org.semanticcloud.semanticEngine.controll;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.semanticcloud.semanticEngine.model.ontology.OntoProperty;
import org.semanticcloud.semanticEngine.entity.models.PropertyOperator;
import org.semanticcloud.semanticEngine.model.ontology.properties.DateTimeProperty;
import org.semanticcloud.semanticEngine.model.ontology.properties.FloatProperty;
import org.semanticcloud.semanticEngine.model.ontology.properties.IntegerProperty;
import org.semanticcloud.semanticEngine.model.ontology.properties.OwlObjectProperty;
import org.semanticcloud.semanticEngine.model.ontology.properties.PropertyOperatorType;
import org.semanticcloud.semanticEngine.model.ontology.properties.StringProperty;
import org.springframework.stereotype.Service;

@Service
public class OperatorService {
    private HashMap<Class, List<PropertyOperator>> operatorsMap = new HashMap<>();

    public List<PropertyOperator> getOperators(OntoProperty property) {
        return operatorsMap.get(property.getClass());
    }

    private void registerPropertyOperstors(Class clazz, List<PropertyOperator> operators) {
        operatorsMap.put(clazz, operators);

    }

    @PostConstruct
    private void init() {
        registerPropertyOperstors(StringProperty.class, createStringPropertyRenderer());
        registerPropertyOperstors(IntegerProperty.class, createIntegerPropertyRenderer());
        registerPropertyOperstors(FloatProperty.class, createFloatPropertyRenderer());
        registerPropertyOperstors(DateTimeProperty.class, createDateTimePropertyRenderer());
        registerPropertyOperstors(OwlObjectProperty.class, createObjectPropertyRenderer());
    }

    private List<PropertyOperator> createStringPropertyRenderer() {
        List<PropertyOperator> propertyOperators = new LinkedList<>();
        propertyOperators.add(new PropertyOperator(PropertyOperatorType.EQUAL_TO.toString(), "is equal to ", true));
        return propertyOperators;
    }

    private List<PropertyOperator> createIntegerPropertyRenderer() {
        List<PropertyOperator> propertyOperators = new LinkedList<>();
        propertyOperators.add(new PropertyOperator(PropertyOperatorType.EQUAL_TO.toString(), "is equal to ", true));
        propertyOperators.add(new PropertyOperator(PropertyOperatorType.GREATER_THAN.toString(), "is greater than "));
        propertyOperators.add(new PropertyOperator(PropertyOperatorType.LESS_THAN.toString(), "is less than "));
        return propertyOperators;
    }

    private List<PropertyOperator> createFloatPropertyRenderer() {
        List<PropertyOperator> propertyOperators = new LinkedList<>();
        propertyOperators.add(new PropertyOperator(PropertyOperatorType.EQUAL_TO.toString(), "is equal to ", true));
        propertyOperators.add(new PropertyOperator(PropertyOperatorType.GREATER_THAN.toString(), "is greater than "));
        propertyOperators.add(new PropertyOperator(PropertyOperatorType.LESS_THAN.toString(), "is less than "));
        return propertyOperators;
    }

    private List<PropertyOperator> createDateTimePropertyRenderer() {
        List<PropertyOperator> propertyOperators = new LinkedList<>();
        propertyOperators.add(new PropertyOperator(PropertyOperatorType.EQUAL_TO.toString(), "is equal to "));
        propertyOperators.add(new PropertyOperator(PropertyOperatorType.GREATER_THAN.toString(), "is greater than "));
        propertyOperators.add(new PropertyOperator(PropertyOperatorType.LESS_THAN.toString(), "is less than "));
        return propertyOperators;
    }

    private List<PropertyOperator> createObjectPropertyRenderer() {
        List<PropertyOperator> propertyOperators = new LinkedList<>();
        propertyOperators.add(new PropertyOperator(PropertyOperatorType.EQUAL_TO_INDIVIDUAL.toString(), "is equal to individual "));
        propertyOperators.add(new PropertyOperator(PropertyOperatorType.DESCRIBED_WITH.toString(), "is described with ", true));
        propertyOperators.add(new PropertyOperator(PropertyOperatorType.CONSTRAINED_BY.toString(), "is constrained by"));
        return propertyOperators;
    }

}
