package org.semanticcloud.jena;

import org.apache.jena.ontology.AllValuesFromRestriction;
import org.apache.jena.ontology.IntersectionClass;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.ontology.SomeValuesFromRestriction;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.semanticcloud.jena.vocabulary.XSD;

public class ConditionParser {
    public void parseEqivalentClass(OntClass ontClass) {
        Condition condition = new Condition();
        ontClass.listEquivalentClasses()
                .filterKeep(OntClass::isAnon)
                .forEachRemaining(
                        c -> {
                            //e
                            System.out.println("Find eqivalent class");
                            parseClass(c);
                            //can be something else ?

                        }
                );

    }

    private void parseClass(OntClass ontClass){
        System.out.println("Condition class");
        if(ontClass.isIntersectionClass()){
            IntersectionClass intersectionClass = ontClass.asIntersectionClass();
            intersectionClass.listOperands().forEachRemaining(
                    intersectionMember -> {
                        if(intersectionMember.isRestriction()){
                            parseRestriction(intersectionMember.asRestriction());

                        }
                        else {
                            System.out.println("Intersection of class:"+ intersectionMember);
                        }
                    }
            );

        }

    }

    private void parseRestriction(Restriction restriction){
        OntProperty onProperty = restriction.getOnProperty();
        System.out.println("Restricted property:"+onProperty);
        if(onProperty.isDatatypeProperty()){
            parceDatatypeProperty(restriction);

        }
        else if( onProperty.isObjectProperty()){
            parseObjectProperty(restriction);

        }

    }

    private <T extends RDFNode> void parceDatatypeProperty(Restriction restriction){
        System.out.println("Datatype condition");
        if(restriction.isAllValuesFromRestriction()){
            System.out.println("AllValues:");
            AllValuesFromRestriction allValuesFromRestriction = restriction.asAllValuesFromRestriction();
            parseDataype(allValuesFromRestriction.getAllValuesFrom().as(OntClass.class));

        }
        else if (restriction.isSomeValuesFromRestriction()){
            System.out.println("SomeValues:");
            SomeValuesFromRestriction someValuesFromRestriction = restriction.asSomeValuesFromRestriction();
            parseDataype(someValuesFromRestriction.getSomeValuesFrom().as(OntClass.class));
        }
        else if (restriction.isHasValueRestriction()){
            System.out.println("HasValue:");
        }
        else if(restriction.isCardinalityRestriction()){
            System.out.println("Cardianlity:");
        }
        else if(restriction.isMinCardinalityRestriction()){
            System.out.println("MinCardianlity:");
        }
        else if(restriction.isMaxCardinalityRestriction()){
            System.out.println("MaxCardianlity:");

        }

    }



    private void parseObjectProperty(Restriction restriction){
        if(restriction.isAllValuesFromRestriction()){
            System.out.println("AllValues:");
            AllValuesFromRestriction allValuesFromRestriction = restriction.asAllValuesFromRestriction();
            parseClass(allValuesFromRestriction.getAllValuesFrom().as(OntClass.class));

        }
        else if (restriction.isSomeValuesFromRestriction()){
            System.out.println("SomeValues:");
            SomeValuesFromRestriction someValuesFromRestriction = restriction.asSomeValuesFromRestriction();
            parseClass(someValuesFromRestriction.getSomeValuesFrom().as(OntClass.class));
        }
        else if (restriction.isHasValueRestriction()){
            System.out.println("HasValue:");
        }
        else if(restriction.isCardinalityRestriction()){
            System.out.println("Cardianlity:");
        }
        else if(restriction.isMinCardinalityRestriction()){
            System.out.println("MinCardianlity:");
        }
        else if(restriction.isMaxCardinalityRestriction()){
            System.out.println("MaxCardianlity:");

        }
    }

    private void parseDataype(OntClass ontClass){
        System.out.println("dupa");
        if(ontClass.getPropertyValue(RDF.type).equals(RDFS.Datatype) && ontClass.hasProperty(OWL2.withRestrictions)){
            ontClass.getPropertyValue(OWL2.withRestrictions)
                    .as(RDFList.class)
                    .iterator()
                    .forEachRemaining(
                            f -> parseDatypeFacets(f.asResource())
                    );
        }


        if(ontClass.isIntersectionClass()){
            IntersectionClass intersectionClass = ontClass.asIntersectionClass();
            intersectionClass.listOperands().forEachRemaining(
                    intersectionMember -> {
                        System.out.println(intersectionMember);
//                        if(intersectionMember.isRestriction()){
//                            parseRestriction(intersectionMember.asRestriction());
//
//                        }
//                        else {
//                            System.out.println("Intersection of class:"+ intersectionMember);
//                        }
                    }
            );

        }
        else if (ontClass.isUnionClass()){

        }
        else {
            //ignore ???
        }

    }

    private void parseDatypeFacets(Resource resource){
        // number restriction
        if(resource.hasProperty(XSD.maxExclusive)){
            System.out.println(resource.getProperty(XSD.maxExclusive));
        }
        else if(resource.hasProperty(XSD.maxInclusive)){
            System.out.println(resource.getProperty(XSD.maxInclusive));
        }
        else if(resource.hasProperty(XSD.minExclusive)){
            System.out.println(resource.getProperty(XSD.minExclusive));
        }
        else if(resource.hasProperty(XSD.minInclusive)){
            System.out.println(resource.getProperty(XSD.minInclusive));
        }
        // support other facets restrictions ?


    }

}
