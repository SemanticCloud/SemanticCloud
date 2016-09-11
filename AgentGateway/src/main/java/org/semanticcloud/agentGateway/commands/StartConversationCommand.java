package org.semanticcloud.agentGateway.commands;

import org.semanticcloud.agentGateway.Message;
import org.semanticcloud.agentGateway.Command;
import org.semanticcloud.agentGateway.Conversation;
import org.semanticcloud.agentGateway.ConversationRegistry;
import org.semanticcloud.agentGateway.MessageSender;

import java.util.UUID;

public class StartConversationCommand implements Command {

	private String conversationId;
	private final Message message;

	public StartConversationCommand(Message message){
		this.message = message;
		conversationId = UUID.randomUUID().toString();
		message.setConversationId(conversationId);
	}
	
	public void executeCommand(ConversationRegistry registry, MessageSender messageSender) {
		System.out.println(String.format("Starting conversation %s", conversationId));
		Conversation conversation = new Conversation(conversationId);
		registry.addConversation(conversation);
		messageSender.sendMessage(message);
	}

	public String getConversationId() {
		return conversationId;
	}

}
