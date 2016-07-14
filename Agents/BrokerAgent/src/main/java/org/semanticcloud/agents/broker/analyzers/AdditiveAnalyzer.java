package org.semanticcloud.agents.broker.analyzers;

import jade.lang.acl.ACLMessage;
import org.semanticcloud.agents.base.messaging.OWLMessage;
import org.semanticcloud.agents.broker.Criterion;
import org.semanticcloud.agents.broker.CriterionType;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdditiveAnalyzer extends Analyzer {

    @Override
    public ACLMessage evaluateProposal(OWLOntology conditions, List<ACLMessage> proposals, List<Criterion> parameters) {
        ACLMessage best = null;
        HashMap<Criterion, Object> minValues = new HashMap<>();
        HashMap<Criterion, Object> maxValues = new HashMap<>();
        //select only valid owl messages
        List<OWLMessage> owlMessages = proposals.stream()
                .filter(message -> message.getPerformative() == ACLMessage.PROPOSE)
                .map(message -> OWLMessage.wrap(message))
                .collect(Collectors.toList());


        // Evaluate proposals.
        Map<OWLMessage, HashMap<Criterion, Object>> values = new HashMap<>();
        double bestUtility = -1;

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
                OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
                ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
                OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
                OWLReasoner reasoner = reasonerFactory.createReasoner(contentOntology, config);
                reasoner.precomputeInferences();
                AnalyzerOntoHelper.extractParameterValues(parametersMap, parameters, contentOntology, reasoner);
                values.put(proposal, parametersMap);
            } catch (OWLOntologyCreationException e) {
                e.printStackTrace();
            }
        }
        calculateMinMax(parameters, values.values(), minValues, maxValues);


        //add normalization

        //find best
        for (OWLMessage proposal : owlMessages) {
            HashMap<Criterion, Object> value = values.get(proposal);
            double utility = calculateValue(value, parameters, minValues, maxValues);
            if (utility > bestUtility) {
                System.out.println(utility);
                bestUtility = utility;
                best = proposal;
            }
        }

        return best;
    }


    private double calculateValue(HashMap<Criterion, Object> values, List<Criterion> parameters, HashMap<Criterion, Object> minValues, HashMap<Criterion, Object> maxValues) {
        Double value = 0.0;
        for (Criterion criterion : parameters) {
            if (criterion.getNestedParams().isEmpty()) {
                Object o = values.get(criterion);
                Double max, min;
                max = (Double) maxValues.get(criterion);
                min = (Double) minValues.get(criterion);
                //give max weight if max and min are equal
                if (max == min) {
                    value += criterion.getWeight();
                    continue;
                }
                if (criterion.getType() == CriterionType.COST) {
                    value += 1 - (max - extractValue(o)) / (max - min);
                } else {
                    value += (max - extractValue(o)) / (max - min);
                }
            } else {
                value += criterion.getWeight() * calculateValue(values, criterion.getNestedParams(), minValues, maxValues);
            }

        }

        return value;
    }

    private Double extractValue(Object o) {
        if (o instanceof OWLLiteral) {
            OWLLiteral literal = (OWLLiteral) o;
            OWL2Datatype datatype = literal.getDatatype().getBuiltInDatatype();
            if (datatype == OWL2Datatype.XSD_DOUBLE) {
                return literal.parseDouble();
            } else if (datatype == OWL2Datatype.XSD_FLOAT) {
                return Double.valueOf(literal.parseFloat());
            } else if (datatype == OWL2Datatype.XSD_INTEGER) {
                return Double.valueOf(literal.parseInteger());
            }
        }
        return 0.0;
    }

    private void calculateMinMax(List<Criterion> parameters, Collection<HashMap<Criterion, Object>> values, HashMap<Criterion, Object> minValues, HashMap<Criterion, Object> maxValues) {
        for (Criterion criterion : parameters) {
            List<Double> collect = values
                    .stream()
                    .map(criterionObjectHashMap -> extractValue(criterionObjectHashMap.get(criterion))).collect(Collectors.toList());
            minValues.put(criterion, collect.stream().min(Double::compare).get());
            maxValues.put(criterion, collect.stream().max(Double::compare).get());
            System.out.println(criterion.getPropertyName());
            System.out.println("Max:" + collect.stream().min(Double::compare).get());
            System.out.println("Min:" + collect.stream().max(Double::compare).get());
            if (!criterion.getNestedParams().isEmpty()) {
                calculateMinMax(criterion.getNestedParams(), values, minValues, maxValues);
            }

        }

    }

}
