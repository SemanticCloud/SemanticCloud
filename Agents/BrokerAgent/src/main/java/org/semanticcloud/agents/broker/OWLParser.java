package org.semanticcloud.agents.broker;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OWLParser {

    public static List<OWLProperty> getPropertiesWithRestrictions(OWLOntology onto) {
        System.out.println(">> getPropertiesWithRestrictions");
        List<OWLProperty> properties = new ArrayList<OWLProperty>();

        Set<OWLObjectProperty> objProperties = onto.getObjectPropertiesInSignature();
        Set<OWLDataProperty> dataProperties = onto.getDataPropertiesInSignature();

        for (OWLDataProperty dp : dataProperties) {
//            System.out.println("analyze data property " + dp.toStringID());
//            DataProperty d = new DataProperty(dp);
//            Set<OWLDataPropertyDomainAxiom> dpdas = ontoLoader.getAiGConditionsOntology().getDataPropertyDomainAxioms(dp);
//            dpdas.addAll(ontoLoader.getAiGtimeOntology().getDataPropertyDomainAxioms(dp));
//            for (OWLDataPropertyDomainAxiom dpda : dpdas)
//                d.getDomain().add(dpda.getDomain());
//            Set<OWLDataPropertyRangeAxiom> dpras = ontoLoader.getAiGConditionsOntology().getDataPropertyRangeAxioms(dp);
//            dpras.addAll(ontoLoader.getAiGtimeOntology().getDataPropertyRangeAxioms(dp));
//            for (OWLDataPropertyRangeAxiom dpra : dpras)
//                d.getRange().add(dpra.getRange());
            properties.add(dp);
        }

        for (OWLObjectProperty op : objProperties) {
//            System.out.println("analyze object property " + op.toStringID());
//            ObjectProperty o = new ObjectProperty(op);
//            Set<OWLObjectPropertyDomainAxiom> opdas = ontoLoader.getAiGConditionsOntology().getObjectPropertyDomainAxioms(op);
//            opdas.addAll(ontoLoader.getAiGtimeOntology().getObjectPropertyDomainAxioms(op));
//            for (OWLObjectPropertyDomainAxiom opda : opdas)
//                o.getDomain().add(opda.getDomain());
//            Set<OWLObjectPropertyRangeAxiom> opras = ontoLoader.getAiGConditionsOntology().getObjectPropertyRangeAxioms(op);
//            opras.addAll(ontoLoader.getAiGtimeOntology().getObjectPropertyRangeAxioms(op));
//            for (OWLObjectPropertyRangeAxiom opra : opras)
//                o.getRange().add(opra.getRange());
            properties.add(op);
        }
        System.out.println("<< getPropertiesWithRestrictions");
        return properties;
    }

//    public static List<NegParameter> getRestrictionsForProperties(List<OWLProperty> properties, OWLOntology onto, AiGOntologyLoader ontoLoader) throws Exception {
//        System.out.println(">> getRestrictionsForProperties");
//
//        List<NegParameter> rslt = new ArrayList<NegParameter>();
//
//        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
//        domFactory.setNamespaceAware(false);
//        DocumentBuilder builder = domFactory.newDocumentBuilder();
//        File tempFile = File.createTempFile("tmp","owl");
//        BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
//        out.write(ontoLoader.getOntologyManager().saveOntologyToString(onto));
//        out.flush();
//        out.close();
//        Document doc = builder.parse(tempFile);
//        XPath xpath = XPathFactory.newInstance().newXPath();
//
//        for (OWLProperty property :  properties) {
//            System.out.println("analyze data property " + property.toString());
//            if (property instanceof DataProperty) {
//                DataProperty dp = (DataProperty)property;
//                Object value = null;
//                if (dp.getRange().size() > 0) {
//                    OWLDataRange dr = dp.getRange().get(0);
//                    if (dr.asOWLDatatype().isDouble() || dr.asOWLDatatype().isFloat() || dr.asOWLDatatype().isInteger() || (dr.asOWLDatatype().getBuiltInDatatype() == OWL2Datatype.XSD_DATE_TIME)) {
//                        System.out.println("is double or float or integer");
//                        XPathExpression expr = xpath.compile("//Restriction/onProperty[@resource='" + property.toString().replaceFirst("<", "").replaceFirst(">", "") + "']/..");
//                        System.out.println("xpath: " + "//Restriction/onProperty[@resource='" + property.toString().replaceFirst("<", "").replaceFirst(">", "") + "']/..");
//                        NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
//                        for (int i= 0; i < nodeList.getLength(); i++) {
//                            Node restriction =nodeList.item(i);
//                            value = OWLParser.getValueForEquals(restriction);
//                            NegParameter np = new NegParameter(property, 1.0);
//                            if (value != null) {
//                                System.out.println(dp.displayText() + " equals " + value);
//                                if (OWLParser.findInResult(rslt, property) == null) {
//                                    np.setValue(value);
//                                    rslt.add(np);
//                                }
//                                else
//                                    OWLParser.findInResult(rslt, property).setValue(value);
//                                break;
//                            }
//                            value = OWLParser.getValueForGreaterThan(restriction);
//                            if (value != null) {
//                                System.out.println(dp.displayText() + " greater than " + value);
//                                if (OWLParser.findInResult(rslt, property) == null) {
//                                    np.setMinValue(value);
//                                    rslt.add(np);
//                                }
//                                else
//                                    OWLParser.findInResult(rslt, property).setMaxValue(value);
//                                break;
//                            }
//                            value = OWLParser.getValueForLessThan(restriction);
//                            if (value != null) {
//                                System.out.println(dp.displayText() + " less than " + value);
//                                if (OWLParser.findInResult(rslt, property) == null) {
//                                    np.setMaxValue(value);
//                                    rslt.add(np);
//                                }
//                                else
//                                    OWLParser.findInResult(rslt, property).setMinValue(value);
//                                break;
//                            }
//                        }
//                    }
//                }
//            } else if (property instanceof ObjectProperty) {
//                Object value = null;
//                System.out.println("is object");
//                XPathExpression expr = xpath.compile("//equivalentClass/Class/intersectionOf/Restriction/onProperty[@resource='" + property.toString().replaceFirst("<", "").replaceFirst(">", "") + "']/..");
//                System.out.println("xpath: " + "//equivalentClass/Class/intersectionOf/Restriction/onProperty[@resource='" + property.toString().replaceFirst("<", "").replaceFirst(">", "") + "']/..");
//                NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
//
//                for (int i= 0; i < nodeList.getLength(); i++) {
//                    Node restriction = nodeList.item(i);
//                    NegParameter np = new NegParameter(property, 1.0);
//                    value = OWLParser.getValueForIndividual(restriction);
//                    if (value != null) {
//                        if (OWLParser.findInResult(rslt, property) == null) {
//                            np.setValue(value);
//                            rslt.add(np);
//                        } else
//                            OWLParser.findInResult(rslt, property).setValue(value);
//                        break;
//                    }
//                    OWLParser.constructConstraintByNegParam(onto, ontoLoader, restriction, np);
//                    rslt.add(np);
//                }
//
//            }
//        }
//        tempFile.deleteOnExit();
//
//        //remove duplicateParameters from userConstrains
//        List<NegParameter> userConstraintsLimited = new ArrayList<NegParameter>();
//        for (int i = 0; i <  rslt.size(); i++) {
//            NegParameter np = rslt.get(i);
//            boolean duplicate = false;
//            if (np.getParent() == null && i < rslt.size()-1) {
//                duplicate = findDuplicateParameter(np, rslt.subList(i+1, rslt.size()));
//            }
//            if (!duplicate) {
//                userConstraintsLimited.add(np);
//            }
//        }
//
//        return userConstraintsLimited;
//    }
//
//    private static boolean findDuplicateParameter(NegParameter np, List<NegParameter> nestedParameters) {
//        boolean duplicate = false;
//        for (NegParameter n : nestedParameters) {
//            if (n.getProperty().displayText().compareTo(np.getProperty().displayText()) == 0) {
//                return true;
//            }
//            duplicate = findDuplicateParameter(np, n.getNestedParams());
//            if (duplicate)
//                return true;
//        }
//
//        return duplicate;
//    }
//
//    private static NegParameter findInResult(List<NegParameter> params, OWLProperty property) {
//        for (NegParameter np : params) {
//            if (np.getProperty().equals(property))
//                return np;
//        }
//        return null;
//    }
//
//    private static NegParameter findInResultByName(List<NegParameter> params, String name) {
//        for (NegParameter np : params) {
//            if (np.getProperty().toString().compareTo(name) == 0)
//                return np;
//        }
//        return null;
//    }
//
//    private static Object getValueForEquals(Node restriction) {
//        NodeList children = restriction.getChildNodes();
//        for (int i = 0; i < children.getLength(); i++) {
//            if (children.item(i).getNodeName().compareTo("hasValue") == 0) {
//                return children.item(i).getTextContent();
//            }
//        }
//        return null;
//    }
//
//    private static Object getValueForGreaterThan(Node restriction) {
//        NodeList children = restriction.getChildNodes();
//        for (int i = 0; i < children.getLength(); i++) {
//            if (children.item(i).getNodeName().compareTo("someValuesFrom") == 0) {
//                NodeList children2 = children.item(i).getChildNodes();
//                for (int j = 0; j < children2.getLength(); j++) {
//                    if (children2.item(j).getNodeName().compareTo("rdfs:Datatype") == 0) {
//                        NodeList children3 = children2.item(j).getChildNodes();
//                        for (int k = 0; k < children3.getLength(); k++) {
//                            if (children3.item(k).getNodeName().compareTo("withRestrictions") == 0) {
//                                NodeList children4 = children3.item(k).getChildNodes();
//                                for (int l = 0; l < children4.getLength(); l++) {
//                                    if (children4.item(l).getNodeName().compareTo("rdf:Description") == 0) {
//                                        NodeList children5 = children4.item(l).getChildNodes();
//                                        for (int m = 0; m < children5.getLength(); m++) {
//                                            if (children5.item(m).getNodeName().compareTo("xsd:minExclusive") == 0) {
//                                                return restriction.getTextContent().trim();
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    private static Object getValueForLessThan(Node restriction) {
//        NodeList children = restriction.getChildNodes();
//        for (int i = 0; i < children.getLength(); i++) {
//            if (children.item(i).getNodeName().compareTo("someValuesFrom") == 0) {
//                NodeList children2 = children.item(i).getChildNodes();
//                for (int j = 0; j < children2.getLength(); j++) {
//                    if (children2.item(j).getNodeName().compareTo("rdfs:Datatype") == 0) {
//                        NodeList children3 = children2.item(j).getChildNodes();
//                        for (int k = 0; k < children3.getLength(); k++) {
//                            if (children3.item(k).getNodeName().compareTo("withRestrictions") == 0) {
//                                NodeList children4 = children3.item(k).getChildNodes();
//                                for (int l = 0; l < children4.getLength(); l++) {
//                                    if (children4.item(l).getNodeName().compareTo("rdf:Description") == 0) {
//                                        NodeList children5 = children4.item(l).getChildNodes();
//                                        for (int m = 0; m < children5.getLength(); m++) {
//                                            if (children5.item(m).getNodeName().compareTo("xsd:maxExclusive") == 0) {
//                                                return restriction.getTextContent().trim();
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    /**
//     *
//     * Retrieves individual value for object property
//     * @param restriction
//     * @return
//     */
//    private static Object getValueForIndividual(Node restriction) {
//        NodeList children = restriction.getChildNodes();
//        for (int i = 0; i < children.getLength(); i++) {
//            if (children.item(i).getNodeName().compareTo("hasValue") == 0) {
//                NamedNodeMap attributes = children.item(i).getAttributes();
//                Node a = attributes.getNamedItem("rdf:resource");
//                if (a != null)
//                    return a.getNodeValue();
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Construct negotiation parameter for 'constraint by' value
//     * @param restriction
//     * @return
//     */
//    private static void constructConstraintByNegParam(OWLOntology onto, AiGOntologyLoader ontoLoader, Node restriction, NegParameter negParam) {
//        NodeList children = restriction.getChildNodes();
//        for (int i = 0; i < children.getLength(); i++) {
//            if (children.item(i).getNodeName().compareTo("someValuesFrom") == 0) {
//                Node classNode = null;
//                for (int j = 0; j < children.item(i).getChildNodes().getLength(); j++) {
//                    if (children.item(i).getChildNodes().item(j).getNodeName().compareTo("Class") == 0) {
//                        classNode = children.item(i).getChildNodes().item(j);
//                        break;
//                    }
//                }
//                Node intersectionNode = null;
//                for (int j = 0; j < classNode.getChildNodes().getLength(); j++) {
//                    if (classNode.getChildNodes().item(j).getNodeName().compareTo("intersectionOf") == 0) {
//                        intersectionNode = classNode.getChildNodes().item(j);
//                        break;
//                    }
//                }
//                if (intersectionNode == null)
//                    return;
//
//                //iteration through nested restrictions
//                NodeList nestedChildren = intersectionNode.getChildNodes();
//                String description = null;
//                for (int j = 0; j < nestedChildren.getLength(); j++) {
//                    if (nestedChildren.item(j).getNodeName().replaceAll("rdf:", "").compareTo("Description") == 0) {
//                        NamedNodeMap attributes = nestedChildren.item(j).getAttributes();
//                        for (int ii = 0; ii<attributes.getLength(); ii++) {
//                            if (attributes.item(ii).getNodeName().replaceAll("rdf:", "").compareTo("about") == 0) {
//                                description = attributes.item(ii).getNodeValue();
//                                break;
//                            }
//                        }
//                    } else if (nestedChildren.item(j).getNodeName().compareTo("Restriction") == 0) {
//                        OWLParser.addNegParameter(onto, ontoLoader, nestedChildren.item(j), description, negParam.getNestedParams());
//                        for (NegParameter np : negParam.getNestedParams())
//                            np.setParent(negParam);
//                    }
//                }
//            }
//        }
//    }
//
//    private static void addNegParameter(OWLOntology onto, AiGOntologyLoader ontoLoader, Node restriction, String description, List<NegParameter> rslt) {
//        //get property
//        Node onProperty = null;
//        for (int i = 0; i < restriction.getChildNodes().getLength(); i++) {
//            if (restriction.getChildNodes().item(i).getNodeName().compareTo("onProperty") == 0) {
//                onProperty = restriction.getChildNodes().item(i);
//                break;
//            }
//        }
//        String propertyStr = null;
//        NamedNodeMap attributes = onProperty.getAttributes();
//
//        Node a = attributes.getNamedItem("rdf:resource");
//        if (a != null)
//            propertyStr = a.getNodeValue();
//        OWLProperty property = OWLParser.getPropertyByName(onto, ontoLoader, propertyStr);
//
//        NegParameter np = new NegParameter(property, 1.0);
//        if (description != null)
//            np.setDescription(description);
//        System.out.println("property: " + property.toString() + " " + property.getClass());
//
//        if (property instanceof DataProperty) {
//            System.out.println("nested data property");
//            Object value = OWLParser.getValueForEquals(restriction);
//            if (value != null) {
//                System.out.println(property.displayText() + " equals " + value);
//                if (OWLParser.findInResultByName(rslt, property.toString()) == null) {
//                    np.setValue(value);
//                    rslt.add(np);
//                }
//                else
//                    OWLParser.findInResultByName(rslt, property.toString()).setValue(value);
//            }
//            value = OWLParser.getValueForGreaterThan(restriction);
//            if (value != null) {
//                System.out.println(property.displayText() + " greater than " + value);
//                if (OWLParser.findInResultByName(rslt, property.toString()) == null) {
//                    np.setMinValue(value);
//                    rslt.add(np);
//                }
//                else
//                    OWLParser.findInResultByName(rslt, property.toString()).setMaxValue(value);
//            }
//            value = OWLParser.getValueForLessThan(restriction);
//            if (value != null) {
//                System.out.println(property.displayText() + " less than " + value);
//                if (OWLParser.findInResultByName(rslt, property.toString()) == null) {
//                    np.setMaxValue(value);
//                    rslt.add(np);
//                }
//                else
//                    OWLParser.findInResultByName(rslt, property.toString()).setMinValue(value);
//            }
//
//        }
//        else if (property instanceof ObjectProperty) {
//            NegParameter nestedParam = new NegParameter(property, 1.0);
//            if (description != null)
//                nestedParam.setDescription(description);
//            Object value = OWLParser.getValueForIndividual(restriction);
//            if (value != null) {
//                if (OWLParser.findInResultByName(rslt, property.toString()) == null) {
//                    nestedParam.setValue(value);
//                    rslt.add(nestedParam);
//                } else
//                    OWLParser.findInResultByName(rslt, property.toString()).setValue(value);
//            } else {
//                OWLParser.constructConstraintByNegParam(onto, ontoLoader, restriction, np);
//                rslt.add(np);
//            }
//        }
//    }
//
//    private static OWLProperty getPropertyByName(OWLOntology onto, AiGOntologyLoader ontoLoader, String name) {
//
//        Set<OWLObjectProperty> objProperties = onto.getObjectPropertiesInSignature();
//        Set<OWLDataProperty> dataProperties = onto.getDataPropertiesInSignature();
//
//        for (OWLDataProperty dp : dataProperties) {
//            if (dp.toString().replaceFirst("<", "").replaceFirst(">", "").compareTo(name) != 0)
//                continue;
//            DataProperty d = new DataProperty(dp);
//            Set<OWLDataPropertyDomainAxiom> dpdas = ontoLoader.getAiGConditionsOntology().getDataPropertyDomainAxioms(dp);
//            dpdas.addAll(ontoLoader.getAiGtimeOntology().getDataPropertyDomainAxioms(dp));
//            for (OWLDataPropertyDomainAxiom dpda : dpdas)
//                d.getDomain().add(dpda.getDomain());
//            Set<OWLDataPropertyRangeAxiom> dpras = ontoLoader.getAiGConditionsOntology().getDataPropertyRangeAxioms(dp);
//            dpras.addAll(ontoLoader.getAiGtimeOntology().getDataPropertyRangeAxioms(dp));
//            for (OWLDataPropertyRangeAxiom dpra : dpras)
//                d.getRange().add(dpra.getRange());
//            return d;
//        }
//
//        for (OWLObjectProperty op : objProperties) {
//            if (op.toString().replaceFirst("<", "").replaceFirst(">", "").compareTo(name) != 0)
//                continue;
//            ObjectProperty o = new ObjectProperty(op);
//            Set<OWLObjectPropertyDomainAxiom> opdas = ontoLoader.getAiGConditionsOntology().getObjectPropertyDomainAxioms(op);
//            opdas.addAll(ontoLoader.getAiGtimeOntology().getObjectPropertyDomainAxioms(op));
//            for (OWLObjectPropertyDomainAxiom opda : opdas)
//                o.getDomain().add(opda.getDomain());
//            Set<OWLObjectPropertyRangeAxiom> opras = ontoLoader.getAiGConditionsOntology().getObjectPropertyRangeAxioms(op);
//            opras.addAll(ontoLoader.getAiGtimeOntology().getObjectPropertyRangeAxioms(op));
//            for (OWLObjectPropertyRangeAxiom opra : opras)
//                o.getRange().add(opra.getRange());
//            return o;
//        }
//        return null;
//    }
}
