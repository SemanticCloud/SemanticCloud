package org.semanticcloud.semanticEngine.controll;

import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.semanticcloud.semanticEngine.controll.reading.OntologyReader;
import org.semanticcloud.semanticEngine.controll.reading.jena.JenaOwlReader;
import org.semanticcloud.semanticEngine.controll.reading.jena.JenaConfig;
import org.semanticcloud.semanticEngine.controll.reading.jena.OwlPropertyFactory;
import org.semanticcloud.semanticEngine.controll.reading.jena.propertyFactories.DateTimePropertyFactory;
import org.semanticcloud.semanticEngine.controll.reading.jena.propertyFactories.FloatPropertyFactory;
import org.semanticcloud.semanticEngine.controll.reading.jena.propertyFactories.IntegerPropertyFactory;
import org.semanticcloud.semanticEngine.controll.reading.jena.propertyFactories.ObjectPropertyFactory;
import org.semanticcloud.semanticEngine.controll.reading.jena.propertyFactories.StringPropertyFactory;
import org.semanticcloud.semanticEngine.controll.owlGeneration.ClassRestrictionGenerator;
import org.semanticcloud.semanticEngine.controll.owlGeneration.IndividualGenerator;
import org.semanticcloud.semanticEngine.controll.owlGeneration.OntologyGenerator;
import org.semanticcloud.semanticEngine.controll.owlGeneration.RestrictionFactory;
import org.semanticcloud.semanticEngine.controll.owlGeneration.restrictionFactories.ClassValueRestrictionFactory;
import org.semanticcloud.semanticEngine.controll.owlGeneration.restrictionFactories.DatatypeRestrictionFactory;
import org.semanticcloud.semanticEngine.controll.owlGeneration.restrictionFactories.DatetimeRestrictionFactoryDecorator;
import org.semanticcloud.semanticEngine.controll.owlGeneration.restrictionFactories.EqualToRestrictionFactory;
import org.semanticcloud.semanticEngine.controll.owlGeneration.restrictionFactories.GreaterThanRestrictionFactory;
import org.semanticcloud.semanticEngine.controll.owlGeneration.restrictionFactories.IndividualValueRestrictionFactory;
import org.semanticcloud.semanticEngine.controll.owlGeneration.restrictionFactories.LessThanRestrictionFactory;
import org.semanticcloud.semanticEngine.model.ontology.properties.DateTimeProperty;
import org.semanticcloud.semanticEngine.model.ontology.properties.FloatProperty;
import org.semanticcloud.semanticEngine.model.ontology.properties.IntegerProperty;
import org.semanticcloud.semanticEngine.model.ontology.properties.PropertyOperatorType;
import org.semanticcloud.semanticEngine.model.ontology.properties.StringProperty;
import org.semanticcloud.semanticEngine.model.conditions.ClassPropertyCondition;
import org.semanticcloud.semanticEngine.model.conditions.DatatypePropertyCondition;
import org.semanticcloud.semanticEngine.model.conditions.IndividualCondition;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OntologyReaderProducer {
    @Autowired
    private JenaConfig jenaConfig;

    @Bean
    public OntologyReader produceReader() {
        String uri = "file:/home/malagus/workspace/master-thesis/Ontologia/cloud.owl";
        OntologyGenerator.initialize(uri,"file:/home/malagus/workspace/master-thesis/Ontologia/");

        return initialize(jenaConfig);
    }

    public JenaOwlReader initialize(JenaConfig config) {
        JenaOwlReader jenaOwlReader = loadFromFile(config);

        OwlPropertyFactory.registerPropertyFactory(new IntegerPropertyFactory());
        OwlPropertyFactory.registerPropertyFactory(new FloatPropertyFactory());
        OwlPropertyFactory.registerPropertyFactory(new DateTimePropertyFactory());
        OwlPropertyFactory.registerPropertyFactory(new StringPropertyFactory());
        OwlPropertyFactory.registerPropertyFactory(new ObjectPropertyFactory());
        try {
            doJob();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jenaOwlReader;


    }

    public JenaOwlReader loadFromFile(JenaConfig config) {
        //todo resoner
        OntModel model = ModelFactory.createOntologyModel();
        //OntModel model = ModelFactory.createOntologyModel(String.valueOf(PelletReasonerFactory.THE_SPEC));
        if (config != null) {
            OntDocumentManager dm = model.getDocumentManager();
            for (Map.Entry<String, String> mapping : config.getLocalMappings().entrySet()) {
                dm.addAltEntry(mapping.getKey(), mapping.getValue());
            }
        }

        model.read(config.getBaseOntology());

        return new JenaOwlReader(model, config.isIgnorePropsWithNoDomain());
    }

    private OWLDataFactory factory;

    public void doJob() throws Exception {
        factory = OntologyGenerator.getOwlApiFactory();

        RestrictionFactory.registerRestrictionTypeFactory(DatatypePropertyCondition.class, createDatatypeRestrictionFactory());
        RestrictionFactory.registerRestrictionTypeFactory(IndividualCondition.class, new IndividualValueRestrictionFactory(factory));
        RestrictionFactory.registerRestrictionTypeFactory(ClassPropertyCondition.class,
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
