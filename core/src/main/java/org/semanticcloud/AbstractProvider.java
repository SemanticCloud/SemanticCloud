package org.semanticcloud;

import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.rdf.model.ModelFactory;
import org.mindswap.pellet.jena.PelletReasonerFactory;


public abstract class AbstractProvider implements Provider  {
    protected final static String NS = "http://semantic-cloud.org/Cloud#";
    protected final String namespace;

    protected AbstractProvider(String namespace) {
        this.namespace = namespace;
    }

    protected OntModel createBaseModel(){
        OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
        model.setDynamicImports(true);
        OntDocumentManager dm = model.getDocumentManager();
        dm.addAltEntry("http://semantic-cloud.org/Cloud", "file:/opt/SemanticCloud/cloud.owl");

        Ontology ontology = model.createOntology(namespace.replace("#", ""));
        ontology.addImport(model.createResource("http://semantic-cloud.org/Cloud"));
        model.setNsPrefix("cloud", "http://semantic-cloud.org/Cloud#");
        return model;
    }
}
