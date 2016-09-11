package org.semanticcloud.agentGateway;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class Message {
	private String conversationId;
	private String recipientAID;
	private String content;

	public ACLMessage toACLMessage(){
		ACLMessage message = new ACLMessage(ACLMessage.REQUEST);

		message.setContent(content);
		//message.setOntology(Config.GUIOntology);
		message.setConversationId(conversationId);
		message.addReceiver(new AID(recipientAID, false));

		return message;
	}

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getRecipientAID() {
        return recipientAID;
    }

    public void setRecipientAID(String recipientAID) {
        this.recipientAID = recipientAID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
