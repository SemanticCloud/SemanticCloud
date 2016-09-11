package org.semanticcloud.agentGateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/gateway")
public class GatewayResource {
    @Autowired
    private Gateway gateway;

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public String startConversation(@RequestBody Message message){
        try {
            return gateway.startConversation(message);
        } catch (GatewayException e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value = "/send/{conversationId}", method = RequestMethod.POST)
    public void sendMessage(@PathVariable("conversationId") String conversationId, @RequestBody Message message){
        try {
            gateway.sendMessage(conversationId, message);
        } catch (GatewayException e) {
            e.printStackTrace();
        }

    }

    @RequestMapping(value = "/replies/{conversationId}")
    public List<Reply> getReplies(@PathVariable("conversationId") String conversationId){
        try {
            return gateway.getResponses(conversationId);
        } catch (GatewayException e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping( value = "/stop/{conversationId}")
    public void stopConversation(@PathVariable("conversationId") String conversationId){

    }
}
