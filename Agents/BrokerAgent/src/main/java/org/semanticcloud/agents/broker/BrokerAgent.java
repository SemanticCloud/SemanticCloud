package org.semanticcloud.agents.broker;

import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import org.semanticcloud.agents.base.AgentType;
import org.semanticcloud.agents.base.BaseAgent;
import org.semanticcloud.agents.base.messaging.OWLMessage;
import org.semanticcloud.agents.broker.behaviours.NegotiationBehaviour;
import org.semanticcloud.agents.broker.behaviours.StartBehaviour;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BrokerAgent extends BaseAgent {
    private Map<String, AID> clients = new HashMap<>();

    @Override
    protected void setup() {
        registerAgent(AgentType.BROKER);
        addBehaviour(new StartBehaviour());
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

    public void startNegotiations(ACLMessage msg, OWLOntology conditions) throws OWLOntologyStorageException {
        List<AID> providers = findProviders();

        if(providers.size() ==0){
            //error no provider
            System.out.println("No responder specified.");
            return;
        }
        clients.put(msg.getConversationId(),msg.getSender());
        OWLMessage cfp = createCFP(conditions);
        cfp.setConversationId(msg.getConversationId());
        providers.forEach(aid -> cfp.addReceiver(aid));
        NegotiationBehaviour negotiationBehaviour = new NegotiationBehaviour(this, cfp, conditions);
        addBehaviour(negotiationBehaviour);
    }

    public void sendMessageToClient(String conversationId, ACLMessage message){
        message.addReceiver(clients.get(conversationId));
        message.setConversationId(conversationId);
        send(message);
    }

    public OWLMessage createCFP(OWLOntology conditions) throws OWLOntologyStorageException {
        OWLMessage message = new OWLMessage(ACLMessage.CFP);
        message.setContentOntology(conditions);
        message.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        //message.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
        message.setConversationId(UUID.randomUUID().toString());
        return message;
    }
}
