package org.semanticcloud.agentGateway;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class Reply {

	private final String sender;
	private String content;

	private String conversiationId;

	public String getContent() {
		return content;
	}

	public Reply(ACLMessage message) {
		this.content = message.getContent();
		this.conversiationId = message.getConversationId();
		this.sender = message.getSender().getName();
	}

	public String getSender() {
		return sender;
	}

	public String getConversiationId() {
		return conversiationId;
	}
}
