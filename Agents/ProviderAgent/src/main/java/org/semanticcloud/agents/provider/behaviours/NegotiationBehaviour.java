package org.semanticcloud.agents.provider.behaviours;

import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import org.semanticcloud.agents.provider.ProviderAgent;

public class NegotiationBehaviour extends ContractNetResponder {

    public NegotiationBehaviour(ProviderAgent a, MessageTemplate mt) {
        super(a, mt);

    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
        ACLMessage propose = cfp.createReply();
        propose.setPerformative(ACLMessage.PROPOSE);
        String ontology = getAgent().prepareProposal(cfp.getContent());
        propose.setContent(ontology);
        //OWLMessage wrap = OWLMessage.wrap(propose);
        //wrap.setContentOntology(ontology);


        return propose;
    }

    @Override
    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        System.out.println("Agent " + getAgent().getLocalName() + ": Proposal rejected");
    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
        System.out.println("Agent " + getAgent().getLocalName() + ": Proposal accepted");
        if (getAgent().performAction()) {
            System.out.println("Agent " + getAgent().getLocalName() + ": Action successfully performed");
            ACLMessage inform = accept.createReply();
            inform.setPerformative(ACLMessage.INFORM);
            return inform;
        } else {
            System.out.println("Agent " + getAgent().getLocalName() + ": Action execution failed");
            throw new FailureException("unexpected-error");
        }
    }

    @Override
    public ProviderAgent getAgent() {
        return (ProviderAgent) super.getAgent();
    }
}
