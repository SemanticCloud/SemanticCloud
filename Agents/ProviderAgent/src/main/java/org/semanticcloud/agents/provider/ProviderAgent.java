package org.semanticcloud.agents.provider;

import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import org.semanticcloud.agents.base.AgentType;
import org.semanticcloud.agents.base.BaseAgent;
import org.semanticcloud.agents.base.messaging.OWLMessage;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.AutoIRIMapper;

import java.io.File;

public class ProviderAgent extends BaseAgent {
    private String providerId;

    @Override
    protected void setup() {
        registerAgent(AgentType.PROVIDER);
        System.out.println("Agent " + getLocalName() + " waiting for CFP...");
        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
                MessageTemplate.MatchPerformative(ACLMessage.CFP));
        //Negotiations
        addBehaviour(new ContractNetResponder(this, template) {
            @Override
            protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
                try {
                    System.out.println("Agent " + getLocalName() + ": CFP received from " + cfp.getSender().getName() + ". Action is " + cfp.getContent());
                    int proposal = prepareProposal();
                    //if (proposal > 2) {
                    // We provide a proposal
                    System.out.println("Agent " + getLocalName() + ": Proposing " + proposal);
                    ACLMessage propose = cfp.createReply();
                    propose.setPerformative(ACLMessage.PROPOSE);
                    propose.setContent(String.valueOf(proposal));
                    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
                    AutoIRIMapper mapper = new AutoIRIMapper(new File("/opt/SemanticCloud"), true);
                    manager.getIRIMappers().add(mapper);
                    System.out.println("Onto.");
                    OWLOntology ontology = null;

                    ontology = manager.loadOntology(IRI.create("http://semantic-cloud.org/CloudOffer"));
                    OWLMessage wrap = OWLMessage.wrap(propose);
                    wrap.setContentOntology(ontology);


                    return wrap;
                } catch (OWLOntologyStorageException e) {
                    e.printStackTrace();
                    return null;
                } catch (OWLOntologyCreationException e) {
                    e.printStackTrace();
                    return null;
                }
//                } else {
//                    // We refuse to provide a proposal
//                    System.out.println("Agent " + getLocalName() + ": Refuse");
//                    throw new RefuseException("evaluation-failed");
//                }
            }

            @Override
            protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
                System.out.println("Agent " + getLocalName() + ": Proposal rejected");
            }

            @Override
            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
                System.out.println("Agent " + getLocalName() + ": Proposal accepted");
                if (performAction()) {
                    System.out.println("Agent " + getLocalName() + ": Action successfully performed");
                    ACLMessage inform = accept.createReply();
                    inform.setPerformative(ACLMessage.INFORM);
                    return inform;
                } else {
                    System.out.println("Agent " + getLocalName() + ": Action execution failed");
                    throw new FailureException("unexpected-error");
                }
            }
        });
        //Component / Resource action
    }

    public int prepareProposal() {
        // Simulate an evaluation by generating a random number
        return (int) (Math.random() * 10);
    }

    public boolean performAction() {
        // Simulate action execution by generating a random number
        return (Math.random() > 0.2);
    }
}
