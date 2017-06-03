package org.semanticcloud;


public interface Provider {
    void prepareProposal();

    void acceptProposal();

    void refuseProposal();

    void performAction();
}
