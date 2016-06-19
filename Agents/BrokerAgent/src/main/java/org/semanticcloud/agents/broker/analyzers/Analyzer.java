package org.semanticcloud.agents.broker.analyzers;

import jade.lang.acl.ACLMessage;
import org.semanticcloud.agents.broker.Criterion;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.List;

public abstract class Analyzer{

    public abstract ACLMessage evaluateProposal(OWLOntology conditions, List<ACLMessage> proposals, List<Criterion> parameters);
}
