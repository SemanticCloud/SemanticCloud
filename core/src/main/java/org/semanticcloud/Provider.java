package org.semanticcloud;


import org.apache.jena.ontology.OntModel;

public interface Provider {
    OntModel prepareProposal(OntModel cfp);

    void acceptProposal();

    void refuseProposal();

    void performAction();
}
