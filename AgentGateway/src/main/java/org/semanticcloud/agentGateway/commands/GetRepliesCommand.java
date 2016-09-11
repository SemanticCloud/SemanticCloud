package org.semanticcloud.agentGateway.commands;


import jade.lang.acl.ACLMessage;
import org.semanticcloud.agentGateway.Command;
import org.semanticcloud.agentGateway.ConversationRegistry;
import org.semanticcloud.agentGateway.MessageSender;

import java.util.List;


public class GetRepliesCommand implements Command {
	private String conversationId;
	private List<ACLMessage> replies;

	public GetRepliesCommand(String conversationId) {
		this.conversationId = conversationId;
	}

	public void executeCommand(ConversationRegistry registry, MessageSender messageSender) {
		System.out.println(String.format("Fetching replies for conversation %s", conversationId));
		replies = registry.getReplies(conversationId);
	}

	public String getConversationId() {
		return conversationId;
	}

	public List<ACLMessage> getReplies() {
		return replies;
	}

	public void setReplies(List<ACLMessage> replies) {
		this.replies = replies;		
	}

}
