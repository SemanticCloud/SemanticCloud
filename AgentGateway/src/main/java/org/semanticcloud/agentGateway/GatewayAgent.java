package org.semanticcloud.agentGateway;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class GatewayAgent extends jade.wrapper.gateway.GatewayAgent implements MessageSender {
    private ConversationRegistry conversationRegistry = new ConversationRegistry();

    @Override
    protected void processCommand(java.lang.Object obj) {
        if (obj instanceof Command) {
            Command command = (Command) obj;

            try{
                command.executeCommand(conversationRegistry, this);
            }
            finally{
                releaseCommand(command);
            }
        }
    }

    @Override
    public void setup() {
        System.out.println(String.format("Gateway agent setting up (AID:%s).", getAID()));
        // Waiting for the answer
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {

                ACLMessage msg = receive();

                if ((msg != null)) {
                    conversationRegistry.addReply(msg.getConversationId(), msg);

                } else
                    block();
            }
        });

        super.setup();
    }

    public void sendMessage(Message message) {
        send(message.toACLMessage());
    }
}
