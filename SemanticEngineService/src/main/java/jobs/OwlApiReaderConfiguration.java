package jobs;

import org.semanticcloud.semanticEngine.controll.reading.owlApi.OwlApiReader;
import org.semanticcloud.semanticEngine.controll.reading.owlApi.OwlPropertyFactory;
import org.semanticcloud.semanticEngine.controll.reading.owlApi.propertyFactories.DateTimePropertyFactory;
import org.semanticcloud.semanticEngine.controll.reading.owlApi.propertyFactories.FloatPropertyFactory;
import org.semanticcloud.semanticEngine.controll.reading.owlApi.propertyFactories.IntegerPropertyFactory;
import org.semanticcloud.semanticEngine.controll.reading.owlApi.propertyFactories.ObjectPropertyFactory;
import org.semanticcloud.semanticEngine.controll.reading.owlApi.propertyFactories.StringPropertyFactory;

public class OwlApiReaderConfiguration {

    public void doJob() throws Exception {
        initialize("test/AiGConditionsOntology.owl", "./test");
        initialize("file:../../samples/GridSample/Ontology/AiGGridInstances.owl", "../../samples/GridSample/Ontology");
    }

    public void initialize(String uri, String localOntologyFolder) {
        OwlApiReader.initialize(uri, localOntologyFolder);

        OwlPropertyFactory.registerPropertyFactory(new IntegerPropertyFactory());
        OwlPropertyFactory.registerPropertyFactory(new FloatPropertyFactory());
        OwlPropertyFactory.registerPropertyFactory(new DateTimePropertyFactory());
        OwlPropertyFactory.registerPropertyFactory(new StringPropertyFactory());
        OwlPropertyFactory.registerPropertyFactory(new ObjectPropertyFactory());
    }
}
