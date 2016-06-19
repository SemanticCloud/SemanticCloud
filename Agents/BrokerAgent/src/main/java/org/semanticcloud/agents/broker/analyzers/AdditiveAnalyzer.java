package org.semanticcloud.agents.broker.analyzers;

import jade.lang.acl.ACLMessage;
import org.semanticcloud.agents.base.messaging.OWLMessage;
import org.semanticcloud.agents.broker.Criterion;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdditiveAnalyzer extends Analyzer {

    @Override
    public ACLMessage evaluateProposal(OWLOntology conditions, List<ACLMessage> proposals, List<Criterion> parameters) {
        ACLMessage best = null;
        //select only valid owl messages
        List<OWLMessage> owlMessages = proposals.stream()
                .filter(message -> message.getPerformative() == ACLMessage.PROPOSE)
                .map(message -> OWLMessage.wrap(message))
                .collect(Collectors.toList());


        // Evaluate proposals.
        Map<OWLMessage, HashMap<Criterion, Object>> values = new HashMap<>();
        double bestProposal = -1;
//        for (Criterion  parameter:parameters) {
//            calculateValue(conditions ,parameter,proposals);
//        }

        //todo remove
        Criterion param = new Criterion();
        param.setDescription("http://semantic-cloud.org/Cloud#Compute");
        param.setPropertyName("http://semantic-cloud.org/Cloud#hasCPU");
        param.setWeight(1.0);
        Criterion param2 = new Criterion();
        param2.setDescription("http://semantic-cloud.org/Cloud#CPU");
        param2.setPropertyName("http://semantic-cloud.org/Cloud#hasCores");
        param2.setWeight(1.0);
        Criterion param3 = new Criterion();
        param3.setDescription("http://semantic-cloud.org/Cloud#CPU");
        param3.setPropertyName("http://semantic-cloud.org/Cloud#hasClockSpeed");
        param3.setWeight(1.0);
        param.addNestedParam(param2);
        param.addNestedParam(param3);
        parameters = new ArrayList<>();
        parameters.add(param);
        //


        //extract parameters values
        for (OWLMessage proposal : owlMessages) {
            OWLOntologyManager owlOntologyManager = conditions.getOWLOntologyManager();
            try {
                OWLOntology contentOntology = proposal.getContentOntology(owlOntologyManager);
                HashMap<Criterion, Object> parametersMap = new HashMap<>();
                AnalyzerOntoHelper.extractParameterValues(parametersMap, parameters, contentOntology);
                values.put(proposal, parametersMap);
//                parametersMap.forEach((negotiationParameter, o) -> {
//                    System.out.println(negotiationParameter.getPropertyName());
//                    System.out.println(o);
//                });
            } catch (OWLOntologyCreationException e) {
                e.printStackTrace();
            }
        }

        //add normalization

        //find best
        for (OWLMessage proposal : owlMessages) {
            HashMap<Criterion, Object> value = values.get(proposal);
            double o = calculateValue(value, parameters);
            if (o > bestProposal) {
                System.out.println(o);
                bestProposal = o;
                best = proposal;
            }
        }

        return best;
    }

    private void calculateValue(OWLOntology conditions, Criterion parameter, List<ACLMessage> proposals) {
        //todo add nested parameters
        parameter.getPropertyName();
    }


    private double calculateValue(HashMap<Criterion, Object> values, List<Criterion> parameters) {
        Double value = 0.0;
        for(Criterion criterion : parameters) {
            if(criterion.getNestedParams().isEmpty()){
                Object o = values.get(criterion);
                //System.out.println(o);
                value += criterion.getWeight() * extractValue(o);

            }
            else {
                value += criterion.getWeight() * calculateValue(values, criterion.getNestedParams());
            }

        }

        return value;
    }

    private Double extractValue(Object o){
        if(o instanceof OWLLiteral){
            OWLLiteral literal = (OWLLiteral) o;
            OWL2Datatype datatype = literal.getDatatype().getBuiltInDatatype();
            if(datatype == OWL2Datatype.XSD_DOUBLE){
                return literal.parseDouble();
            }
            else if (datatype == OWL2Datatype.XSD_FLOAT){
                return Double.valueOf(literal.parseFloat());
            }
            else if(datatype == OWL2Datatype.XSD_INTEGER){
                return Double.valueOf(literal.parseInteger());
            }
        }
        return 0.0;
    }

}
