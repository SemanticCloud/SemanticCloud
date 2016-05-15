package org.semanticcloud.agents.broker;

import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import org.semanticcloud.agents.base.AgentType;
import org.semanticcloud.agents.base.BaseAgent;
import org.semanticcloud.agents.base.messaging.OWLMessage;
import org.semanticcloud.agents.broker.behaviours.NegotiationBehaviour;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

public class BrokerAgent extends BaseAgent {

    @Override
    protected void setup() {
        registerAgent(AgentType.BROKER);
        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            System.out.println("Onto.");
            OWLOntology ontology = manager.createOntology();
            System.out.println("start.");
            startNegotiations(ontology);
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
    }

    public List<AID> findProviders() {
        List<AID> agents = new LinkedList<>();

        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("provider");
        dfd.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, dfd);
            for (DFAgentDescription description : result) {
                agents.add(description.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return agents;
    }

    public void startNegotiations(OWLOntology conditions) throws OWLOntologyStorageException {
        List<AID> providers = findProviders();

        if(providers.size() ==0){
            //error no provider
            System.out.println("No responder specified.");
            return;
        }
        OWLMessage cfp = createCFP(conditions);
        providers.forEach(aid -> cfp.addReceiver(aid));
        NegotiationBehaviour negotiationBehaviour = new NegotiationBehaviour(this, cfp);
        addBehaviour(negotiationBehaviour);
    }

    public OWLMessage createCFP(OWLOntology conditions) throws OWLOntologyStorageException {
        OWLMessage message = new OWLMessage(ACLMessage.CFP);
        message.setContentOntology(conditions);
        message.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        message.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
        message.setConversationId(UUID.randomUUID().toString());
        return message;
    }
}
