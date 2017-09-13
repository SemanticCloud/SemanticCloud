package org.semanticcloud.agents.provider;

import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.commons.io.IOUtils;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.shared.Lock;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.semanticcloud.Provider;
import org.semanticcloud.agents.base.AgentType;
import org.semanticcloud.agents.base.BaseAgent;
import org.semanticcloud.agents.provider.behaviours.ActionBehaviour;
import org.semanticcloud.agents.provider.behaviours.NegotiationBehaviour;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProviderAgent extends BaseAgent {
    protected Provider provider;
    protected String providerId;
    protected String identity;
    protected String credential;


    private String ns2 = "http://semantic-cloud.org/test#";

    protected void init() {

    }


    @Override
    protected void setup() {
        init();
        registerAgent(AgentType.PROVIDER);
        System.out.println("Agent " + getLocalName() + " waiting for CFP...");
        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
                MessageTemplate.MatchPerformative(ACLMessage.CFP));
        addBehaviour(new NegotiationBehaviour(this, template));
        MessageTemplate actionTemplate = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST) );
        addBehaviour(new ActionBehaviour(this,actionTemplate));
    }

    public String prepareProposal(String content) {
        OntModel cfp = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
        cfp.setDynamicImports(true);
        cfp.read(IOUtils.toInputStream(content), "RDF/XML");
        System.out.println("Agent " + getLocalName() + " get cfp");

        OntModel proposal = provider.prepareProposal(cfp);

        StringWriter out = new StringWriter();
        proposal.write(out, "RDF/XML");
        System.out.println("Agent " + getLocalName() + " send offer");
        return out.toString();
    }

    public boolean performAction() {
        // Simulate action execution by generating a random number
        return (Math.random() > 0.2);
    }


}
