package org.semanticcloud.agents.broker.analyzers;

import jade.lang.acl.ACLMessage;
import org.semanticcloud.agents.base.messaging.OWLMessage;
import org.semanticcloud.agents.broker.NegotiationParameter;

import java.util.List;
import java.util.Objects;

public abstract class Analyzer{

    public abstract ACLMessage evaluateProposal(List<ACLMessage> proposals, List<NegotiationParameter> parameters);
}
