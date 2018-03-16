package owl.owlapi;

import cern.colt.matrix.impl.SparseObjectMatrix2D;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.search.Searcher;
import org.semanticweb.owlapi.util.OWLClassExpressionCollector;
import owl.sod.hlds.AtomicConcept;
import owl.sod.hlds.GreaterThanRole;
import owl.sod.hlds.Item;
import owl.sod.hlds.LessThanRole;
import owl.sod.hlds.SemanticDescription;
import owl.sod.hlds.UniversalRole;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class KBWrapper {
    private RestrictionVisitor restrictionVisitor;
    HashMap<String, Item> ontology;
    HashMap<Integer, String> atomics;
    HashMap<String, Integer> invAtomics;
    private HashMap<String, Integer> primitives;
    HashMap<Integer, Nodo> tassonomia;
    HashMap<String, Item> concetti_norm;
    private HashMap<Integer, Boolean> visitatiTop;
    private HashMap<Integer, Boolean> visitatiBottom;
    private int numConfrontiSussunzione = 0;

    private int num;

    private SparseObjectMatrix2D subsumes;

    private List<String> defClasses;


    public KBWrapper(OWLOntology rootOntology) {
        normalizeOntology(rootOntology);
    }

    public boolean isSatisfiable(String classIRI) {
        if (classIRI.contains("Thing")) {
            return true;
        }
        if (this.concetti_norm.containsKey(classIRI)) {
            Item item2 = (Item) this.concetti_norm.get(classIRI);
            return newcheckConsistency(item2.description);
        }
        AtomicConcept concettoC = new AtomicConcept();
        concettoC.name = classIRI;

        Item item2 = new Item(classIRI);
        item2.description.addConcept(concettoC);
        normalizzaItem(item2);
        this.concetti_norm.put(classIRI, item2);
        return newcheckConsistency(item2.description);
    }


    public List<String> getUnsatisfiableClasses(HashMap<String, Item> ontology) {
        List<String> unsClasses = new ArrayList();

        Iterator<String> iter1 = ontology.keySet().iterator();

        while (iter1.hasNext()) {
            String key1 = (String) iter1.next();
            AtomicConcept concetto1 = new AtomicConcept();
            concetto1.name = key1;
            Item item1 = new Item(key1);
            item1.description.addConcept(concetto1);
            normalizzaItem(item1);
            if (!newcheckConsistency(item1.description)) {
                unsClasses.add(key1);
            }
        }

        return unsClasses;
    }


    public void normalizeOntology(OWLOntology onto) {
        try {
            this.ontology = new HashMap();
            this.concetti_norm = new HashMap();

            this.restrictionVisitor = new RestrictionVisitor();
            this.defClasses = new ArrayList();

            this.atomics = new HashMap();
            this.invAtomics = new HashMap();
            this.primitives = new HashMap();
            this.num = 0;
            this.invAtomics.put("Thing", Integer.valueOf(this.num));
            this.atomics.put(Integer.valueOf(this.num++), "Thing");
            this.invAtomics.put("Nothing", Integer.valueOf(1));
            this.atomics.put(Integer.valueOf(this.num++), "Nothing");

            for (OWLClass cls : onto.getClassesInSignature()) {
                Item c = new Item(cls.getIRI().toString());

                Set<OWLClassExpression> superClasses = EntitySearcher.getSuperClasses(cls, onto).collect(Collectors.toSet());

                for (OWLClassExpression desc : superClasses) {
                    exploreClass(desc, c);
                }

                if (c.name.equals("owl:Thing")) {
                    c.name = "Thing";
                }

                if (!this.atomics.containsValue(c.name)) {
                    this.invAtomics.put(c.name, Integer.valueOf(this.num));
                    this.atomics.put(Integer.valueOf(this.num++), c.name);
                    this.primitives.put(c.name, Integer.valueOf(0));
                }

                Set<OWLClassExpression> disjClasses = EntitySearcher.getDisjointClasses(cls, onto).collect(Collectors.toSet());
                boolean exist;
                for (OWLClassExpression desc : disjClasses) {
                    exist = false;
                    for (int i = 0; i < c.description.atomicConcepts.size(); i++) {
                        if (((AtomicConcept) c.description.atomicConcepts.get(i)).name.equals(desc.asOWLClass().getIRI().toString())) {
                            exist = true;
                        }
                    }
                    if (!exist) {
                        AtomicConcept newDisjConcept = new AtomicConcept();
                        newDisjConcept.name = desc.asOWLClass().getIRI().toString();
                        newDisjConcept.denied = true;
                        c.description.addConcept(newDisjConcept);
                    }
                }

                Set<OWLClassExpression> equivClasses = EntitySearcher.getEquivalentClasses(cls, onto).collect(Collectors.toSet());
                for (OWLClassExpression desc :  equivClasses) {
                    exploreClass(desc, c);
                    c.equivalent = true;
                    this.defClasses.add(c.name);
                }

                this.ontology.put(c.name, c);
            }


            this.tassonomia = new HashMap();
            this.subsumes = new SparseObjectMatrix2D(this.atomics.size(), this.atomics.size());
            classifyInclusions();
            classifyDefined();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public HashMap<String, Item> normalizeIndividuals(OWLOntology onto) {
        HashMap<String, Item> individualsMap = new HashMap();
        try {
            this.restrictionVisitor = new RestrictionVisitor();
            Set<OWLNamedIndividual> individuals = onto.getIndividualsInSignature();
            normalizeIndividuals(onto, individuals, individualsMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return individualsMap;
    }

    private void normalizeIndividuals(OWLOntology onto, Set<OWLNamedIndividual> individuals, HashMap<String, Item> map) {
        for (OWLNamedIndividual ind : individuals) {
            Item item = new Item(ind.asOWLNamedIndividual().getIRI().toString());

            Set<OWLClassExpression> express = Searcher.types(onto.classAssertionAxioms(ind)).collect(Collectors.toSet());
            for (OWLClassExpression exp : express) {
                exploreItem(exp, item);
            }
            normalizzaItem(item);
            map.put(item.name, item);
        }
    }

    private void exploreItem(OWLClassExpression expression, Item item) {
        String type = expression.getClassExpressionType().toString();
        if (type.equals("Class")) {
            OWLClass classe = expression.asOWLClass();
            AtomicConcept newConcept = new AtomicConcept();
            newConcept.name = classe.getIRI().toString();
            item.description.addConcept(newConcept);
        } else if (type.equals("ObjectSomeValuesFrom")) {
            this.restrictionVisitor.createExistential();
            expression.accept(this.restrictionVisitor);
            item.description.addGreaterThanRole(this.restrictionVisitor.getGTRestriction());
        } else if (type.equals("ObjectAllValuesFrom")) {
            this.restrictionVisitor.createUniversal();
            expression.accept(this.restrictionVisitor);
            item.description.addUniversalRole(this.restrictionVisitor.getUVRestriction());
        } else if (type.equals("ObjectIntersectionOf")) {
            Set<OWLClassExpression> intersect = expression.asConjunctSet();
            for (OWLClassExpression descrizione : intersect) {
                exploreItem(descrizione, item);
            }
        } else if (type.equals("ObjectMaxCardinality")) {
            this.restrictionVisitor.createCardMax();
            expression.accept(this.restrictionVisitor);
            item.description.addLessThanRole(this.restrictionVisitor.getLTRestriction());
        } else if (type.equals("ObjectMinCardinality")) {
            this.restrictionVisitor.createCardMin();
            expression.accept(this.restrictionVisitor);
            item.description.addGreaterThanRole(this.restrictionVisitor.getGTRestriction());
        } else if (type.equals("ObjectExactCardinality")) {
            this.restrictionVisitor.createCard();
            expression.accept(this.restrictionVisitor);
            item.description.addGreaterThanRole(this.restrictionVisitor.getGTRestriction());
            item.description.addLessThanRole(this.restrictionVisitor.getLTRestriction());
        }
    }

    public void exploreClass(OWLClassExpression expression, Item c) {
        String type = expression.getClassExpressionType().toString();
        boolean exist;
        if (type.equals("Class")) {
            OWLClass classe = expression.asOWLClass();
            String nome = classe.getIRI().toString();

            if (nome.equals("owl:Thing")) {
                nome = "Thing";
            }
            if (!this.atomics.containsValue(nome)) {
                this.invAtomics.put(nome, Integer.valueOf(this.num));
                this.atomics.put(Integer.valueOf(this.num++), nome);
                this.primitives.put(nome, Integer.valueOf(1));
            } else {
                this.primitives.put(nome, Integer.valueOf(1));
            }
            exist = false;
            for (int i = 0; i < c.description.atomicConcepts.size(); i++) {
                if (((AtomicConcept) c.description.atomicConcepts.get(i)).name.equals(nome)) {
                    exist = true;
                }
            }
            if (!exist) {
                AtomicConcept newConcept = new AtomicConcept();
                newConcept.name = nome;
                c.description.addConcept(newConcept);
            }
        } else if (type.equals("ObjectSomeValuesFrom")) {
            this.restrictionVisitor.createExistential();
            expression.accept(this.restrictionVisitor);
            c.description.addGreaterThanRole(this.restrictionVisitor
                    .getGTRestriction());
        } else if (type.equals("ObjectAllValuesFrom")) {
            this.restrictionVisitor.createUniversal();
            expression.accept(this.restrictionVisitor);
            c.description.addUniversalRole(this.restrictionVisitor
                    .getUVRestriction());
        } else if (type.equals("ObjectIntersectionOf")) {
            Set<OWLClassExpression> intersect = expression.asConjunctSet();
            for (OWLClassExpression descrizione : intersect) {
                exploreClass(descrizione, c);
            }
        } else if (type.equals("ObjectMaxCardinality")) {
            this.restrictionVisitor.createCardMax();
            expression.accept(this.restrictionVisitor);
            c.description
                    .addLessThanRole(this.restrictionVisitor.getLTRestriction());
        } else if (type.equals("ObjectMinCardinality")) {
            this.restrictionVisitor.createCardMin();
            expression.accept(this.restrictionVisitor);
            c.description.addGreaterThanRole(this.restrictionVisitor
                    .getGTRestriction());
        } else if (type.equals("ObjectExactCardinality")) {
            this.restrictionVisitor.createCard();
            expression.accept(this.restrictionVisitor);
            c.description.addGreaterThanRole(this.restrictionVisitor
                    .getGTRestriction());
            c.description
                    .addLessThanRole(this.restrictionVisitor.getLTRestriction());
        }
    }

    private class RestrictionVisitor extends OWLClassExpressionCollector {
        GreaterThanRole gtRestriction = null;
        LessThanRole ltRestriction = null;
        UniversalRole uvRestriction = null;


        public RestrictionVisitor() {
        }

        public GreaterThanRole getGTRestriction() {
            return this.gtRestriction;
        }

        public LessThanRole getLTRestriction() {
            return this.ltRestriction;
        }

        public UniversalRole getUVRestriction() {
            return this.uvRestriction;
        }

        public void createUniversal() {
            this.uvRestriction = new UniversalRole("");
        }

        public void createExistential() {
            this.gtRestriction = new GreaterThanRole();
        }

        public void createCard() {
            this.gtRestriction = new GreaterThanRole();
            this.ltRestriction = new LessThanRole();
        }

        public void createCardMin() {
            this.gtRestriction = new GreaterThanRole();
        }

        public void createCardMax() {
            this.ltRestriction = new LessThanRole();
        }


        public Collection<OWLClassExpression> visit(OWLClass desc) {
            return objects;
        }


        public Collection<OWLClassExpression> visit(OWLObjectSomeValuesFrom desc) {
            this.gtRestriction.name = ((OWLObjectPropertyExpression) desc.getProperty()).asOWLObjectProperty().getIRI().getFragment();
            this.gtRestriction.cardinality = 1;
            return objects;
        }

        public Collection<OWLClassExpression> visit(OWLObjectAllValuesFrom desc) {
            this.uvRestriction.name = ((OWLObjectPropertyExpression) desc.getProperty()).asOWLObjectProperty().getIRI().getFragment();
            exploreFiller((OWLClassExpression) desc.getFiller(), this.uvRestriction.filler);
            return objects;
        }

        public Collection<OWLClassExpression> visit(OWLObjectMaxCardinality desc) {
            this.ltRestriction.name = ((OWLObjectPropertyExpression) desc.getProperty()).asOWLObjectProperty().getIRI().getFragment();
            this.ltRestriction.cardinality = desc.getCardinality();
            return objects;
        }

        public Collection<OWLClassExpression> visit(OWLObjectMinCardinality desc) {
            this.gtRestriction.name = ((OWLObjectPropertyExpression) desc.getProperty()).asOWLObjectProperty().getIRI().getFragment();
            this.gtRestriction.cardinality = desc.getCardinality();
            return objects;
        }

        public Collection<OWLClassExpression> visit(OWLObjectExactCardinality desc) {
            this.gtRestriction.name = ((OWLObjectPropertyExpression) desc.getProperty()).asOWLObjectProperty().getIRI().getFragment();
            this.gtRestriction.cardinality = desc.getCardinality();
            this.ltRestriction.name = ((OWLObjectPropertyExpression) desc.getProperty()).asOWLObjectProperty().getIRI().getFragment();
            this.ltRestriction.cardinality = desc.getCardinality();
            return objects;
        }

        public void exploreFiller(OWLClassExpression expression, SemanticDescription res) {
            String type = expression.getClassExpressionType().toString();
            AtomicConcept newConcept;
            if (type.equals("Class")) {
                OWLClass classe = expression.asOWLClass();
                String nome = classe.getIRI().toString();

                if (nome.equals("owl:Thing")) {
                    nome = "Thing";
                }
                if (!KBWrapper.this.atomics.containsValue(nome)) {
                    KBWrapper.this.invAtomics.put(nome, Integer.valueOf(KBWrapper.this.num));
                    KBWrapper.this.atomics.put(Integer.valueOf(KBWrapper.this.num++), nome);
                    KBWrapper.this.primitives.put(nome, Integer.valueOf(0));
                }
                newConcept = new AtomicConcept();
                newConcept.name = nome;
                res.addConcept(newConcept);
            } else if (type.equals("ObjectSomeValuesFrom")) {
                RestrictionVisitor visitor = new RestrictionVisitor();
                visitor.createExistential();
                expression.accept(visitor);
                res.addGreaterThanRole(visitor.getGTRestriction());
            } else if (type.equals("ObjectAllValuesFrom")) {
                RestrictionVisitor visitor = new RestrictionVisitor();
                visitor.createUniversal();
                expression.accept(visitor);
                res.addUniversalRole(visitor.getUVRestriction());
            } else if (type.equals("ObjectIntersectionOf")) {
                Set<OWLClassExpression> intersect = expression.asConjunctSet();
                for (OWLClassExpression descrizione : intersect) {
                    exploreFiller(descrizione, res);
                }
            } else if (type.equals("ObjectMaxCardinality")) {
                RestrictionVisitor visitor = new RestrictionVisitor();
                visitor.createCardMax();
                expression.accept(visitor);
                res.addLessThanRole(visitor.getLTRestriction());
            } else if (type.equals("ObjectMinCardinality")) {
                RestrictionVisitor visitor = new RestrictionVisitor();
                visitor.createCardMin();
                expression.accept(visitor);
                res.addGreaterThanRole(visitor.getGTRestriction());
            } else if (type.equals("ObjectExactCardinality")) {
                RestrictionVisitor visitor = new RestrictionVisitor();
                visitor.createCard();
                expression.accept(visitor);
                res.addGreaterThanRole(visitor.getGTRestriction());
                res.addLessThanRole(visitor.getLTRestriction());
            }
        }
    }

    public static String nameExtraction(String name) {
        if (name.contains("Thing")) {
            return name;
        }
        int posizione = name.indexOf("#");
        String nome = name.substring(posizione + 1, name.length() - 1);
        return nome;
    }

    public void normalizzaItem(Item item) {
        List<AtomicConcept> atomicConcepts = (ArrayList) item.description.getConcepts();
        List<AtomicConcept> concepts2remove = new ArrayList();
        List<GreaterThanRole> gtRestrictions = (ArrayList) item.description.greaterThanRoles;
        List<LessThanRole> ltRestrictions = (ArrayList) item.description.lessThanRoles;
        List<UniversalRole> uvRestrictions = (ArrayList) item.description.universalRoles;
        List<AtomicConcept> complements = (ArrayList) item.description.getComplements();
        List<AtomicConcept> newConcepts = new ArrayList();
        List<GreaterThanRole> newgtRestrictions = new ArrayList();
        List<LessThanRole> newltRestrictions = new ArrayList();
        List<UniversalRole> newuvRestrictions = new ArrayList();
        List<AtomicConcept> newComplements = new ArrayList();

        for (int i = 0; i < atomicConcepts.size(); i++) {
            AtomicConcept atomicConcept = (AtomicConcept) atomicConcepts.get(i);
            unfoldConcept(atomicConcept, newConcepts, newgtRestrictions,
                    newltRestrictions, newuvRestrictions, newComplements,
                    concepts2remove, atomicConcepts, item, gtRestrictions,
                    ltRestrictions, uvRestrictions, complements);
        }

        for (int i = 0; i < newConcepts.size(); i++) {
            AtomicConcept s = (AtomicConcept) newConcepts.get(i);
            if (!atomicConcepts.contains(s)) {
                atomicConcepts.add(s);
                item.description.atomicConcepts.add(s);
            }
        }

        for (int i = 0; i < newgtRestrictions.size(); i++) {
            GreaterThanRole r = (GreaterThanRole) newgtRestrictions.get(i);
            if (!gtRestrictions.contains(r)) {
                gtRestrictions.add(r);
            }
        }

        for (int i = 0; i < newltRestrictions.size(); i++) {
            LessThanRole r = (LessThanRole) newltRestrictions.get(i);
            if (!ltRestrictions.contains(r)) {
                ltRestrictions.add(r);
            }
        }

        for (int i = 0; i < newuvRestrictions.size(); i++) {
            UniversalRole r = (UniversalRole) newuvRestrictions.get(i);

            uvRestrictions.add(r);
        }


        newgtRestrictions = new ArrayList();
        newltRestrictions = new ArrayList();
        newuvRestrictions = new ArrayList();

        for (int i = 0; i < newComplements.size(); i++) {
            AtomicConcept s = (AtomicConcept) newComplements.get(i);
            if (!complements.contains(s)) {
                complements.add(s);
                item.description.atomicConcepts.add(s);
            }
        }

        for (int i = 0; i < concepts2remove.size(); i++) {
            AtomicConcept s = (AtomicConcept) concepts2remove.get(i);
            if (atomicConcepts.contains(s)) {
                atomicConcepts.remove(s);
                item.description.atomicConcepts.remove(s);
            }
        }

        List<GreaterThanRole> newgtItemRestrictions = new ArrayList();
        List<LessThanRole> newltItemRestrictions = new ArrayList();
        List<UniversalRole> newuvItemRestrictions = new ArrayList();

        for (int i = 0; i < uvRestrictions.size(); i++) {
            UniversalRole r = (UniversalRole) uvRestrictions.get(i);
            unfoldRestriction(r, newConcepts, newgtRestrictions,
                    newltRestrictions, newuvRestrictions, newComplements,
                    concepts2remove, atomicConcepts, item, newgtItemRestrictions,
                    newltItemRestrictions, newuvItemRestrictions);
        }

        atomicConcepts = new ArrayList(item.description.getConcepts());
        complements = new ArrayList(item.description.getComplements());

        for (int i = 0; i < newgtItemRestrictions.size(); i++) {
            GreaterThanRole r = (GreaterThanRole) newgtItemRestrictions.get(i);
            gtRestrictions.add(r);
        }

        for (int i = 0; i < newltItemRestrictions.size(); i++) {
            LessThanRole r = (LessThanRole) newltItemRestrictions.get(i);
            ltRestrictions.add(r);
        }

        for (int i = 0; i < newuvItemRestrictions.size(); i++) {
            UniversalRole r = (UniversalRole) newuvItemRestrictions.get(i);
            uvRestrictions.add(r);
        }

        newcheckRestrictions(item.description.atomicConcepts, gtRestrictions, ltRestrictions, uvRestrictions);
    }


    void checkRestrictions(List<GreaterThanRole> gtRestrictions, List<LessThanRole> ltRestrictions, List<UniversalRole> uvRestrictions) {
        boolean stop;


        do {
            List<GreaterThanRole> newgtRestrictions = new ArrayList();
            List<LessThanRole> newltRestrictions = new ArrayList();
            List<UniversalRole> newuvRestrictions = new ArrayList();
            stop = false;
            boolean empty = false;
            List<GreaterThanRole> gtrestr2remove = new ArrayList();
            List<LessThanRole> ltrestr2remove = new ArrayList();
            List<UniversalRole> uvrestr2remove = new ArrayList();
            for (int i = 0; (i < gtRestrictions.size()) && (!stop); i++) {
                GreaterThanRole gtrestriction = (GreaterThanRole) gtRestrictions.get(i);
                for (int j = 0; (j < gtRestrictions.size()) && (!stop) && (j != i); j++) {
                    GreaterThanRole gtr = (GreaterThanRole) gtRestrictions.get(j);
                    if (gtr.name.equals(gtrestriction.name)) {
                        int n = gtrestriction.cardinality;
                        int m = gtr.cardinality;
                        if (n > m) {
                            gtrestr2remove.add(gtr);
                            stop = true;
                        } else if (n < m) {
                            gtrestr2remove.add(gtrestriction);
                        }
                    }
                }
                for (int j = 0; (j < ltRestrictions.size()) && (!stop); j++) {
                    LessThanRole ltr = (LessThanRole) ltRestrictions.get(j);
                    if (ltr.name.equals(gtrestriction.name)) {
                        int n = gtrestriction.cardinality;
                        int m = ltr.cardinality;
                        if (n > m) {
                            for (int k = 0; k < gtRestrictions.size(); k++) {
                                GreaterThanRole gtrestr = (GreaterThanRole) gtRestrictions.get(k);
                                gtrestr2remove.add(gtrestr);
                            }
                            for (int k = 0; k < ltRestrictions.size(); k++) {
                                LessThanRole ltrestr = (LessThanRole) ltRestrictions.get(k);
                                ltrestr2remove.add(ltrestr);
                            }
                            for (int k = 0; k < uvRestrictions.size(); k++) {
                                UniversalRole uvrestr = (UniversalRole) uvRestrictions.get(k);
                                uvrestr2remove.add(uvrestr);
                            }
                            stop = true;
                            empty = true;
                        }
                    }
                }
            }

            for (int i = 0; (i < ltRestrictions.size()) && (!stop); i++) {
                LessThanRole ltrestriction = (LessThanRole) ltRestrictions.get(i);
                for (int j = 0; (j < gtRestrictions.size()) && (!stop); j++) {
                    GreaterThanRole gtr = (GreaterThanRole) gtRestrictions.get(j);
                    if (gtr.name.equals(ltrestriction.name)) {
                        int n = ltrestriction.cardinality;
                        int m = gtr.cardinality;
                        if (n < m) {
                            for (int k = 0; k < gtRestrictions.size(); k++) {
                                GreaterThanRole gtrestr = (GreaterThanRole) gtRestrictions.get(k);
                                gtrestr2remove.add(gtrestr);
                            }
                            for (int k = 0; k < ltRestrictions.size(); k++) {
                                LessThanRole ltrestr = (LessThanRole) ltRestrictions.get(k);
                                ltrestr2remove.add(ltrestr);
                            }
                            for (int k = 0; k < uvRestrictions.size(); k++) {
                                UniversalRole uvrestr = (UniversalRole) uvRestrictions.get(k);
                                uvrestr2remove.add(uvrestr);
                            }
                            stop = true;
                            empty = true;
                        }
                    }
                }
                for (int j = 0; (j < ltRestrictions.size()) && (!stop) && (j != i); j++) {
                    LessThanRole ltr = (LessThanRole) ltRestrictions.get(j);
                    if (ltr.name.equals(ltrestriction.name)) {
                        int n = ltrestriction.cardinality;
                        int m = ltr.cardinality;
                        if (n < m) {
                            ltrestr2remove.add(ltr);
                            stop = true;
                        } else if (n > m) {
                            ltRestrictions.remove(i);
                            stop = true;
                        }
                    }
                }
            }
            for (int i = 0; (i < uvRestrictions.size()) && (!stop); i++) {
                UniversalRole uvrestriction = (UniversalRole) uvRestrictions.get(i);
                for (int j = 0; (j < uvRestrictions.size()) && (!stop) && (j != i); j++) {
                    UniversalRole uvr = (UniversalRole) uvRestrictions.get(j);
                    if (uvr.name.equals(uvrestriction.name)) {
                        UniversalRole newuvRestr = new UniversalRole(uvr.name);
                        newuvRestr.filler.addConcepts(uvr.filler.atomicConcepts);
                        newuvRestr.filler.addConcepts(uvrestriction.filler.atomicConcepts);

                        List<GreaterThanRole> gtrestr2add = new ArrayList(uvr.filler.greaterThanRoles);
                        List<LessThanRole> ltrestr2add = new ArrayList(uvr.filler.lessThanRoles);
                        List<UniversalRole> uvrestr2add = new ArrayList(uvr.filler.universalRoles);
                        for (int k = 0; k < gtrestr2add.size(); k++) {
                            GreaterThanRole next = (GreaterThanRole) gtrestr2add.get(k);
                            newuvRestr.filler.greaterThanRoles.add(next);
                        }
                        for (int k = 0; k < ltrestr2add.size(); k++) {
                            LessThanRole next = (LessThanRole) ltrestr2add.get(k);
                            newuvRestr.filler.lessThanRoles.add(next);
                        }
                        for (int k = 0; k < uvrestr2add.size(); k++) {
                            UniversalRole next = (UniversalRole) uvrestr2add.get(k);
                            newuvRestr.filler.universalRoles.add(next);
                        }

                        gtrestr2add = new ArrayList(uvrestriction.filler.greaterThanRoles);
                        ltrestr2add = new ArrayList(uvrestriction.filler.lessThanRoles);
                        uvrestr2add = new ArrayList(uvrestriction.filler.universalRoles);
                        for (int k = 0; k < gtrestr2add.size(); k++) {
                            GreaterThanRole next = (GreaterThanRole) gtrestr2add.get(k);
                            newuvRestr.filler.greaterThanRoles.add(next);
                        }
                        for (int k = 0; k < ltrestr2add.size(); k++) {
                            LessThanRole next = (LessThanRole) ltrestr2add.get(k);
                            newuvRestr.filler.lessThanRoles.add(next);
                        }
                        for (int k = 0; k < uvrestr2add.size(); k++) {
                            UniversalRole next = (UniversalRole) uvrestr2add.get(k);
                            newuvRestr.filler.universalRoles.add(next);
                        }

                        newuvRestrictions.add(newuvRestr);
                        uvrestr2remove.add(uvr);
                        uvrestr2remove.add(uvrestriction);
                        stop = true;
                    }
                }
            }


            for (int j = 0; j < newgtRestrictions.size(); j++) {
                GreaterThanRole nuova = (GreaterThanRole) newgtRestrictions.get(j);
                gtRestrictions.add(nuova);
            }
            for (int j = 0; j < newltRestrictions.size(); j++) {
                LessThanRole nuova = (LessThanRole) newltRestrictions.get(j);
                ltRestrictions.add(nuova);
            }
            for (int j = 0; j < newuvRestrictions.size(); j++) {
                UniversalRole nuova = (UniversalRole) newuvRestrictions.get(j);
                uvRestrictions.add(nuova);
            }

            for (int j = 0; j < gtrestr2remove.size(); j++) {
                GreaterThanRole rem = (GreaterThanRole) gtrestr2remove.get(j);
                gtRestrictions.remove(rem);
            }
            for (int j = 0; j < ltrestr2remove.size(); j++) {
                LessThanRole rem = (LessThanRole) ltrestr2remove.get(j);
                ltRestrictions.remove(rem);
            }
            for (int j = 0; j < uvrestr2remove.size(); j++) {
                UniversalRole rem = (UniversalRole) uvrestr2remove.get(j);
                uvRestrictions.remove(rem);
            }
        } while (stop);

        for (int j = 0; j < uvRestrictions.size(); j++) {
            UniversalRole uvrestriction = (UniversalRole) uvRestrictions.get(j);
            checkRestrictions(uvrestriction.filler.greaterThanRoles, uvrestriction.filler.lessThanRoles, uvrestriction.filler.universalRoles);
        }
    }


    void newcheckRestrictions(List<AtomicConcept> atomicConcepts, List<GreaterThanRole> gtRestrictions, List<LessThanRole> ltRestrictions, List<UniversalRole> uvRestrictions) {
        boolean empty = false;
        boolean stop;
        do {
            List<GreaterThanRole> newgtRestrictions = new ArrayList();
            List<LessThanRole> newltRestrictions = new ArrayList();
            List<UniversalRole> newuvRestrictions = new ArrayList();
            stop = false;
            List<AtomicConcept> concept2remove = new ArrayList();
            List<GreaterThanRole> gtrestr2remove = new ArrayList();
            List<LessThanRole> ltrestr2remove = new ArrayList();
            List<UniversalRole> uvrestr2remove = new ArrayList();
            if (!empty) {
                for (int i = 0; (i < atomicConcepts.size()) && (!stop); i++) {
                    AtomicConcept atomicConcept = (AtomicConcept) atomicConcepts.get(i);
                    for (int j = 0; (j < atomicConcepts.size()) && (!stop) && (j != i); j++) {
                        AtomicConcept conc = (AtomicConcept) atomicConcepts.get(j);
                        if ((atomicConcept.name.equals(conc.name)) && (atomicConcept.denied != conc.denied)) {
                            for (int k = 0; k < atomicConcepts.size(); k++) {
                                AtomicConcept concetto = (AtomicConcept) atomicConcepts.get(k);
                                concept2remove.add(concetto);
                            }
                            for (int k = 0; k < gtRestrictions.size(); k++) {
                                GreaterThanRole gtrestr = (GreaterThanRole) gtRestrictions.get(k);
                                gtrestr2remove.add(gtrestr);
                            }
                            for (int k = 0; k < ltRestrictions.size(); k++) {
                                LessThanRole ltrestr = (LessThanRole) ltRestrictions.get(k);
                                ltrestr2remove.add(ltrestr);
                            }
                            for (int k = 0; k < uvRestrictions.size(); k++) {
                                UniversalRole uvrestr = (UniversalRole) uvRestrictions.get(k);
                                uvrestr2remove.add(uvrestr);
                            }
                            AtomicConcept bottom = new AtomicConcept();
                            bottom.name = "Nothing";
                            atomicConcepts.add(bottom);
                            stop = true;
                            empty = true;
                        }
                    }
                }
            }

            for (int i = 0; (i < gtRestrictions.size()) && (!stop); i++) {
                GreaterThanRole gtrestriction = (GreaterThanRole) gtRestrictions.get(i);
                for (int j = 0; (j < gtRestrictions.size()) && (!stop) && (j != i); j++) {
                    GreaterThanRole gtr = (GreaterThanRole) gtRestrictions.get(j);
                    if (gtr.name.equals(gtrestriction.name)) {
                        int n = gtrestriction.cardinality;
                        int m = gtr.cardinality;
                        if (n > m) {
                            gtrestr2remove.add(gtr);
                            stop = true;
                        } else if (n < m) {
                            gtrestr2remove.add(gtrestriction);
                            stop = true;
                        }
                    }
                }
                for (int j = 0; (j < ltRestrictions.size()) && (!stop); j++) {
                    LessThanRole ltr = (LessThanRole) ltRestrictions.get(j);
                    if (ltr.name.equals(gtrestriction.name)) {
                        int n = gtrestriction.cardinality;
                        int m = ltr.cardinality;
                        if (n > m) {
                            for (int k = 0; k < atomicConcepts.size(); k++) {
                                AtomicConcept concetto = (AtomicConcept) atomicConcepts.get(k);
                                concept2remove.add(concetto);
                            }
                            for (int k = 0; k < gtRestrictions.size(); k++) {
                                GreaterThanRole gtrestr = (GreaterThanRole) gtRestrictions.get(k);
                                gtrestr2remove.add(gtrestr);
                            }
                            for (int k = 0; k < ltRestrictions.size(); k++) {
                                LessThanRole ltrestr = (LessThanRole) ltRestrictions.get(k);
                                ltrestr2remove.add(ltrestr);
                            }
                            for (int k = 0; k < uvRestrictions.size(); k++) {
                                UniversalRole uvrestr = (UniversalRole) uvRestrictions.get(k);
                                uvrestr2remove.add(uvrestr);
                            }
                            AtomicConcept bottom = new AtomicConcept();
                            bottom.name = "Nothing";
                            atomicConcepts.add(bottom);
                            stop = true;
                        }
                    }
                }
            }

            for (int i = 0; (i < ltRestrictions.size()) && (!stop); i++) {
                LessThanRole ltrestriction = (LessThanRole) ltRestrictions.get(i);
                for (int j = 0; (j < gtRestrictions.size()) && (!stop); j++) {
                    GreaterThanRole gtr = (GreaterThanRole) gtRestrictions.get(j);
                    if (gtr.name.equals(ltrestriction.name)) {
                        int n = ltrestriction.cardinality;
                        int m = gtr.cardinality;
                        if (n < m) {
                            for (int k = 0; k < atomicConcepts.size(); k++) {
                                AtomicConcept concetto = (AtomicConcept) atomicConcepts.get(k);
                                concept2remove.add(concetto);
                            }
                            for (int k = 0; k < gtRestrictions.size(); k++) {
                                GreaterThanRole gtrestr = (GreaterThanRole) gtRestrictions.get(k);
                                gtrestr2remove.add(gtrestr);
                            }
                            for (int k = 0; k < ltRestrictions.size(); k++) {
                                LessThanRole ltrestr = (LessThanRole) ltRestrictions.get(k);
                                ltrestr2remove.add(ltrestr);
                            }
                            for (int k = 0; k < uvRestrictions.size(); k++) {
                                UniversalRole uvrestr = (UniversalRole) uvRestrictions.get(k);
                                uvrestr2remove.add(uvrestr);
                            }
                            AtomicConcept bottom = new AtomicConcept();
                            bottom.name = "Nothing";
                            atomicConcepts.add(bottom);
                            stop = true;
                        }
                    }
                }
                for (int j = 0; (j < ltRestrictions.size()) && (!stop) && (j != i); j++) {
                    LessThanRole ltr = (LessThanRole) ltRestrictions.get(j);
                    if (ltr.name.equals(ltrestriction.name)) {
                        int n = ltrestriction.cardinality;
                        int m = ltr.cardinality;
                        if (n < m) {
                            ltrestr2remove.add(ltr);
                            stop = true;
                        } else if (n > m) {
                            ltRestrictions.remove(i);
                            stop = true;
                        }
                    }
                }
            }
            for (int i = 0; (i < uvRestrictions.size()) && (!stop); i++) {
                UniversalRole uvrestriction = (UniversalRole) uvRestrictions.get(i);
                for (int j = 0; (j < uvRestrictions.size()) && (!stop) && (j != i); j++) {
                    UniversalRole uvr = (UniversalRole) uvRestrictions.get(j);
                    if (uvr.name.equals(uvrestriction.name)) {
                        UniversalRole newuvRestr = new UniversalRole(uvr.name);
                        newuvRestr.filler.addConcepts(uvr.filler.atomicConcepts);
                        newuvRestr.filler.addConcepts(uvrestriction.filler.atomicConcepts);

                        List<GreaterThanRole> gtrestr2add = new ArrayList(uvr.filler.greaterThanRoles);
                        List<LessThanRole> ltrestr2add = new ArrayList(uvr.filler.lessThanRoles);
                        List<UniversalRole> uvrestr2add = new ArrayList(uvr.filler.universalRoles);
                        for (int k = 0; k < gtrestr2add.size(); k++) {
                            GreaterThanRole next = (GreaterThanRole) gtrestr2add.get(k);
                            newuvRestr.filler.greaterThanRoles.add(next);
                        }
                        for (int k = 0; k < ltrestr2add.size(); k++) {
                            LessThanRole next = (LessThanRole) ltrestr2add.get(k);
                            newuvRestr.filler.lessThanRoles.add(next);
                        }
                        for (int k = 0; k < uvrestr2add.size(); k++) {
                            UniversalRole next = (UniversalRole) uvrestr2add.get(k);
                            newuvRestr.filler.universalRoles.add(next);
                        }

                        gtrestr2add = new ArrayList(uvrestriction.filler.greaterThanRoles);
                        ltrestr2add = new ArrayList(uvrestriction.filler.lessThanRoles);
                        uvrestr2add = new ArrayList(uvrestriction.filler.universalRoles);
                        for (int k = 0; k < gtrestr2add.size(); k++) {
                            GreaterThanRole next = (GreaterThanRole) gtrestr2add.get(k);
                            newuvRestr.filler.greaterThanRoles.add(next);
                        }
                        for (int k = 0; k < ltrestr2add.size(); k++) {
                            LessThanRole next = (LessThanRole) ltrestr2add.get(k);
                            newuvRestr.filler.lessThanRoles.add(next);
                        }
                        for (int k = 0; k < uvrestr2add.size(); k++) {
                            UniversalRole next = (UniversalRole) uvrestr2add.get(k);
                            newuvRestr.filler.universalRoles.add(next);
                        }

                        newuvRestrictions.add(newuvRestr);
                        uvrestr2remove.add(uvr);
                        uvrestr2remove.add(uvrestriction);
                        stop = true;
                    }
                }
            }


            for (int j = 0; j < newgtRestrictions.size(); j++) {
                GreaterThanRole nuova = (GreaterThanRole) newgtRestrictions.get(j);
                gtRestrictions.add(nuova);
            }
            for (int j = 0; j < newltRestrictions.size(); j++) {
                LessThanRole nuova = (LessThanRole) newltRestrictions.get(j);
                ltRestrictions.add(nuova);
            }
            for (int j = 0; j < newuvRestrictions.size(); j++) {
                UniversalRole nuova = (UniversalRole) newuvRestrictions.get(j);
                uvRestrictions.add(nuova);
            }

            for (int j = 0; j < concept2remove.size(); j++) {
                AtomicConcept rem = (AtomicConcept) concept2remove.get(j);
                atomicConcepts.remove(rem);
            }
            for (int j = 0; j < gtrestr2remove.size(); j++) {
                GreaterThanRole rem = (GreaterThanRole) gtrestr2remove.get(j);
                gtRestrictions.remove(rem);
            }
            for (int j = 0; j < ltrestr2remove.size(); j++) {
                LessThanRole rem = (LessThanRole) ltrestr2remove.get(j);
                ltRestrictions.remove(rem);
            }
            for (int j = 0; j < uvrestr2remove.size(); j++) {
                UniversalRole rem = (UniversalRole) uvrestr2remove.get(j);
                uvRestrictions.remove(rem);
            }
        } while (stop);

        for (int j = 0; j < uvRestrictions.size(); j++) {
            UniversalRole uvrestriction = (UniversalRole) uvRestrictions.get(j);
            newcheckRestrictions(uvrestriction.filler.atomicConcepts, uvrestriction.filler.greaterThanRoles, uvrestriction.filler.lessThanRoles, uvrestriction.filler.universalRoles);
            if ((uvrestriction.filler.atomicConcepts.size() > 0) && (((AtomicConcept) uvrestriction.filler.atomicConcepts.get(0)).name.equals("Nothing"))) {
                LessThanRole less = new LessThanRole();
                less.name = uvrestriction.name;
                less.cardinality = 0;
                ltRestrictions.add(less);

                for (int i = 0; i < gtRestrictions.size(); i++) {
                    GreaterThanRole gtrole = (GreaterThanRole) gtRestrictions.get(i);
                    if ((gtrole.name.equals(less.name)) && (gtrole.cardinality >= 1)) {
                        List<AtomicConcept> concept2remove = new ArrayList();
                        List<GreaterThanRole> gtrestr2remove = new ArrayList();
                        List<LessThanRole> ltrestr2remove = new ArrayList();
                        List<UniversalRole> uvrestr2remove = new ArrayList();
                        for (int k = 0; k < atomicConcepts.size(); k++) {
                            AtomicConcept concetto = (AtomicConcept) atomicConcepts.get(k);
                            concept2remove.add(concetto);
                        }
                        for (int k = 0; k < gtRestrictions.size(); k++) {
                            GreaterThanRole gtrestr = (GreaterThanRole) gtRestrictions.get(k);
                            gtrestr2remove.add(gtrestr);
                        }
                        for (int k = 0; k < ltRestrictions.size(); k++) {
                            LessThanRole ltrestr = (LessThanRole) ltRestrictions.get(k);
                            ltrestr2remove.add(ltrestr);
                        }
                        for (int k = 0; k < uvRestrictions.size(); k++) {
                            UniversalRole uvrestr = (UniversalRole) uvRestrictions.get(k);
                            uvrestr2remove.add(uvrestr);
                        }

                        for (int k = 0; k < concept2remove.size(); k++) {
                            AtomicConcept rem = (AtomicConcept) concept2remove.get(k);
                            atomicConcepts.remove(rem);
                        }
                        for (int k = 0; k < gtrestr2remove.size(); k++) {
                            GreaterThanRole rem = (GreaterThanRole) gtrestr2remove.get(k);
                            gtRestrictions.remove(rem);
                        }
                        for (int k = 0; k < ltrestr2remove.size(); k++) {
                            LessThanRole rem = (LessThanRole) ltrestr2remove.get(k);
                            ltRestrictions.remove(rem);
                        }
                        for (int k = 0; k < uvrestr2remove.size(); k++) {
                            UniversalRole rem = (UniversalRole) uvrestr2remove.get(k);
                            uvRestrictions.remove(rem);
                        }

                        AtomicConcept bottom = new AtomicConcept();
                        bottom.name = "Nothing";
                        atomicConcepts.add(bottom);
                    }
                }
            }
        }
    }


    void unfoldRestriction(UniversalRole r, List<AtomicConcept> newConcepts, List<GreaterThanRole> newgtRestrictions, List<LessThanRole> newltRestrictions, List<UniversalRole> newuvRestrictions, List<AtomicConcept> newComplements, List<AtomicConcept> concepts2remove, List<AtomicConcept> atomicConcepts, Item item, List<GreaterThanRole> newgtItemRestrictions, List<LessThanRole> newltItemRestrictions, List<UniversalRole> newuvItemRestrictions) {
        newConcepts = new ArrayList();
        newgtRestrictions = new ArrayList();
        newltRestrictions = new ArrayList();
        newuvRestrictions = new ArrayList();
        newComplements = new ArrayList();
        concepts2remove = new ArrayList();
        atomicConcepts = new ArrayList(r.filler.getConcepts());
        List<AtomicConcept> concetti = new ArrayList(r.filler.getConcepts());
        for (int i = 0; i < concetti.size(); i++) {
            unfoldConcept((AtomicConcept) concetti.get(i), newConcepts,
                    newgtRestrictions, newltRestrictions, newuvRestrictions,
                    newComplements, concepts2remove, atomicConcepts, item,
                    r.filler.greaterThanRoles, r.filler.lessThanRoles,
                    r.filler.universalRoles, r.filler.getComplements());
        }
        List<AtomicConcept> con = (ArrayList) r.filler.getConcepts();
        List<GreaterThanRole> gtres = (ArrayList) r.filler.greaterThanRoles;
        List<LessThanRole> ltres = (ArrayList) r.filler.lessThanRoles;
        List<UniversalRole> uvres = (ArrayList) r.filler.universalRoles;
        List<AtomicConcept> compl = (ArrayList) r.filler.getComplements();

        for (int i = 0; i < newConcepts.size(); i++) {
            AtomicConcept c = (AtomicConcept) newConcepts.get(i);
            con.add(c);
            r.filler.atomicConcepts.add(c);
        }
        for (int j = 0; j < newgtRestrictions.size(); j++) {
            GreaterThanRole re = (GreaterThanRole) newgtRestrictions.get(j);
            gtres.add(re);
        }
        for (int j = 0; j < newltRestrictions.size(); j++) {
            LessThanRole re = (LessThanRole) newltRestrictions.get(j);
            ltres.add(re);
        }
        for (int j = 0; j < newuvRestrictions.size(); j++) {
            UniversalRole re = (UniversalRole) newuvRestrictions.get(j);
            uvres.add(re);
        }
        for (int i = 0; i < newComplements.size(); i++) {
            AtomicConcept c = (AtomicConcept) newComplements.get(i);
            compl.add(c);
            r.filler.atomicConcepts.add(c);
        }

        List<GreaterThanRole> newgtCardRestr = new ArrayList();
        List<LessThanRole> newltCardRestr = new ArrayList();
        List<UniversalRole> newuvCardRestr = new ArrayList();

        for (int i = 0; i < uvres.size(); i++) {
            UniversalRole c = (UniversalRole) uvres.get(i);
            unfoldRestriction(c, newConcepts, newgtRestrictions,
                    newltRestrictions, newuvRestrictions, newComplements,
                    concepts2remove, atomicConcepts, item, newgtCardRestr,
                    newltCardRestr, newuvCardRestr);
        }
        for (int i = 0; i < newgtCardRestr.size(); i++) {
            GreaterThanRole c = (GreaterThanRole) newgtCardRestr.get(i);
            gtres.add(c);
        }
        for (int i = 0; i < newltCardRestr.size(); i++) {
            LessThanRole c = (LessThanRole) newltCardRestr.get(i);
            ltres.add(c);
        }
        for (int i = 0; i < newuvCardRestr.size(); i++) {
            UniversalRole c = (UniversalRole) newuvCardRestr.get(i);
            uvres.add(c);
        }
    }


    void unfoldConcept(AtomicConcept atomicConcept, List<AtomicConcept> newConcepts, List<GreaterThanRole> newgtRestrictions, List<LessThanRole> newltRestrictions, List<UniversalRole> newuvRestrictions, List<AtomicConcept> newComplements, List<AtomicConcept> concepts2remove, List<AtomicConcept> atomicConcepts, Item item, List<GreaterThanRole> gtRestrictions, List<LessThanRole> ltRestrictions, List<UniversalRole> uvRestrictions, List<AtomicConcept> complements) {
        Item conc = (Item) this.ontology.get(atomicConcept.name);

        if (conc != null) {
            List<AtomicConcept> subclassOf = new ArrayList(conc.description.getConcepts());
            List<GreaterThanRole> gtrestrictions = new ArrayList(conc.description.greaterThanRoles);
            List<LessThanRole> ltrestrictions = new ArrayList(conc.description.lessThanRoles);
            List<UniversalRole> uvrestrictions = new ArrayList(conc.description.universalRoles);
            if (!conc.equivalent) {
                for (int j = 0; j < subclassOf.size(); j++) {
                    AtomicConcept sub = (AtomicConcept) subclassOf.get(j);
                    if ((!sub.name.contains("Thing")) &&
                            (!newConcepts.contains(sub)) && (!atomicConcepts.contains(sub))) {
                        newConcepts.add(sub);
                        unfoldConcept(sub, newConcepts, newgtRestrictions,
                                newltRestrictions, newuvRestrictions,
                                newComplements, concepts2remove, atomicConcepts,
                                item, gtRestrictions, ltRestrictions,
                                uvRestrictions, complements);
                    }
                }

                for (int j = 0; j < gtrestrictions.size(); j++) {
                    GreaterThanRole r = (GreaterThanRole) gtrestrictions.get(j);
                    boolean b = false;
                    boolean end = false;
                    for (int i = 0; (i < newgtRestrictions.size()) && (!end); i++) {
                        if (((GreaterThanRole) newgtRestrictions.get(i)).isTheSame(r)) {
                            b = true;
                            end = true;
                        }
                    }
                    end = false;
                    boolean d = false;
                    for (int i = 0; (i < gtRestrictions.size()) && (!end); i++) {
                        if (((GreaterThanRole) gtRestrictions.get(i)).isTheSame(r)) {
                            d = true;
                            end = true;
                        }
                    }
                    if ((!b) && (!d))
                        newgtRestrictions.add(r);
                }
                for (int j = 0; j < ltrestrictions.size(); j++) {
                    LessThanRole r = (LessThanRole) ltrestrictions.get(j);
                    boolean b = false;
                    boolean end = false;
                    for (int i = 0; (i < newltRestrictions.size()) && (!end); i++) {
                        if (((LessThanRole) newltRestrictions.get(i)).isTheSame(r)) {
                            b = true;
                            end = true;
                        }
                    }
                    end = false;
                    boolean d = false;
                    for (int i = 0; (i < ltRestrictions.size()) && (!end); i++) {
                        if (((LessThanRole) ltRestrictions.get(i)).isTheSame(r)) {
                            d = true;
                            end = true;
                        }
                    }
                    if ((!b) && (!d))
                        newltRestrictions.add(r);
                }
                for (int j = 0; j < uvrestrictions.size(); j++) {
                    UniversalRole r = (UniversalRole) uvrestrictions.get(j);
                    boolean b = false;
                    boolean end = false;
                    for (int i = 0; (i < newuvRestrictions.size()) && (!end); i++) {
                        if (((UniversalRole) newuvRestrictions.get(i)).isTheSame(r)) {
                            b = true;
                            end = true;
                        }
                    }
                    end = false;
                    boolean d = false;
                    for (int i = 0; (i < uvRestrictions.size()) && (!end); i++) {
                        if (((UniversalRole) uvRestrictions.get(i)).isTheSame(r)) {
                            d = true;
                            end = true;
                        }
                    }
                    if ((!b) && (!d)) {
                        newuvRestrictions.add(r);
                    }
                }
                List<AtomicConcept> disj = new ArrayList(conc.description.getComplements());
                for (int j = 0; j < disj.size(); j++) {
                    AtomicConcept comp = (AtomicConcept) disj.get(j);
                    if ((!newComplements.contains(comp)) &&
                            (!complements.contains(comp))) {
                        newComplements.add(comp);
                    }
                }
            } else {
                for (int j = 0; j < subclassOf.size(); j++) {
                    AtomicConcept c = (AtomicConcept) subclassOf.get(j);
                    newConcepts.add(c);
                    unfoldConcept(c, newConcepts, newgtRestrictions,
                            newltRestrictions, newuvRestrictions,
                            newComplements, concepts2remove, atomicConcepts, item,
                            gtRestrictions, ltRestrictions, uvRestrictions,
                            complements);
                }
                for (int j = 0; j < gtrestrictions.size(); j++) {
                    GreaterThanRole c = (GreaterThanRole) gtrestrictions.get(j);
                    boolean b = false;
                    for (int i = 0; i < newgtRestrictions.size(); i++) {
                        b = (b) || (((GreaterThanRole) newgtRestrictions.get(i)).isTheSame(c));
                    }
                    boolean d = false;
                    for (int i = 0; i < gtRestrictions.size(); i++) {
                        d = (d) || (((GreaterThanRole) gtRestrictions.get(i)).isTheSame(c));
                    }
                    if ((!b) && (!d))
                        newgtRestrictions.add(c);
                }
                for (int j = 0; j < ltrestrictions.size(); j++) {
                    LessThanRole c = (LessThanRole) ltrestrictions.get(j);
                    boolean b = false;
                    for (int i = 0; i < newltRestrictions.size(); i++) {
                        b = (b) || (((LessThanRole) newltRestrictions.get(i)).isTheSame(c));
                    }
                    boolean d = false;
                    for (int i = 0; i < ltRestrictions.size(); i++) {
                        d = (d) || (((LessThanRole) ltRestrictions.get(i)).isTheSame(c));
                    }
                    if ((!b) && (!d))
                        newltRestrictions.add(c);
                }
                for (int j = 0; j < uvrestrictions.size(); j++) {
                    UniversalRole c = (UniversalRole) uvrestrictions.get(j);
                    boolean b = false;
                    for (int i = 0; i < newuvRestrictions.size(); i++) {
                        b = (b) || (((UniversalRole) newuvRestrictions.get(i)).isTheSame(c));
                    }
                    boolean d = false;
                    for (int i = 0; i < uvRestrictions.size(); i++) {
                        d = (d) || (((UniversalRole) uvRestrictions.get(i)).isTheSame(c));
                    }
                    if ((!b) && (!d))
                        newuvRestrictions.add(c);
                }
                for (int j = 0; j < subclassOf.size(); j++) {
                    AtomicConcept sub = (AtomicConcept) subclassOf.get(j);
                    if ((!sub.name.contains("Thing")) &&
                            (!newConcepts.contains(sub)) &&
                            (!atomicConcepts.contains(sub))) {
                        newConcepts.add(sub);
                        unfoldConcept(sub, newConcepts, newgtRestrictions,
                                newltRestrictions, newuvRestrictions,
                                newComplements, concepts2remove, atomicConcepts,
                                item, gtRestrictions, ltRestrictions,
                                uvRestrictions, complements);
                    }
                }


                List<AtomicConcept> disj = new ArrayList(conc.description.getComplements());
                for (int j = 0; j < disj.size(); j++) {
                    AtomicConcept comp = (AtomicConcept) disj.get(j);
                    if ((!newComplements.contains(comp)) &&
                            (!complements.contains(comp))) {
                        newComplements.add(comp);
                    }
                }
                concepts2remove.add(atomicConcept);
            }
        }
    }

    public void item2xml(Item item, PrintWriter out) {
        out.println("<I name=\"" + item.name + "\">");
        if ((!item.description.getConcepts().isEmpty()) || (!item.description.getComplements().isEmpty())) {
            out.println("<Cs>");
            printConcepts(item.description.getConcepts(), item.description.getComplements(), out);
            out.println("</Cs>");
        }

        if (item.description.lessThanRoles.size() != 0) {
            out.println("<LTRs>");
            printLTRs(item.description.lessThanRoles, out);
            out.println("</LTRs>");
        }

        if (item.description.greaterThanRoles.size() != 0) {
            out.println("<GTRs>");
            printGTRs(item.description.greaterThanRoles, out);
            out.println("</GTRs>");
        }

        if (item.description.universalRoles.size() != 0) {
            out.println("<UTRs>");
            printURs(item.description.universalRoles, out);
            out.println("</UTRs>");
        }

        out.println("</I>");
    }

    void printConcepts(List<AtomicConcept> atomicConcepts, List<AtomicConcept> complements, PrintWriter out) {
        for (int i = 0; i < atomicConcepts.size(); i++) {
            out.println("<C id=\"" + ((AtomicConcept) atomicConcepts.get(i)).name + "\" />");
        }
        for (int i = 0; i < complements.size(); i++) {
            out.println("<C id=\"" + ((AtomicConcept) complements.get(i)).name + "\" deny=\"true\" />");
        }
    }

    void printLTRs(List<LessThanRole> restrictions, PrintWriter out) {
        for (int i = 0; i < restrictions.size(); i++) {
            LessThanRole r = (LessThanRole) restrictions.get(i);
            out.println("<LTR id=\"" + r.name + "\" n=\"" + r.cardinality + "\" />");
        }
    }

    void printGTRs(List<GreaterThanRole> restrictions, PrintWriter out) {
        for (int i = 0; i < restrictions.size(); i++) {
            GreaterThanRole r = (GreaterThanRole) restrictions.get(i);
            out.println("<GTR id=\"" + r.name + "\" n=\"" + r.cardinality + "\" />");
        }
    }


    void printURs(List<UniversalRole> restrictions, PrintWriter out) {
        for (int i = 0; i < restrictions.size(); i++) {
            UniversalRole r = (UniversalRole) restrictions.get(i);
            out.println("<UR id=\"" + r.name + "\">");
            if ((!r.filler.getConcepts().isEmpty()) || (!r.filler.getComplements().isEmpty())) {
                out.println("<Cs>");
                printConcepts(r.filler.getConcepts(), r.filler.getComplements(), out);
                out.println("</Cs>");
            }

            if (r.filler.lessThanRoles.size() != 0) {
                out.println("<LTRs>");
                printLTRs(r.filler.lessThanRoles, out);
                out.println("</LTRs>");
            }

            if (r.filler.greaterThanRoles.size() != 0) {
                out.println("<GTRs>");
                printGTRs(r.filler.greaterThanRoles, out);
                out.println("</GTRs>");
            }

            if (r.filler.universalRoles.size() != 0) {
                out.println("<UTRs>");
                printURs(r.filler.universalRoles, out);
                out.println("</UTRs>");
            }

            out.println("</UR>");
        }
    }

    public boolean newcheckConsistency(SemanticDescription description) {
        if (description.atomicConcepts != null) {
            for (AtomicConcept atomicConcept : description.atomicConcepts) {
                if (atomicConcept.name.equals("Nothing")) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isConsistent(HashMap<String, Item> ontology) {
        Set<String> list1 = ontology.keySet();
        Iterator<String> iter1 = list1.iterator();

        while (iter1.hasNext()) {
            String key1 = (String) iter1.next();
            AtomicConcept concetto1 = new AtomicConcept();
            concetto1.name = key1;
            Item item1 = null;

            if (this.concetti_norm.containsKey(key1)) {
                item1 = (Item) this.concetti_norm.get(key1);
            } else {
                AtomicConcept concettoC = new AtomicConcept();
                concettoC.name = key1;

                item1 = new Item(key1);
                item1.description.addConcept(concettoC);
                normalizzaItem(item1);
                this.concetti_norm.put(key1, item1);
            }

            if (!newcheckConsistency(item1.description)) {
                return false;
            }
        }

        return true;
    }

    public List<Nodo> topSearch(Nodo conceptC, Nodo conceptX) {
        List<Nodo> predecessoriC = new ArrayList();
        Nodo x = (Nodo) this.tassonomia.get(Integer.valueOf(conceptX.getName()));
        if (!this.visitatiTop.containsKey(Integer.valueOf(conceptX.getName()))) {
            this.visitatiTop.put(Integer.valueOf(conceptX.getName()), Boolean.valueOf(true));
        }
        List<Nodo> successoriX = x.getSuccessors();
        List<Nodo> posSucc = new ArrayList();
        for (int i = 0; i < successoriX.size(); i++) {
            Nodo y = (Nodo) successoriX.get(i);
            if (subsumes(y, conceptC)) {
                posSucc.add(y);
            }
        }

        if (posSucc.size() == 0) {
            predecessoriC.add(conceptX);
        } else {
            for (int i = 0; i < posSucc.size(); i++) {
                Nodo y = (Nodo) posSucc.get(i);
                if (!this.visitatiTop.containsKey(Integer.valueOf(y.getName()))) {
                    List<Nodo> result = topSearch(conceptC, y);
                    predecessoriC.addAll(result);
                }
            }
        }

        return predecessoriC;
    }

    public List<Nodo> enanchedTopSearch(Nodo conceptC, Nodo conceptX) {
        List<Nodo> predecessoriC = new ArrayList();
        Nodo x = (Nodo) this.tassonomia.get(Integer.valueOf(conceptX.getName()));
        if (!this.visitatiTop.containsKey(Integer.valueOf(conceptX.getName()))) {
            this.visitatiTop.put(Integer.valueOf(conceptX.getName()), Boolean.valueOf(true));
        }
        List<Nodo> successoriX = x.getSuccessors();
        List<Nodo> posSucc = new ArrayList();
        for (int i = 0; i < successoriX.size(); i++) {
            Nodo y = (Nodo) successoriX.get(i);
            if (enhancedTopSubsumes(y, conceptC)) {
                posSucc.add(y);
            }
        }

        if (posSucc.size() == 0) {
            predecessoriC.add(conceptX);
        } else {
            for (int i = 0; i < posSucc.size(); i++) {
                Nodo y = (Nodo) posSucc.get(i);
                if (!this.visitatiTop.containsKey(Integer.valueOf(y.getName()))) {
                    List<Nodo> result = enanchedTopSearch(conceptC, y);
                    predecessoriC.addAll(result);
                }
            }
        }

        return predecessoriC;
    }

    public List<Nodo> bottomSearch(Nodo conceptC, Nodo conceptX) {
        List<Nodo> successoriC = new ArrayList();
        Nodo x = (Nodo) this.tassonomia.get(Integer.valueOf(conceptX.getName()));
        if (!this.visitatiBottom.containsKey(Integer.valueOf(conceptX.getName()))) {
            this.visitatiBottom.put(Integer.valueOf(conceptX.getName()), Boolean.valueOf(true));
        }
        List<Nodo> predecessoriX = x.getPredecessors();
        List<Nodo> posPred = new ArrayList();
        for (int i = 0; i < predecessoriX.size(); i++) {
            Nodo y = (Nodo) predecessoriX.get(i);
            if (subsumes(conceptC, y)) {
                posPred.add(y);
            }
        }
        if (posPred.size() == 0) {
            successoriC.add(conceptX);
        } else {
            for (int i = 0; i < posPred.size(); i++) {
                Nodo y = (Nodo) posPred.get(i);
                if (!this.visitatiBottom.containsKey(Integer.valueOf(y.getName()))) {
                    List<Nodo> result = bottomSearch(conceptC, y);
                    successoriC.addAll(result);
                }
            }
        }
        return successoriC;
    }

    public List<Nodo> enanchedBottomSearch(Nodo conceptC, Nodo conceptX, List<Nodo> successori) {
        List<Nodo> successoriC = new ArrayList();
        Nodo x = (Nodo) this.tassonomia.get(Integer.valueOf(conceptX.getName()));
        if (!this.visitatiBottom.containsKey(Integer.valueOf(conceptX.getName()))) {
            this.visitatiBottom.put(Integer.valueOf(conceptX.getName()), Boolean.valueOf(true));
        }
        List<Nodo> predecessoriX = x.getPredecessors();
        List<Nodo> posPred = new ArrayList();
        for (int i = 0; i < predecessoriX.size(); i++) {
            Nodo y = (Nodo) predecessoriX.get(i);
            if (enanchedBottomSubsumes(conceptC, y, successori)) {
                posPred.add(y);
            }
        }
        if (posPred.size() == 0) {
            successoriC.add(conceptX);
        } else {
            for (int i = 0; i < posPred.size(); i++) {
                Nodo y = (Nodo) posPred.get(i);
                if (!this.visitatiBottom.containsKey(Integer.valueOf(y.getName()))) {
                    List<Nodo> result = enanchedBottomSearch(conceptC, y, successori);
                    successoriC.addAll(result);
                }
            }
        }
        return successoriC;
    }

    public boolean subsumes(Nodo y, Nodo c) {
        boolean result = false;
        String stringaY = (String) this.atomics.get(Integer.valueOf(y.getName()));
        String stringaC = (String) this.atomics.get(Integer.valueOf(c.getName()));
        if ((stringaY.equals("Thing")) || (stringaC.equals("Nothing")))
            return true;
        if ((stringaY.equals("Nothing")) || (stringaC.equals("Thing")))
            return false;
        if ((isSubsumed(y, c)) || (checkToldSubsume(stringaY, stringaC, y.getName(), c.getName())))
            return true;
        if (isNotSubsumed(y, c)) {
            return false;
        }
        this.numConfrontiSussunzione += 1;
        result = computeSubsumption(y, c);

        return result;
    }

    private boolean checkToldSubsume(String stringaY, String stringaC, int nameY, int nameC) {
        Item itemC = (Item) this.ontology.get(stringaC);
        AtomicConcept concettoY = new AtomicConcept();
        concettoY.name = stringaY;
        concettoY.denied = false;
        if (itemC.description.getConcepts().contains(concettoY)) {
            this.subsumes.setQuick(nameY, nameC, Boolean.valueOf(true));
            return true;
        }
        return false;
    }

    public boolean enhancedTopSubsumes(Nodo y, Nodo c) {
        boolean result = false;
        String stringaY = (String) this.atomics.get(Integer.valueOf(y.getName()));
        String stringaC = (String) this.atomics.get(Integer.valueOf(c.getName()));
        if ((stringaY.equals("Thing")) || (stringaC.equals("Nothing")))
            return true;
        if ((stringaY.equals("Nothing")) || (stringaC.equals("Thing")))
            return false;
        if (isSubsumed(y, c))
            return true;
        if (isNotSubsumed(y, c)) {
            return false;
        }
        boolean checkPred = true;
        List<Nodo> pred = y.getPredecessors();
        for (int i = 0; i < pred.size(); i++) {
            checkPred = (checkPred) && (enhancedTopSubsumes((Nodo) pred.get(i), c));
        }
        if (checkPred) {
            this.numConfrontiSussunzione += 1;
            result = computeSubsumption(y, c);
        } else {
            this.subsumes.setQuick(y.getName(), c.getName(), Boolean.valueOf(false));
            result = false;
        }

        return result;
    }

    public boolean enanchedBottomSubsumes(Nodo y, Nodo c, List<Nodo> successori) {
        boolean result = false;
        String stringaY = (String) this.atomics.get(Integer.valueOf(y.getName()));
        String stringaC = (String) this.atomics.get(Integer.valueOf(c.getName()));
        if ((stringaY.equals("Thing")) || (stringaC.equals("Nothing")))
            return true;
        if ((stringaY.equals("Nothing")) || (stringaC.equals("Thing")))
            return false;
        if (isSubsumed(y, c))
            return true;
        if ((isNotSubsumed(y, c)) || (!isASuccessor(c, successori))) {
            return false;
        }
        this.numConfrontiSussunzione += 1;
        result = computeSubsumption(y, c);

        return result;
    }

    public boolean isASuccessor(Nodo c, List<Nodo> successori) {
        boolean result = true;
        for (int i = 0; i < successori.size(); i++) {
            Nodo node = (Nodo) successori.get(i);
            if (node.getName() == 0) {
                return true;
            }
            boolean succ = c.getPredecessors().contains(node);
            if (!succ) {
                result = false;
                for (int k = 0; k < c.getPredecessors().size(); k++) {
                    result = (result) || (successor((Nodo) c.getPredecessors().get(k), node));
                }
            } else {
                result = result;
            }

            if (!result) {
                return false;
            }
        }
        return result;
    }

    public void checkToldSubsumers(List<Integer> toldSubsumers) {
        List<Integer> subsumersToRemove = new ArrayList();
        for (int i = 0; i < toldSubsumers.size(); i++) {
            Integer sub1 = (Integer) toldSubsumers.get(i);
            for (int j = 0; j < toldSubsumers.size(); j++) {
                if (i != j) {
                    Integer sub2 = (Integer) toldSubsumers.get(j);
                    if (this.subsumes.getQuick(sub2.intValue(), sub1.intValue()) != null) {
                        if (((Boolean) this.subsumes.getQuick(sub2.intValue(), sub1.intValue())).booleanValue()) {
                            subsumersToRemove.add(sub2);
                        }
                    } else if (computeSubsumption((Nodo) this.tassonomia.get(sub2), (Nodo) this.tassonomia.get(sub1))) {
                        subsumersToRemove.add(sub2);
                    }
                }
            }
        }

        for (int i = 0; i < subsumersToRemove.size(); i++) {
            toldSubsumers.remove(subsumersToRemove.get(i));
        }
    }

    public boolean successor(Nodo c, Nodo y) {
        boolean result = false;
        if (c.getPredecessors().contains(y)) {
            return true;
        }
        for (int i = 0; i < c.getPredecessors().size(); i++) {
            result = (result) || (successor((Nodo) c.getPredecessors().get(i), y));
        }

        return result;
    }

    public void classifyInclusions() {
        this.visitatiTop = new HashMap();
        this.visitatiBottom = new HashMap();
        Nodo thing = new Nodo(0);
        Nodo bottom = new Nodo(1);
        thing.getSuccessors().add(bottom);
        bottom.getPredecessors().add(thing);
        this.tassonomia.put(Integer.valueOf(0), thing);
        this.tassonomia.put(Integer.valueOf(1), bottom);
        for (int i = 0; i < this.atomics.size(); i++) {
            String c = (String) this.atomics.get(Integer.valueOf(i));
            Item itemC = (Item) this.ontology.get(c);
            if ((!c.endsWith("Thing")) && (!c.contains("Nothing")) && (!itemC.equivalent)) {
                this.visitatiTop.clear();
                this.visitatiBottom.clear();
                Nodo nodoC = new Nodo(i);
                this.tassonomia.put(Integer.valueOf(i), nodoC);
                List<Nodo> predecessori = null;
                if (hasNoToldSubsumers((Item) this.ontology.get(c))) {
                    predecessori = new ArrayList();
                    predecessori.add(thing);
                } else {
                    List<Integer> toldSubsumers = getToldSubsumers(c);
                    if (areProcessed(toldSubsumers)) {
                        if (toldSubsumers.size() > 1) {
                            checkToldSubsumers(toldSubsumers);
                        }
                        predecessori = new ArrayList();
                        for (int j = 0; j < toldSubsumers.size(); j++) {
                            predecessori.add((Nodo) this.tassonomia.get(toldSubsumers.get(j)));
                            this.subsumes.setQuick(((Integer) toldSubsumers.get(j)).intValue(), nodoC.getName(), Boolean.valueOf(true));
                        }
                    } else {
                        predecessori = enanchedTopSearch(nodoC, thing);
                    }
                }
                List<Nodo> successori = null;
                if (((Integer) this.primitives.get(c)).intValue() != 0) {
                    successori = enanchedBottomSearch(nodoC, bottom, predecessori);
                } else {
                    successori = new ArrayList();
                    successori.add(bottom);
                }
                addLink(nodoC, predecessori, successori);
                removeLink(predecessori, successori);
            }
        }
    }

    public void classifyDefined() {
        this.visitatiTop = new HashMap();
        this.visitatiBottom = new HashMap();
        Nodo thing = (Nodo) this.tassonomia.get(Integer.valueOf(0));
        Nodo bottom = (Nodo) this.tassonomia.get(Integer.valueOf(1));
        for (int i = 0; i < this.defClasses.size(); i++) {
            String c = (String) this.defClasses.get(i);
            if ((!c.contains("Thing")) && (!c.contains("Nothing"))) {
                this.visitatiTop.clear();
                this.visitatiBottom.clear();
                int key = ((Integer) this.invAtomics.get(c)).intValue();
                Nodo nodoC = new Nodo(key);
                this.tassonomia.put(Integer.valueOf(key), nodoC);
                List<Nodo> predecessori = enanchedTopSearch(nodoC, thing);
                List<Nodo> successori = null;
                if ((predecessori.size() == 1) && (((Nodo) predecessori.get(0)).getName() != 0)) {
                    Nodo nodoPredec = (Nodo) predecessori.get(0);
                    this.numConfrontiSussunzione += 1;
                    if (computeSubsumption(nodoC, nodoPredec)) {
                        nodoC.getEquivalents().add(nodoPredec);
                        nodoPredec.getEquivalents().add(nodoC);
                        addLink(nodoC, nodoPredec.getPredecessors(), nodoPredec.getSuccessors());
                    } else {
                        successori = enanchedBottomSearch(nodoC, bottom, predecessori);
                        addLink(nodoC, predecessori, successori);
                        removeLink(predecessori, successori);
                    }
                } else {
                    successori = enanchedBottomSearch(nodoC, bottom, predecessori);
                    addLink(nodoC, predecessori, successori);
                    removeLink(predecessori, successori);
                }
            }
        }
    }

    private boolean areProcessed(List<Integer> toldSubsumers) {
        boolean result = true;
        for (int i = 0; i < toldSubsumers.size(); i++) {
            if (!this.tassonomia.containsKey(toldSubsumers.get(i))) {
                return false;
            }
        }
        return result;
    }

    private List<Integer> getToldSubsumers(String c) {
        List<Integer> subsumers = new ArrayList();
        Item concept = (Item) this.ontology.get(c);
        List<AtomicConcept> sub = new ArrayList(concept.description.getConcepts());
        for (int i = 0; i < sub.size(); i++) {
            AtomicConcept con = (AtomicConcept) sub.get(i);
            subsumers.add((Integer) this.invAtomics.get(con.name));
        }

        return subsumers;
    }

    private boolean hasNoToldSubsumers(Item item) {
        if (item.description.getConcepts().size() == 0) {
            return true;
        }
        return false;
    }


    public void addLink(Nodo c, List<Nodo> predecessori, List<Nodo> successori) {
        for (int i = 0; i < predecessori.size(); i++) {
            if (!c.getPredecessors().contains(predecessori.get(i))) {
                c.getPredecessors().add((Nodo) predecessori.get(i));
            }
            Nodo pre = (Nodo) this.tassonomia.get(Integer.valueOf(((Nodo) predecessori.get(i)).getName()));
            if (!pre.getSuccessors().contains(c)) {
                pre.getSuccessors().add(c);
            }
        }
        for (int i = 0; i < successori.size(); i++) {
            if (!c.getSuccessors().contains(successori.get(i))) {
                c.getSuccessors().add((Nodo) successori.get(i));
            }
            Nodo suc = (Nodo) this.tassonomia.get(Integer.valueOf(((Nodo) successori.get(i)).getName()));
            if (!suc.getPredecessors().contains(c)) {
                suc.getPredecessors().add(c);
            }
        }
    }

    public void removeLink(List<Nodo> predecessori, List<Nodo> successori) {
        for (int i = 0; i < predecessori.size(); i++) {
            Nodo pre = (Nodo) this.tassonomia.get(Integer.valueOf(((Nodo) predecessori.get(i)).getName()));
            for (int j = 0; j < successori.size(); j++) {
                Nodo succ = (Nodo) this.tassonomia.get(Integer.valueOf(((Nodo) successori.get(j)).getName()));
                if (pre.getSuccessors().contains(succ)) {
                    pre.getSuccessors().remove(succ);
                    succ.getPredecessors().remove(pre);
                }
            }
        }
    }

    public void printNodo(Nodo node, int depth) {
        String conceptName = (String) this.atomics.get(Integer.valueOf(node.getName()));
        if (conceptName.equals("Nothing")) {
            return;
        }
        printIndent(depth);
        if (node.getEquivalents().size() == 0) {
            System.out.println(conceptName);
        } else {
            System.out.print(conceptName);
            for (int i = 0; i < node.getEquivalents().size(); i++) {
                Nodo equiv = (Nodo) node.getEquivalents().get(i);
                System.out.print(" = " + (String) this.atomics.get(Integer.valueOf(equiv.getName())));
            }
            System.out.println("");
        }
        for (int i = 0; i < node.getSuccessors().size(); i++) {
            Nodo figlio = (Nodo) this.tassonomia.get(Integer.valueOf(((Nodo) node.getSuccessors().get(i)).getName()));
            printNodo(figlio, depth + 1);
        }
    }

    public void printIndent(int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("\t");
        }
    }

    public void printTBox() {
        Nodo thing = (Nodo) this.tassonomia.get(Integer.valueOf(0));
        printNodo(thing, 0);
    }

    public boolean isSubsumed(Nodo y, Nodo c) {
        boolean result = false;
        if (this.subsumes.getQuick(y.getName(), c.getName()) != null) {
            result = ((Boolean) this.subsumes.getQuick(y.getName(), c.getName())).booleanValue();
        }
        return result;
    }

    public boolean isNotSubsumed(Nodo y, Nodo c) {
        boolean result = false;
        if (this.subsumes.getQuick(y.getName(), c.getName()) != null) {
            if (!((Boolean) this.subsumes.getQuick(y.getName(), c.getName())).booleanValue()) {
                result = true;
            } else {
                result = false;
            }
        }
        return result;
    }

    public boolean computeSubsumption(Nodo y, Nodo c) {
        String stringaY = (String) this.atomics.get(Integer.valueOf(y.getName()));
        String stringaC = (String) this.atomics.get(Integer.valueOf(c.getName()));
        AtomicConcept concettoY = new AtomicConcept();
        AtomicConcept concettoC = new AtomicConcept();
        concettoY.name = stringaY;
        concettoC.name = stringaC;
        Item item1;
        if (this.concetti_norm.containsKey(stringaY)) {
            item1 = (Item) this.concetti_norm.get(stringaY);
        } else {
            item1 = new Item(stringaY);
            item1.description.addConcept(concettoY);
            normalizzaItem(item1);
            this.concetti_norm.put(stringaY, item1);
        }
        Item item2;
        if (this.concetti_norm.containsKey(stringaC)) {
            item2 = (Item) this.concetti_norm.get(stringaC);
        } else {
            item2 = new Item(stringaC);
            item2.description.addConcept(concettoC);
            normalizzaItem(item2);
            this.concetti_norm.put(stringaC, item2);
        }

        if (item2.description.isSubsumed(item1.description)) {
            this.subsumes.setQuick(y.getName(), c.getName(), Boolean.valueOf(true));
            return true;
        }
        this.subsumes.setQuick(y.getName(), c.getName(), Boolean.valueOf(false));
        return false;
    }
}


