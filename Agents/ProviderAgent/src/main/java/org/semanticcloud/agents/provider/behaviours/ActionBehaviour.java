package org.semanticcloud.agents.provider.behaviours;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

public class ActionBehaviour extends AchieveREResponder {
    public ActionBehaviour(Agent a, MessageTemplate mt) {
        super(a, mt);
    }

    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
//        if (performAction()) {
//            System.out.println("Agent "+getLocalName()+": Action successfully performed");
//            ACLMessage inform = request.createReply();
//            inform.setPerformative(ACLMessage.INFORM);
//            return inform;
//        }
//        else {
//            System.out.println("Agent "+getLocalName()+": Action failed");
//            throw new FailureException("unexpected-error");
//        }
        return null;
    }
}
