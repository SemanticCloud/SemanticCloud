package org.semanticcloud.agents.broker.behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import org.semanticcloud.agents.broker.BrokerAgent;
import org.semanticcloud.agents.broker.OWLParser;
import org.semanticcloud.agents.broker.analyzers.AdditiveAnalyzer;
import org.semanticcloud.agents.broker.analyzers.Analyzer;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class NegotiationBehaviour extends ContractNetInitiator {
    private Analyzer analyzer;
    private OWLOntology conditions;

    public NegotiationBehaviour(BrokerAgent agent, ACLMessage cfp, OWLOntology conditions) {
        super(agent, cfp);
        analyzer = new AdditiveAnalyzer();
        this.conditions = conditions;
    }

    @Override
    public BrokerAgent getAgent() {
        return (BrokerAgent) myAgent;
    }

    protected void handlePropose(ACLMessage propose, Vector v) {
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.setContent("Agent " + propose.getSender().getName() + " proposed " + propose.getContent());
        getAgent().sendMessageToClient(propose.getConversationId(),message);
    }

    protected void handleRefuse(ACLMessage refuse) {
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.setContent("Agent " + refuse.getSender().getName() + " refused " + refuse.getContent());
        getAgent().sendMessageToClient(refuse.getConversationId(),message);
    }

    protected void handleFailure(ACLMessage failure) {
        if (failure.getSender().equals(myAgent.getAMS())) {
            // FAILURE notification from the JADE runtime: the receiver
            // does not exist
            System.out.println("Responder does not exist");
        } else {
            System.out.println("Agent " + failure.getSender().getName() + " failed");
        }
    }

    protected void handleAllResponses(Vector responses, Vector acceptances) {

        Enumeration e = responses.elements();
        List<OWLProperty> properties = OWLParser.getPropertiesWithRestrictions(conditions);
        System.out.println(properties);
        ACLMessage best = analyzer.evaluateProposal(conditions, responses, null);
        while (e.hasMoreElements()) {
            ACLMessage msg = (ACLMessage) e.nextElement();
            if (msg.getPerformative() == ACLMessage.PROPOSE) {
                ACLMessage reply = msg.createReply();
                if (msg.getSender().equals(best.getSender())) {
                    ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                    message.setContent("Agent " + best.getSender().getName() + " best offer: " + best.getContent());
                    getAgent().sendMessageToClient(best.getConversationId(),message);
                    System.out.println("Accepting proposal " + best.getContent() + " from responder " + best.getSender().getName());
                    reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                } else {
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                }
                acceptances.addElement(reply);
            }
        }

    }

    protected void handleInform(ACLMessage inform) {
        System.out.println("Agent " + inform.getSender().getName() + " successfully performed the requested action");
    }

}
