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

        List<AID> providers = findProviders();
        if (providers.size() != 0) {

            // Fill the CFP message
            ACLMessage msg = new ACLMessage(ACLMessage.CFP);
            for (AID aid : providers) {
                msg.addReceiver(aid);
            }
            //todo owl message components description
            msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
            // We want to receive a reply in 10 secs
            msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
            msg.setContent("dummy-action");
            msg.setConversationId(UUID.randomUUID().toString());

            addBehaviour(new ContractNetInitiator(this, msg) {

                protected void handlePropose(ACLMessage propose, Vector v) {
                    System.out.println("Agent " + propose.getSender().getName() + " proposed " + propose.getContent());
                }

                protected void handleRefuse(ACLMessage refuse) {
                    System.out.println("Agent " + refuse.getSender().getName() + " refused");
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

                    // Evaluate proposals.
                    int bestProposal = -1;
                    AID bestProposer = null;
                    ACLMessage accept = null;
                    Enumeration e = responses.elements();
                    while (e.hasMoreElements()) {
                        ACLMessage msg = (ACLMessage) e.nextElement();
                        if (msg.getPerformative() == ACLMessage.PROPOSE) {
                            ACLMessage reply = msg.createReply();
                            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                            acceptances.addElement(reply);
                            int proposal = Integer.parseInt(msg.getContent());
                            if (proposal > bestProposal) {
                                bestProposal = proposal;
                                bestProposer = msg.getSender();
                                accept = reply;
                            }
                        }
                    }
                    // Accept the proposal of the best proposer
                    if (accept != null) {
                        System.out.println("Accepting proposal " + bestProposal + " from responder " + bestProposer.getName());
                        accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    }
                }

                protected void handleInform(ACLMessage inform) {
                    System.out.println("Agent " + inform.getSender().getName() + " successfully performed the requested action");
                }
            });
        } else {
            System.out.println("No responder specified.");
        }
    }

    private void selectBestOffer() {

    }


    private List<AID> findProviders() {
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
}
