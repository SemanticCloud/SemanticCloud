package org.semanticcloud.agents.broker.behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.commons.io.IOUtils;
import org.semanticcloud.agents.broker.BrokerAgent;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.AutoIRIMapper;

import java.io.File;

public class StartBehaviour extends CyclicBehaviour {
    private MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

    @Override
    public BrokerAgent getAgent() {
        return (BrokerAgent) myAgent;
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive(template);
        if (msg != null) {
            try {
                OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
                AutoIRIMapper mapper = new AutoIRIMapper(new File("/opt/SemanticCloud"), true);
                manager.getIRIMappers().add(mapper);
                System.out.println("Onto.");
                OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IOUtils.toInputStream(msg.getContent()));


                //ontology = manager.loadOntology(IRI.create("https://semanticcloud.github.io/Ontology/cloud.owlR"));
                System.out.println("start.");
                getAgent().startNegotiations(msg, ontology);
            } catch (OWLOntologyStorageException e) {
                e.printStackTrace();
            } catch (OWLOntologyCreationException e) {
                e.printStackTrace();
            }
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent("start");
            getAgent().sendMessageToClient(msg.getConversationId(), reply);


        } else {
            block();
        }

    }
}
