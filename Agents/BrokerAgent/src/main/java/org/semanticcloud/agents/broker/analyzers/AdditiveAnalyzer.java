package org.semanticcloud.agents.broker.analyzers;

import jade.lang.acl.ACLMessage;
import org.semanticcloud.agents.base.messaging.OWLMessage;
import org.semanticcloud.agents.broker.NegotiationParameter;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AdditiveAnalyzer extends Analyzer {

    @Override
    public ACLMessage evaluateProposal(List<ACLMessage> proposals, List<NegotiationParameter> parameters) {
        ACLMessage best = null;


        // Evaluate proposals.
        double bestProposal = -1;

        for (ACLMessage message: proposals) {
            if (message.getPerformative() == ACLMessage.PROPOSE) {
                OWLMessage proposal = OWLMessage.wrap(message);
                double o = calculateValue(proposal);
                if (o > bestProposal) {
                    bestProposal = o;
                    best = message;
                }
            }
        }


        //preselect

        //find best

        //calculate value

//        for (NegotiationParameter parameter : parameters) {
//            double value = 0.0;
//            value = parameter.getWeight() * calculateValue();
//        }
        return best;
    }

    private double calculateValue(OWLMessage proposal) {

        return ThreadLocalRandom.current().nextDouble(0, 1);
    }
}
