package org.semanticcloud;


import openllet.jena.PelletReasonerFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.rdf.model.ModelFactory;


public abstract class AbstractProvider implements Provider  {
    protected final static String OFFER_CLASS = Cloud.NS +"Condition";
    protected final String namespace;

    protected AbstractProvider(String namespace) {
        this.namespace = namespace;
    }

    protected OntModel createBaseModel(){
        OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
        model.setDynamicImports(true);

        Ontology ontology = model.createOntology(namespace.replace("#", ""));
        ontology.addImport(model.createResource(Cloud.NS.replace("#", "")));
        model.setNsPrefix("cloud", Cloud.NS);
        return model;
    }
}
