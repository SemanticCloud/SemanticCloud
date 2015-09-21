package jobs;

import org.semanticcloud.semanticEngine.entity.models.owlGeneration.ClassRestrictionGenerator;
import org.semanticcloud.semanticEngine.entity.models.owlGeneration.IndividualGenerator;
import org.semanticcloud.semanticEngine.entity.models.owlGeneration.OntologyGenerator;
import org.semanticcloud.semanticEngine.entity.models.owlGeneration.RestrictionFactory;
import org.semanticcloud.semanticEngine.entity.models.owlGeneration.restrictionFactories.ClassValueRestrictionFactory;
import org.semanticcloud.semanticEngine.entity.models.owlGeneration.restrictionFactories.DatatypeRestrictionFactory;
import org.semanticcloud.semanticEngine.entity.models.owlGeneration.restrictionFactories.DatetimeRestrictionFactoryDecorator;
import org.semanticcloud.semanticEngine.entity.models.owlGeneration.restrictionFactories.EqualToRestrictionFactory;
import org.semanticcloud.semanticEngine.entity.models.owlGeneration.restrictionFactories.GreaterThanRestrictionFactory;
import org.semanticcloud.semanticEngine.entity.models.owlGeneration.restrictionFactories.IndividualValueRestrictionFactory;
import org.semanticcloud.semanticEngine.entity.models.owlGeneration.restrictionFactories.LessThanRestrictionFactory;
import org.semanticcloud.semanticEngine.entity.models.propertyConditions.ClassValueCondition;
import org.semanticcloud.semanticEngine.entity.models.propertyConditions.DatatypePropertyCondition;
import org.semanticcloud.semanticEngine.entity.models.propertyConditions.IndividualValueCondition;
import org.semanticcloud.semanticEngine.entity.properties.DateTimeProperty;
import org.semanticcloud.semanticEngine.entity.properties.FloatProperty;
import org.semanticcloud.semanticEngine.entity.properties.IntegerProperty;
import org.semanticcloud.semanticEngine.entity.properties.PropertyOperatorType;
import org.semanticcloud.semanticEngine.entity.properties.StringProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;

//@OnApplicationStart
public class PropertyTypeConfiguration {
    private OWLDataFactory factory;

    public void doJob() throws Exception {
        new OntologyGeneratorConfiguration().doJob();
        factory = OntologyGenerator.getOwlApiFactory();

        RestrictionFactory.registerRestrictionTypeFactory(DatatypePropertyCondition.class, createDatatypeRestrictionFactory());
        RestrictionFactory.registerRestrictionTypeFactory(IndividualValueCondition.class, new IndividualValueRestrictionFactory(factory));
        RestrictionFactory.registerRestrictionTypeFactory(ClassValueCondition.class,
                new ClassValueRestrictionFactory(getClassRestrictionGenerator(), getIndividualGenerator(), factory));
    }

    private IndividualGenerator getIndividualGenerator() {
        return OntologyGenerator.getGlobalInstance().getIndividualGenerator();
    }

    private ClassRestrictionGenerator getClassRestrictionGenerator() {
        return OntologyGenerator.getGlobalInstance().getClassRestrictionGenerator();
    }

    private DatatypeRestrictionFactory createDatatypeRestrictionFactory() throws Exception {

        DatatypeRestrictionFactory topLevelFactory = new DatatypeRestrictionFactory();
        topLevelFactory.registerOperatorRestrictionFactory(StringProperty.class, PropertyOperatorType.EQUAL_TO.toString(), new EqualToRestrictionFactory("http://www.w3.org/2001/XMLSchema#string", factory));
        topLevelFactory.registerOperatorRestrictionFactory(IntegerProperty.class, PropertyOperatorType.EQUAL_TO.toString(), new EqualToRestrictionFactory("http://www.w3.org/2001/XMLSchema#integer", factory));
        topLevelFactory.registerOperatorRestrictionFactory(IntegerProperty.class, PropertyOperatorType.LESS_THAN.toString(), new LessThanRestrictionFactory("http://www.w3.org/2001/XMLSchema#integer", factory));
        topLevelFactory.registerOperatorRestrictionFactory(IntegerProperty.class, PropertyOperatorType.GREATER_THAN.toString(), new GreaterThanRestrictionFactory("http://www.w3.org/2001/XMLSchema#integer", factory));
        topLevelFactory.registerOperatorRestrictionFactory(FloatProperty.class, PropertyOperatorType.EQUAL_TO.toString(), new EqualToRestrictionFactory("http://www.w3.org/2001/XMLSchema#float", factory));
        topLevelFactory.registerOperatorRestrictionFactory(FloatProperty.class, PropertyOperatorType.LESS_THAN.toString(), new LessThanRestrictionFactory("http://www.w3.org/2001/XMLSchema#float", factory));
        topLevelFactory.registerOperatorRestrictionFactory(FloatProperty.class, PropertyOperatorType.GREATER_THAN.toString(), new GreaterThanRestrictionFactory("http://www.w3.org/2001/XMLSchema#float", factory));
        topLevelFactory.registerOperatorRestrictionFactory(DateTimeProperty.class, PropertyOperatorType.EQUAL_TO.toString(),
                new DatetimeRestrictionFactoryDecorator(new EqualToRestrictionFactory("http://www.w3.org/2001/XMLSchema#datetime", factory)));
        topLevelFactory.registerOperatorRestrictionFactory(DateTimeProperty.class, PropertyOperatorType.LESS_THAN.toString(),
                new DatetimeRestrictionFactoryDecorator(new LessThanRestrictionFactory("http://www.w3.org/2001/XMLSchema#datetime", factory)));
        topLevelFactory.registerOperatorRestrictionFactory(DateTimeProperty.class, PropertyOperatorType.GREATER_THAN.toString(),
                new DatetimeRestrictionFactoryDecorator(new GreaterThanRestrictionFactory("http://www.w3.org/2001/XMLSchema#datetime", factory)));

        return topLevelFactory;
    }


}
