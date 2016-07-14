package org.semanticcloud.agents.broker.analyzers;

import org.semanticcloud.agents.broker.Criterion;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class AnalyzerOntoHelper {

    public static Object getIndividualPropertyValue(OWLOntology ontology, Criterion negParameter, OWLReasoner reasoner) {
        OWLDataFactory fac = ontology.getOWLOntologyManager().getOWLDataFactory();

        OWLClass contractClass = fac.getOWLClass(IRI.create(negParameter.getDescription()));
        NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(contractClass, true);
        Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();
        System.out.println("For class " + negParameter.getDescription() + " there are " + individuals.size() + " individuals");

        for(OWLNamedIndividual ind : individuals) {
//            System.out.println("analyze individual: " + ind.getIRI());
//            NodeSet<OWLNamedIndividual> contractValuesNodeSet = reasoner.getObjectPropertyValues(ind, proposedContract);
//            Set<OWLNamedIndividual> values = contractValuesNodeSet.getFlattened();
//            System.out.println("For property " + negParameter.getPropertyName() + " there are " + values.size() + " values");
            //for(OWLNamedIndividual proposedContractIndiv : values) {
                System.out.println("analyze value: " + ind.getIRI());
                //property of Contract individual - top level
                //if (negParameter.getDescription() == null) {
                    System.out.println("neg parameter description is null");
                    //data property
                        OWLDataProperty owlDataProperty = fac.getOWLDataProperty(IRI.create(negParameter.getPropertyName()));
                        if (owlDataProperty != null) {
                            Set<OWLLiteral> results = reasoner.getDataPropertyValues(ind, owlDataProperty);
                            for (OWLLiteral r : results) {
                                return r;
                            }
                        }
                        //object property
                        OWLObjectProperty property = fac.getOWLObjectProperty(IRI.create(negParameter.getPropertyName()));
                        if (property != null) {
                            NodeSet<OWLNamedIndividual> valuesNodeSet = reasoner.getObjectPropertyValues(ind, property);
                            Set<OWLNamedIndividual> results = valuesNodeSet.getFlattened();
                            for (OWLNamedIndividual r : results)
                                return r;
                        }
                //}
                //nested properties -> find domain in description, and then find property values
//                else {
//                    System.out.println("neg parameter description is not null");
//                    System.out.println("find domain class: " + negParameter.getDescription());
//                    OWLClass domainClass = fac.getOWLClass(IRI.create(negParameter.getDescription()));
//                    NodeSet<OWLNamedIndividual> domainNodeSet = reasoner.getInstances(domainClass, true);
//                    Set<OWLNamedIndividual> domainIndividuals = domainNodeSet.getFlattened();
//                    for (OWLNamedIndividual domainIndiv : domainIndividuals) {
//                        //check if this is the individual that has the property
//                        if (!checkIfIndividualCorrect(domainIndiv, negParameter, reasoner, fac))
//                            continue;
//                        //data property
//                            OWLDataProperty owlDataProperty = fac.getOWLDataProperty(IRI.create(negParameter.getPropertyName()));
//                            if (owlDataProperty != null) {
//                                Set<OWLLiteral> results = reasoner.getDataPropertyValues(domainIndiv, owlDataProperty);
//                                for (OWLLiteral r : results)
//                                    return r;
//                            }
//                            //object property
//                            OWLObjectProperty owlObjectProperty = fac.getOWLObjectProperty(IRI.create(negParameter.getPropertyName()));
//                            if (owlObjectProperty != null) {
//                                System.out.println("get property: " + owlObjectProperty + " for individual " + domainIndiv);
//                                try {
//                                    NodeSet<OWLNamedIndividual> valuesNodeSet = reasoner.getObjectPropertyValues(domainIndiv, owlObjectProperty);
//                                    Set<OWLNamedIndividual> results = valuesNodeSet.getFlattened();
//                                    for (OWLNamedIndividual r : results)
//                                        return r;
//                                } catch (NullPointerException e) {
//                                    System.out.println("no property defined!");
//                                }
//                            }
//                    }
//                }
            //}

        }
        System.out.println("<< getIndividualPropertyValue");

        return null;
    }

    //TODO add reccurssion, do not compare names but properties / distance between individuals
    private static boolean checkIfIndividualCorrect(OWLNamedIndividual indiv, Criterion np, OWLReasoner reasoner, OWLDataFactory fac) {
        System.out.println(">> checkIfIndividualCorrect: " + indiv);
        Criterion parent = np.getParent();

        if (parent != null) {
            if (parent.getDescription() != null) {
                OWLClass parentDomainClass = fac.getOWLClass(IRI.create(parent.getDescription()));
                System.out.println("parent class " + parentDomainClass);
                OWLObjectProperty property = fac.getOWLObjectProperty(IRI.create(parent.getPropertyName()));
                System.out.println("property " + property);
                NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(parentDomainClass, true);
                Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();

                for(OWLNamedIndividual ind : individuals) {
                    if (property != null) {
                        try {
                            NodeSet<OWLNamedIndividual> valuesNodeSet = reasoner.getObjectPropertyValues(ind, property);
                            Set<OWLNamedIndividual> results = valuesNodeSet.getFlattened();
                            for (OWLNamedIndividual r : results) {
                                if (r.toStringID().compareTo(indiv.toStringID()) == 0) {
                                    return true;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                return true;
            }
        }
        System.out.println("<< checkIfIndividualCorrect: " + indiv);
        return false;
    }

    public static boolean extractParameterValues(HashMap<Criterion, Object> parametersMap, List<Criterion> userConstraints, OWLOntology offer, OWLReasoner reasoner) {
        boolean allFound = true;
        boolean allFoundNested = true;
        for (Criterion np : userConstraints) {
            System.out.println("extract value for parameter " + np.getPropertyName());
            Object value = AnalyzerOntoHelper.getIndividualPropertyValue(offer, np, reasoner);
            if (value != null) {
                parametersMap.put(np, value);
            } else {
                System.out.println("No value for parameter " + np.getPropertyName());
                allFound = false;
            }
            allFoundNested = AnalyzerOntoHelper.extractParameterValues(parametersMap, np.getNestedParams(), offer, reasoner);
        }
        return (allFound && allFoundNested);
    }
}
