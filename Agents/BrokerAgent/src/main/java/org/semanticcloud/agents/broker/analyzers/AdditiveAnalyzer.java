package org.semanticcloud.agents.broker.analyzers;

import jade.lang.acl.ACLMessage;
import org.semanticcloud.agents.base.messaging.OWLMessage;
import org.semanticcloud.agents.broker.NegotiationParameter;

import java.util.List;

public class AdditiveAnalyzer extends Analyzer {

    @Override
    public ACLMessage evaluateProposal(List<ACLMessage> proposals, List<NegotiationParameter> parameters) {
        ACLMessage best = null;


        // Evaluate proposals.
        int bestProposal = -1;

        for (ACLMessage proposal: proposals) {
            if (proposal.getPerformative() == ACLMessage.PROPOSE) {
                int o = Integer.parseInt(proposal.getContent());
                if (o > bestProposal) {
                    bestProposal = o;
                    best = proposal;
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

    private double calculateValue() {
        return 0;
    }
}
