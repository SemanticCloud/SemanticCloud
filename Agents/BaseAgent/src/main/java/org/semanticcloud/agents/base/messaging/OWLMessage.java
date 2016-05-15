package org.semanticcloud.agents.base.messaging;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyAlreadyExistsException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class OWLMessage extends ACLMessage {
    public OWLMessage(int performative) {
        super(performative);
    }

    public void setContentOntology(OWLOntology ontology) throws OWLOntologyStorageException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ontology.getOWLOntologyManager().saveOntology(ontology,stream);
        setContent(stream.toString());
    }
    public OWLOntology getContentOntology(OWLOntologyManager ontologyManager) throws OWLOntologyCreationException {
        return getContentOntology(ontologyManager,null);
    }
    public OWLOntology getContentOntology(OWLOntologyManager ontologyManager, Agent agent) throws OWLOntologyCreationException{
        String content = this.getContent();
        ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes());
        OWLOntology ontology;
        try{
            ontology = ontologyManager.loadOntologyFromOntologyDocument(stream);
        } catch (OWLOntologyAlreadyExistsException e){
            ontology = ontologyManager.getOntology(e.getOntologyID());
        }
        return ontology;
    }
}
