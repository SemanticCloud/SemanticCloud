package owl.owlapi;

import openllet.owlapi.PelletReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;
import owl.sod.hlds.Abduction;
import owl.sod.hlds.AtomicConcept;
import owl.sod.hlds.Composition;
import owl.sod.hlds.Contraction;
import owl.sod.hlds.GreaterThanRole;
import owl.sod.hlds.Item;
import owl.sod.hlds.LessThanRole;
import owl.sod.hlds.Match;
import owl.sod.hlds.SemanticDescription;
import owl.sod.hlds.UniversalRole;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.reasoner.AxiomNotInProfileException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.ClassExpressionNotInProfileException;
import org.semanticweb.owlapi.reasoner.FreshEntitiesException;
import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.TimeOutException;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import org.semanticweb.owlapi.util.DLExpressivityChecker;
import org.semanticweb.owlapi.util.OWLClassExpressionCollector;
import org.semanticweb.owlapi.util.Version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;


public class MicroReasoner extends PelletReasoner
        implements OWLReasoner  {
    private static final String MSG_UNIMPLEMENTED = "Not yet implemented.";
    private static final String OWL_PREFIX = "http://www.w3.org/2002/07/owl#";
    private static final String OWL_THING = "Thing";
    private static final String OWL_NOTHING = "Nothing";
    private static final int MAJOR = 1;
    private static final int MINOR = 0;
    private static final int PATCH = 0;
    private static final int BUILD = 0;
    static final String REASONER_NAME = "Mini-ME OWLReasoner - Developed by SisInfLab (http://sisinflab.poliba.it)";
    private InferenceType[] infType = null;


    private KBWrapper kb;


    private OWLOntology rootOntology;


    public HashMap<String, Item> supplyIndividuals;


    public HashMap<String, Item> demandIndividuals;


    public MicroReasoner(OWLOntology ontology) {
        super(ontology,BufferingMode.BUFFERING);
        this.rootOntology = ontology;
        this.kb = new KBWrapper(this.rootOntology);
        precomputeInferences(new InferenceType[]{InferenceType.CLASS_HIERARCHY});
        this.supplyIndividuals = this.kb.normalizeIndividuals(this.rootOntology);
        this.demandIndividuals = new HashMap();
    }

    public String getReasonerName() {
        return "Mini-ME OWLReasoner - Developed by SisInfLab (http://sisinflab.poliba.it)";
    }

    public Version getReasonerVersion() {
        return new Version(1, 0, 0, 0);
    }

    public String getDescriptionLogicName() {
        Set<OWLOntology> onto = OWLManager.createOWLOntologyManager().getOntologies();
        onto.add(this.rootOntology);

        DLExpressivityChecker dlex = new DLExpressivityChecker(onto);

        return dlex.getDescriptionLogicName();
    }


    public BufferingMode getBufferingMode() {
        return BufferingMode.NON_BUFFERING;
    }


    public Set<String> loadDemand(OWLOntology individualsAsOntology) {
        return loadIndividual(individualsAsOntology, this.demandIndividuals);
    }


    public String loadDemand(Item demandItem) {
        return loadIndividual(demandItem, this.demandIndividuals);
    }


    public Set<String> loadSupply(OWLOntology individualsAsOntology) {
        return loadIndividual(individualsAsOntology, this.supplyIndividuals);
    }


    public String loadSupply(Item supplyItem) {
        return loadIndividual(supplyItem, this.supplyIndividuals);
    }

    private Set<String> loadIndividual(OWLOntology individualsAsOntology, HashMap<String, Item> map) {
        if (individualsAsOntology == null)
            throw new NullPointerException();
        HashMap<String, Item> newIndividuals = this.kb.normalizeIndividuals(individualsAsOntology);
        map.putAll(newIndividuals);
        return newIndividuals.keySet();
    }

    private String loadIndividual(Item item, HashMap<String, Item> map) {
        if (item == null)
            throw new NullPointerException();
        String name = item.name;
        this.kb.normalizzaItem(item);
        map.put(name, item);
        return name;
    }


    public Set<String> getSupplyIndividuals() {
        return this.supplyIndividuals.keySet();
    }


    public Set<String> getDemandIndividuals() {
        return this.demandIndividuals.keySet();
    }


    public Item retrieveSupplyIndividual(String individual) {
        return retrieveIndividual(individual, this.supplyIndividuals);
    }


    public Item retrieveDemandIndividual(String individual) {
        return retrieveIndividual(individual, this.demandIndividuals);
    }

    private Item retrieveIndividual(String individual, HashMap<String, Item> map) {
        if (individual == null) {
            throw new NullPointerException();
        }
        Item item = (Item) map.get(individual);
        if (item == null) {
            throw new ResourceNotFoundException();
        }
        return item;
    }


    public Contraction contraction(String supplyIndividual, String demandIndividual) {
        Item supply = retrieveSupplyIndividual(supplyIndividual);
        Item demand = retrieveDemandIndividual(demandIndividual);
        return supply.description.contract(demand.description);
    }


    public Abduction abduction(String supplyIndividual, String demandIndividual) {
        Item supply = retrieveSupplyIndividual(supplyIndividual);
        Item demand = retrieveDemandIndividual(demandIndividual);
        return supply.description.abduce(demand.description);
    }

    public SemanticDescription bonus(String supplyIndividual, String demandIndividual) {
        Item supply = retrieveSupplyIndividual(supplyIndividual);
        Item demand = retrieveDemandIndividual(demandIndividual);
        return demand.description.abduce(supply.description).H;
    }

    public SemanticDescription difference(String supplyIndividual, String demandIndividual) {
        Item supply = retrieveSupplyIndividual(supplyIndividual);
        Item demand = retrieveDemandIndividual(demandIndividual);
        return supply.description.subtract(demand.description);
    }


    public Match evaluateMatch(String supplyIndividual, String demandIndividual) {
        Contraction contr = null;
        Abduction abd = null;

        if (checkCompatibility(supplyIndividual, demandIndividual)) {
            checkSubsumption(supplyIndividual, demandIndividual);


        } else {


            contr = contraction(supplyIndividual, demandIndividual);
            Item keep = new Item("_keep", contr.K);
            loadDemand(keep);
            abd = abduction(supplyIndividual, keep.name);
        }

        return new Match(abd, contr);
    }

    public Composition composition(String demandIndividual) {
        Vector<Item> compatible = new Vector();
        Item demand = retrieveDemandIndividual(demandIndividual);


        for (String supplyIndividual : this.supplyIndividuals.keySet()) {
            Item supply = retrieveSupplyIndividual(supplyIndividual);
            if (supply.description.checkCompatibility(demand.description)) {
                compatible.add(supply);
            }
        }
        return demand.description.greedyCCoP(compatible);
    }


    public boolean checkCompatibility(String supplyIndividual, String demandIndividual) {
        Item supply = retrieveSupplyIndividual(supplyIndividual);
        Item demand = retrieveDemandIndividual(demandIndividual);
        return supply.description.checkCompatibility(demand.description);
    }


    public boolean checkSubsumption(String subsumedIndividual, String subsumerIndividual) {
        Item subsumed = retrieveSupplyIndividual(subsumedIndividual);
        Item subsumer = retrieveDemandIndividual(subsumerIndividual);
        return subsumed.description.isSubsumed(subsumer.description);
    }

    public Node<OWLClass> getTopClassNode() {
        return new OWLClassNode(OWLManager.getOWLDataFactory().getOWLClass(IRI.create("http://www.w3.org/2002/07/owl#Thing")));
    }

    public Node<OWLClass> getBottomClassNode() {
        return new OWLClassNode(OWLManager.getOWLDataFactory().getOWLClass(IRI.create("http://www.w3.org/2002/07/owl#Nothing")));
    }

    public OWLOntology getRootOntology() {
        return this.rootOntology;
    }


    public void precomputeInferences(InferenceType... inferenceTypes)
            throws ReasonerInterruptedException, TimeOutException, InconsistentOntologyException {
        this.infType = inferenceTypes;


        if (this.kb.ontology.size() == 0) {
            this.kb.normalizeOntology(this.rootOntology);
            this.supplyIndividuals = this.kb.normalizeIndividuals(this.rootOntology);
        }
    }

    public boolean isPrecomputed(InferenceType inferenceType) {
        InferenceType[] arrayOfInferenceType;
        int j = (arrayOfInferenceType = this.infType).length;
        for (int i = 0; i < j; i++) {
            InferenceType element = arrayOfInferenceType[i];
            if (element == inferenceType) {
                return true;
            }
        }
        return false;
    }

    public boolean isConsistent()
            throws ReasonerInterruptedException, TimeOutException {
        return this.kb.isConsistent(this.kb.ontology);
    }


    public boolean isSatisfiable(OWLClassExpression classExpression)
            throws ReasonerInterruptedException, TimeOutException, ClassExpressionNotInProfileException, FreshEntitiesException, InconsistentOntologyException {
        return this.kb.isSatisfiable(classExpression.asOWLClass().getIRI().toString());
    }


    public Node<OWLClass> getUnsatisfiableClasses()
            throws ReasonerInterruptedException, TimeOutException, InconsistentOntologyException {
        OWLClassNode unsClasses = new OWLClassNode();

        List<String> classes = this.kb.getUnsatisfiableClasses(this.kb.ontology);

        for (OWLClass cls : this.rootOntology.getClassesInSignature()) {
            if (classes.contains(cls.getIRI().toString())) {
                unsClasses.add(cls);
            }
        }
        return unsClasses;
    }


    public NodeSet<OWLClass> getSubClasses(OWLClassExpression ce, boolean direct)
            throws ReasonerInterruptedException, TimeOutException, FreshEntitiesException, InconsistentOntologyException, ClassExpressionNotInProfileException {
        if (direct) {
            return getDirectSubClasses(ce);
        }
        return getAllSubClasses(ce, new OWLClassNodeSet());
    }


    public NodeSet<OWLClass> getSuperClasses(OWLClassExpression ce, boolean direct)
            throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        if (direct) {
            return getDirectSuperClasses(ce);
        }
        return getAllSuperClasses(ce, new OWLClassNodeSet());
    }


    public Node<OWLClass> getEquivalentClasses(OWLClassExpression ce)
            throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
        if (isPrecomputed(InferenceType.DISJOINT_CLASSES)) {

            return null;
        }

        return getSyntacticEquivalentClasses(ce);
    }


    public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression ce)
            throws ReasonerInterruptedException, TimeOutException, FreshEntitiesException, InconsistentOntologyException {
        if (isPrecomputed(InferenceType.DISJOINT_CLASSES)) {

            return null;
        }

        return getSyntacticDisjointClasses(ce);
    }

    private NodeSet<OWLClass> getAllSubClasses(OWLClassExpression ce, OWLClassNodeSet subClasses) {
        String name = ce.asOWLClass().getIRI().toString();
        int rootID = 0;

        if (name.contains("Thing")) {
            name = "Thing";
        }

        rootID = ((Integer) this.kb.invAtomics.get(name)).intValue();

        ArrayList<String> subClassName = getSubClassName(rootID);

        for (OWLClass cls : this.rootOntology.getClassesInSignature()) {
            if (subClassName.contains(cls.getIRI().toString())) {
                subClasses.addEntity(cls);
            }
        }

        return subClasses;
    }

    private ArrayList<String> getSubClassName(int rootID) {
        if (!((String) this.kb.atomics.get(Integer.valueOf(rootID))).equals("Nothing")) {
            ArrayList<Nodo> subClassID = ((Nodo) this.kb.tassonomia.get(Integer.valueOf(rootID))).getSuccessors();

            ArrayList<String> subClassName = new ArrayList();

            for (int i = 0; i < subClassID.size(); i++) {
                int child = ((Nodo) subClassID.get(i)).getName();
                subClassName.add((String) this.kb.atomics.get(Integer.valueOf(child)));
                subClassName.addAll(getSubClassName(child));
            }

            return subClassName;
        }

        return new ArrayList();
    }

    private NodeSet<OWLClass> getDirectSubClasses(OWLClassExpression ce) {
        OWLClassNodeSet subClasses = new OWLClassNodeSet();

        String name = ce.asOWLClass().getIRI().toString();
        int rootID = 0;

        if (name.contains("Thing")) {
            name = "Thing";
        } else if (name.contains("Nothing")) {
            name = "Nothing";
        }
        rootID = ((Integer) this.kb.invAtomics.get(name)).intValue();

        ArrayList<Nodo> subClassID = ((Nodo) this.kb.tassonomia.get(Integer.valueOf(rootID))).getSuccessors();

        Vector<String> subClassName = new Vector();
        for (int i = 0; i < subClassID.size(); i++) {
            subClassName.add((String) this.kb.atomics.get(Integer.valueOf(((Nodo) subClassID.get(i)).getName())));
        }

        for (OWLClass cls : this.rootOntology.getClassesInSignature()) {
            if (subClassName.contains(cls.getIRI().toString())) {
                subClasses.addEntity(cls);
            }
        }

        return subClasses;
    }

    private NodeSet<OWLClass> getAllSuperClasses(OWLClassExpression ce, OWLClassNodeSet superClasses) {
        String name = ce.asOWLClass().getIRI().toString();
        int rootID = 0;

        if (name.contains("Thing")) {
            name = "Thing";
        }

        rootID = ((Integer) this.kb.invAtomics.get(name)).intValue();

        ArrayList<String> superClassName = getSuperClassName(rootID);

        for (OWLClass cls : this.rootOntology.getClassesInSignature()) {
            if (superClassName.contains(cls.getIRI().toString())) {
                superClasses.addEntity(cls);
            }
        }

        return superClasses;
    }

    private ArrayList<String> getSuperClassName(int rootID) {
        if (!((String) this.kb.atomics.get(Integer.valueOf(rootID))).equals("Thing")) {
            ArrayList<Nodo> superClassID = ((Nodo) this.kb.tassonomia.get(Integer.valueOf(rootID))).getPredecessors();

            ArrayList<String> superClassName = new ArrayList();

            for (int i = 0; i < superClassID.size(); i++) {
                int parent = ((Nodo) superClassID.get(i)).getName();
                superClassName.add((String) this.kb.atomics.get(Integer.valueOf(parent)));
                superClassName.addAll(getSuperClassName(parent));
            }

            return superClassName;
        }

        return new ArrayList();
    }

    private NodeSet<OWLClass> getDirectSuperClasses(OWLClassExpression ce) {
        OWLClassNodeSet superClasses = new OWLClassNodeSet();

        String name = ce.asOWLClass().getIRI().toString();
        int rootID = 0;

        if (name.contains("Thing")) {
            name = "Thing";
        }

        rootID = ((Integer) this.kb.invAtomics.get(name)).intValue();

        ArrayList<Nodo> superClassID = ((Nodo) this.kb.tassonomia.get(Integer.valueOf(rootID))).getPredecessors();

        Vector<String> superClassName = new Vector();
        for (int i = 0; i < superClassID.size(); i++) {
            superClassName.add((String) this.kb.atomics.get(Integer.valueOf(((Nodo) superClassID.get(i)).getName())));
        }

        for (OWLClass cls : this.rootOntology.getClassesInSignature()) {
            if (superClassName.contains(cls.getIRI().toString())) {
                superClasses.addEntity(cls);
            }
        }

        return superClasses;
    }

    private Node<OWLClass> getSyntacticEquivalentClasses(OWLClassExpression ce) {
        OWLClassNode equivClasses = new OWLClassNode();

        Iterator<OWLClassExpression> it = EntitySearcher.getEquivalentClasses(ce.asOWLClass(), this.rootOntology).iterator();
        while (it.hasNext()) {
            OWLClassExpression exp = (OWLClassExpression) it.next();
            if (!exp.isAnonymous()) {


                equivClasses.add(exp.asOWLClass());
            }
        }
        return equivClasses;
    }


    private NodeSet<OWLClass> getSyntacticDisjointClasses(OWLClassExpression ce)
            throws ReasonerInterruptedException, TimeOutException, FreshEntitiesException, InconsistentOntologyException {
        OWLClassNodeSet disjClasses = new OWLClassNodeSet();

        Iterator<OWLClassExpression> it =EntitySearcher.getDisjointClasses(ce.asOWLClass(), this.rootOntology).iterator();
        while (it.hasNext()) {
            disjClasses.addEntity(((OWLClassExpression) it.next()).asOWLClass());
        }

        return disjClasses;
    }


    public void dispose() {
    }

    public List<OWLOntologyChange> getPendingChanges() {
        ArrayList<OWLOntologyChange> changes;

        return changes = new ArrayList();
    }


    public Set<InferenceType> getPrecomputableInferenceTypes() {
        Set<InferenceType> supportedInferenceTypes = new HashSet();
        supportedInferenceTypes.add(InferenceType.CLASS_HIERARCHY);
        return supportedInferenceTypes;
    }






    private static class RestrictionVisitor extends OWLClassExpressionCollector {
        GreaterThanRole gtRestriction = null;
        LessThanRole ltRestriction = null;
        UniversalRole uvRestriction = null;



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
            this.gtRestriction.name = nameExtraction(((OWLObjectPropertyExpression) desc.getProperty()).toString());
            this.gtRestriction.cardinality = 1;
            return objects;
        }


        public Collection<OWLClassExpression> visit(OWLObjectAllValuesFrom desc) {
            this.uvRestriction.name = nameExtraction(((OWLObjectPropertyExpression) desc.getProperty()).toString());
            exploreFiller((OWLClassExpression) desc.getFiller(), this.uvRestriction.filler);
            return objects;
        }


        public Collection<OWLClassExpression> visit(OWLObjectMaxCardinality desc) {
            this.ltRestriction.name = nameExtraction(((OWLObjectPropertyExpression) desc.getProperty()).toString());
            this.ltRestriction.cardinality = desc.getCardinality();
            return objects;
        }


        public Collection<OWLClassExpression> visit(OWLObjectMinCardinality desc) {
            this.gtRestriction.name = nameExtraction(((OWLObjectPropertyExpression) desc.getProperty()).toString());
            this.gtRestriction.cardinality = desc.getCardinality();
            return objects;
        }


        public Collection<OWLClassExpression> visit(OWLObjectExactCardinality desc) {
            this.gtRestriction.name = nameExtraction(((OWLObjectPropertyExpression) desc.getProperty()).toString());
            this.gtRestriction.cardinality = desc.getCardinality();
            this.ltRestriction.name = nameExtraction(((OWLObjectPropertyExpression) desc.getProperty()).toString());
            this.ltRestriction.cardinality = desc.getCardinality();
            return objects;
        }

        public void exploreFiller(OWLClassExpression expression, SemanticDescription res) {
            String type = expression.getClassExpressionType().toString();
            if (type.equals("Class")) {
                OWLClass classe = expression.asOWLClass();
                AtomicConcept newConcept = new AtomicConcept();
                newConcept.name = nameExtraction(classe.toString());
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

        public static String nameExtraction(String name) {
            if (name.contains("Thing")) {
                return name;
            }
            int posizione = name.indexOf("#");
            String nome = name.substring(posizione + 1, name.length() - 1);
            return nome;
        }
    }
}


