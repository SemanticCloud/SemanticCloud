package org.semanticcloud.agents.base.messaging;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Iterator;
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
        ontology.getOWLOntologyManager().saveOntology(ontology, stream);
        setContent(stream.toString());
    }

    public OWLOntology getContentOntology(OWLOntologyManager ontologyManager) throws OWLOntologyCreationException {
        return getContentOntology(ontologyManager, null);
    }

    public OWLOntology getContentOntology(OWLOntologyManager ontologyManager, Agent agent) throws OWLOntologyCreationException {
        String content = this.getContent();
        ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes());
        OWLOntology ontology;
        try {
            ontology = ontologyManager.loadOntologyFromOntologyDocument(stream);
        } catch (OWLOntologyAlreadyExistsException e) {
            ontology = ontologyManager.getOntology(e.getOntologyID());
        }
        return ontology;
    }

    public static OWLMessage wrap(ACLMessage msg) {
        OWLMessage wrapper = null;
        if (msg != null) {
            if (msg instanceof OWLMessage) {
                wrapper = (OWLMessage) msg;
            } else {
                wrapper = new OWLMessage(msg.getPerformative());
                wrapper.setSender(msg.getSender());
                Iterator it = msg.getAllReceiver();

                while (it.hasNext()) {
                    wrapper.addReceiver((AID) it.next());
                }

                it = msg.getAllReplyTo();

                while (it.hasNext()) {
                    wrapper.addReplyTo((AID) it.next());
                }

                wrapper.setLanguage(msg.getLanguage());
                wrapper.setOntology(msg.getOntology());
                wrapper.setProtocol(msg.getProtocol());
                wrapper.setInReplyTo(msg.getInReplyTo());
                wrapper.setReplyWith(msg.getReplyWith());
                wrapper.setConversationId(msg.getConversationId());
                wrapper.setReplyByDate(msg.getReplyByDate());
                if (msg.hasByteSequenceContent()) {
                    wrapper.setByteSequenceContent(msg.getByteSequenceContent());
                } else {
                    wrapper.setContent(msg.getContent());
                }

                wrapper.setEncoding(msg.getEncoding());
            }
        }

        return wrapper;
    }
}
