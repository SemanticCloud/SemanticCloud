package org.semanticcloud.agentGateway;

import jade.core.Profile;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Properties;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.JadeGateway;
import org.semanticcloud.agentGateway.commands.GetRepliesCommand;
import org.semanticcloud.agentGateway.commands.SendMessageCommand;
import org.semanticcloud.agentGateway.commands.StartConversationCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class Gateway {
    @Autowired
    private GatewayConfiguration gatewayConfiguration;

    @PostConstruct
    private void init(){
        Properties properties = new Properties();

        properties.setProperty(Profile.PLATFORM_ID, gatewayConfiguration.getPlatformId());
        properties.setProperty(Profile.MAIN_PORT, gatewayConfiguration.getMainPort());
        properties.setProperty(Profile.MAIN_HOST, gatewayConfiguration.getMainHost());
        properties.setProperty(Profile.CONTAINER_NAME, gatewayConfiguration.getContainerName());
        properties.setProperty(Profile.LOCAL_HOST, gatewayConfiguration.getLocalhost());
        properties.setProperty(Profile.LOCAL_PORT, gatewayConfiguration.getLocalPort());
        properties.setProperty(Profile.SERVICES, gatewayConfiguration.getServices());
        JadeGateway.init("org.semanticcloud.agentGateway.GatewayAgent",properties);

    }

    public String startConversation(Message message) throws GatewayException {
        StartConversationCommand command = new StartConversationCommand(message);
        executeCommand(command);
        return command.getConversationId();
    }

    protected void executeCommand(Command command) throws GatewayException {
        try {
            JadeGateway.execute(command, 200);
        } catch (StaleProxyException e) {
            e.printStackTrace();
            throw new GatewayException(e);
        } catch (ControllerException e) {
            e.printStackTrace();
            throw new GatewayException(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new GatewayException(e);
        }
    }

    public void sendMessage(String conversationId, Message message) throws GatewayException {
        SendMessageCommand command = new SendMessageCommand(conversationId, message);
        executeCommand(command);
    }

    public List<Reply> getResponses(String conversationId) throws GatewayException {
        GetRepliesCommand command = new GetRepliesCommand(conversationId);
        executeCommand(command);
        List<ACLMessage> messages = command.getReplies();

        if (messages != null) {
            List<Reply> replies = new ArrayList<Reply>(messages.size());
            for (ACLMessage message : messages) {

                replies.add(new Reply(message));
            }
            return replies;
        }
        else
            return new ArrayList<Reply>();
    }
}
