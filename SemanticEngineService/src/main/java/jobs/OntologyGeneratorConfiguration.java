package jobs;

import org.semanticcloud.semanticEngine.entity.models.owlGeneration.OntologyGenerator;

public class OntologyGeneratorConfiguration {

    public void doJob() throws Exception {
        OntologyGenerator.initialize("file:test/AiGConditionsOntology.owl", "./test");
        OntologyGenerator.initialize("file:../../samples/GridSample/Ontology/AiGGridInstances.owl", "../../samples/GridSample/Ontology");
    }
}
