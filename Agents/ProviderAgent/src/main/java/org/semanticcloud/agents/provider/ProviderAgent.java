package org.semanticcloud.agents.provider;

import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import org.semanticcloud.agents.base.AgentType;
import org.semanticcloud.agents.base.BaseAgent;
import org.semanticcloud.agents.provider.behaviours.ActionBehaviour;
import org.semanticcloud.agents.provider.behaviours.NegotiationBehaviour;

public class ProviderAgent extends BaseAgent {
    protected String providerId;
    protected String identity;
    protected String credential;

    protected void init() {

    }


    @Override
    protected void setup() {
        init();
        registerAgent(AgentType.PROVIDER);
        System.out.println("Agent " + getLocalName() + " waiting for CFP...");
        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
                MessageTemplate.MatchPerformative(ACLMessage.CFP));
        addBehaviour(new NegotiationBehaviour(this, template));
        MessageTemplate actionTemplate = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );
        addBehaviour(new ActionBehaviour(this,actionTemplate));
    }

    public String prepareProposal(String content) {
        return null;
    }

    public boolean performAction() {
        // Simulate action execution by generating a random number
        return (Math.random() > 0.2);
    }
}
