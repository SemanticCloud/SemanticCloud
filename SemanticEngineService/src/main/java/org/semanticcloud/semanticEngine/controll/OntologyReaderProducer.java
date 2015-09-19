package org.semanticcloud.semanticEngine.controll;

import java.util.Map;

import jobs.PropertyTypeConfiguration;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.semanticcloud.semanticEngine.controll.ontologyReading.OntologyReader;
import org.semanticcloud.semanticEngine.controll.ontologyReading.jena.JenaOwlReader;
import org.semanticcloud.semanticEngine.controll.ontologyReading.jena.JenaOwlReaderConfig;
import org.semanticcloud.semanticEngine.controll.ontologyReading.jena.OwlPropertyFactory;
import org.semanticcloud.semanticEngine.controll.ontologyReading.jena.propertyFactories.DateTimePropertyFactory;
import org.semanticcloud.semanticEngine.controll.ontologyReading.jena.propertyFactories.FloatPropertyFactory;
import org.semanticcloud.semanticEngine.controll.ontologyReading.jena.propertyFactories.IntegerPropertyFactory;
import org.semanticcloud.semanticEngine.controll.ontologyReading.jena.propertyFactories.ObjectPropertyFactory;
import org.semanticcloud.semanticEngine.controll.ontologyReading.jena.propertyFactories.StringPropertyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class OntologyReaderProducer {

    @Bean
    public OntologyReader produceReader() {
        String uri = "file:/opt/Ontology/CloudOntology.owl";
        JenaOwlReaderConfig jenaOwlReaderConfig = new JenaOwlReaderConfig()
                .useLocalMapping("http://semantic-cloud.org/Cloud", "file:/opt/Ontology/CloudOntology.owl")
                .ignorePropertiesWithUnspecifiedDomain();

        return initialize(uri, jenaOwlReaderConfig);
    }

    public JenaOwlReader initialize(String uri, JenaOwlReaderConfig config) {
        JenaOwlReader jenaOwlReader = loadFromFile(uri, config);
        OntologyReader.setGlobalInstance(jenaOwlReader);

        OwlPropertyFactory.registerPropertyFactory(new IntegerPropertyFactory());
        OwlPropertyFactory.registerPropertyFactory(new FloatPropertyFactory());
        OwlPropertyFactory.registerPropertyFactory(new DateTimePropertyFactory());
        OwlPropertyFactory.registerPropertyFactory(new StringPropertyFactory());
        OwlPropertyFactory.registerPropertyFactory(new ObjectPropertyFactory());
        try {
            new PropertyTypeConfiguration().doJob();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jenaOwlReader;


    }

    public JenaOwlReader loadFromFile(String uri, JenaOwlReaderConfig config) {
        //todo resoner
        OntModel model = ModelFactory.createOntologyModel();
        //OntModel model = ModelFactory.createOntologyModel(String.valueOf(PelletReasonerFactory.THE_SPEC));
        if (config != null) {
            OntDocumentManager dm = model.getDocumentManager();
            for (Map.Entry<String, String> mapping : config.getLocalMappings().entrySet()) {
                dm.addAltEntry(mapping.getKey(), mapping.getValue());
            }
        }

        model.read(uri);

        return new JenaOwlReader(model, config);
    }
}
