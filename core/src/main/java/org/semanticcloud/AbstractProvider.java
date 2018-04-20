package org.semanticcloud;


import openllet.jena.PelletReasonerFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.rdf.model.ModelFactory;

import static openllet.jena.PelletReasonerFactory.THE_SPEC;


public abstract class AbstractProvider implements Provider  {
    protected final static String OFFER_CLASS = Cloud.NS +"Condition";
    protected final String namespace;

    protected AbstractProvider(String namespace) {
        this.namespace = namespace;
    }

    protected OntModel createBaseModel(){
        OntModelSpec ontModelSpec = new OntModelSpec(OntModelSpec.OWL2_MEM);
        ontModelSpec.setReasonerFactory(PelletReasonerFactory.theInstance());
        OntModel model = ModelFactory.createOntologyModel(ontModelSpec);
        model.setDynamicImports(true);

        Ontology ontology = model.createOntology(namespace.replace("#", ""));
        ontology.addImport(model.createResource(Cloud.NS.replace("#", "")));
        model.setNsPrefix("cloud", Cloud.NS);
        return model;
    }
}
