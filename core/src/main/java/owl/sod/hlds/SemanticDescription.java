package owl.sod.hlds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


public class SemanticDescription {
    public List<AtomicConcept> atomicConcepts;
    public List<GreaterThanRole> greaterThanRoles;
    public List<LessThanRole> lessThanRoles;
    public List<UniversalRole> universalRoles;

    public SemanticDescription() {
        this.atomicConcepts = new ArrayList();
        this.greaterThanRoles = new ArrayList();
        this.lessThanRoles = new ArrayList();
        this.universalRoles = new ArrayList();
    }

    public List<AtomicConcept> getConcepts() {
        List<AtomicConcept> subClass = new ArrayList();
        for (int i = 0; i < this.atomicConcepts.size(); i++) {
            if (!((AtomicConcept) this.atomicConcepts.get(i)).denied) {
                subClass.add((AtomicConcept) this.atomicConcepts.get(i));
            }
        }
        return subClass;
    }

    public List<AtomicConcept> getComplements() {
        List<AtomicConcept> complements = new ArrayList();
        for (int i = 0; i < this.atomicConcepts.size(); i++) {
            if (((AtomicConcept) this.atomicConcepts.get(i)).denied) {
                complements.add((AtomicConcept) this.atomicConcepts.get(i));
            }
        }
        return complements;
    }

    public void addConcept(AtomicConcept atomicConcept) {
        this.atomicConcepts.add(atomicConcept);
    }

    public void addConcepts(List<AtomicConcept> v) {
        for (int i = 0; i < v.size(); i++) {
            if (!this.atomicConcepts.contains(v.get(i)))
                this.atomicConcepts.add((AtomicConcept) v.get(i));
        }
    }

    public void addGreaterThanRole(GreaterThanRole greaterThanRole) {
        this.greaterThanRoles.add(greaterThanRole);
    }

    public void addLessThanRole(LessThanRole lessThanRole) {
        this.lessThanRoles.add(lessThanRole);
    }

    public void addUniversalRole(UniversalRole universalRole) {
        this.universalRoles.add(universalRole);
    }

    public void addAllFromSD(SemanticDescription sourceSD) {
        this.atomicConcepts.addAll(sourceSD.atomicConcepts);
        this.greaterThanRoles.addAll(sourceSD.greaterThanRoles);
        this.lessThanRoles.addAll(sourceSD.lessThanRoles);
        this.universalRoles.addAll(sourceSD.universalRoles);
    }

    public void removeUniversalRole(UniversalRole universalRole) {
        this.universalRoles.remove(universalRole);
    }

    public void removeConcept(AtomicConcept atomicConcept) {
        this.atomicConcepts.remove(atomicConcept);
    }

    public void removegreaterThanRoles(GreaterThanRole greaterThanRole) {
        this.greaterThanRoles.remove(greaterThanRole);
    }

    public void removelessThanRoles(LessThanRole lessThanRole) {
        this.lessThanRoles.remove(lessThanRole);
    }


    public String toString() {
        String outputString = "(";

        if ((this.atomicConcepts != null) || (this.greaterThanRoles != null) || (this.lessThanRoles != null) || (this.universalRoles != null)) {

            if (this.atomicConcepts != null) {
                for (AtomicConcept atomicConcept : this.atomicConcepts) {
                    if (atomicConcept.denied) {
                        outputString = outputString + "!";
                    }
                    outputString = outputString + atomicConcept.name;
                    outputString = outputString + ",";
                }
            }
            if (this.greaterThanRoles != null) {
                for (GreaterThanRole greaterThanRole : this.greaterThanRoles) {
                    outputString = outputString + greaterThanRole.name + ">=" + greaterThanRole.cardinality + ",";
                }
            }
            if (this.lessThanRoles != null) {
                for (LessThanRole lessThanRole : this.lessThanRoles) {
                    outputString = outputString + lessThanRole.name + "<=" + lessThanRole.cardinality + ",";
                }
            }
            if (this.universalRoles != null) {
                for (UniversalRole ur : this.universalRoles) {
                    outputString = outputString + ur.toString();
                }
            }
        }
        if (outputString.length() > 1) {
            outputString = outputString.substring(0, outputString.length() - 1);
        }

        outputString = outputString + ")";
        return outputString;
    }

    public boolean isSubsumed(SemanticDescription subsumer) {
        if (this.atomicConcepts != null) {
            for (AtomicConcept atomicConcept : this.atomicConcepts) {
                if (atomicConcept.name.equals("Nothing")) {
                    return true;
                }
            }
        }

        if ((subsumer.atomicConcepts != null) && (this.atomicConcepts != null)) {
            for (AtomicConcept subsumerConc : subsumer.atomicConcepts) {
                boolean result = false;
                for (AtomicConcept thisConc : this.atomicConcepts) {
                    if (((thisConc.name.equals(subsumerConc.name)) || (subsumerConc.name.contains("Thing"))) && (thisConc.denied == subsumerConc.denied)) {
                        result = true;
                    }
                }
                if (!result) {
                    return false;
                }
            }
        }

        if ((subsumer.greaterThanRoles != null) && (this.greaterThanRoles != null)) {
            for (GreaterThanRole subsumerGTR : subsumer.greaterThanRoles) {
                boolean result = false;
                for (GreaterThanRole thisGTR : this.greaterThanRoles) {
                    if ((thisGTR.name.equals(subsumerGTR.name)) && (thisGTR.cardinality >= subsumerGTR.cardinality)) {
                        result = true;
                    }
                }
                if (!result) {
                    return false;
                }
            }
        }

        if ((subsumer.lessThanRoles != null) && (this.lessThanRoles != null)) {
            for (LessThanRole subsumerLTR : subsumer.lessThanRoles) {
                boolean result = false;
                for (LessThanRole thisLTR : this.lessThanRoles) {
                    if ((thisLTR.name.equals(subsumerLTR.name)) && (thisLTR.cardinality <= subsumerLTR.cardinality)) {
                        result = true;
                    }
                }
                if (!result) {
                    return false;
                }
            }
        }

        if ((subsumer.universalRoles != null) && (this.universalRoles != null)) {
            for (UniversalRole subsumerUV : subsumer.universalRoles) {
                boolean result = false;
                for (UniversalRole thisUV : this.universalRoles) {
                    if (subsumerUV.name.equals(thisUV.name)) {
                        result = thisUV.filler.isSubsumed(subsumerUV.filler);
                    }
                }
                if (!result) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean checkCompatibility(SemanticDescription demandSD) {
        if ((demandSD.atomicConcepts != null) && (this.atomicConcepts != null)) {
            for (AtomicConcept demandConc : demandSD.atomicConcepts) {
                boolean result = conceptSearch(this.atomicConcepts, demandConc);
                if (result) {
                    return false;
                }
            }
        }
        if ((demandSD.greaterThanRoles != null) && (this.lessThanRoles != null)) {
            for (GreaterThanRole demandGT : demandSD.greaterThanRoles) {
                boolean result = numberRoleSearchGT(this.lessThanRoles, demandGT);
                if (result) {
                    return false;
                }
            }
        }
        if ((demandSD.lessThanRoles != null) && (this.greaterThanRoles != null)) {
            for (LessThanRole demandLT : demandSD.lessThanRoles) {
                boolean result = numberRoleSearchLT(this.greaterThanRoles, demandLT);
                if (result) {
                    return false;
                }
            }
        }

        if ((demandSD.universalRoles != null) && (this.universalRoles != null)) {
//            Iterator localIterator2;
//            for (??? =demandSD.universalRoles.iterator(); ???.hasNext();
//            localIterator2.hasNext())
//            {
//                UniversalRole sdUniversalRole = (UniversalRole) ???.next();
//                localIterator2 = this.universalRoles.iterator();
//                continue;
//                UniversalRole thisUniversalRole = (UniversalRole) localIterator2.next();
//                if ((sdUniversalRole.name.equals(thisUniversalRole.name)) && ((hasRelation(this.greaterThanRoles, sdUniversalRole.name)) || (hasRelation(demandSD.greaterThanRoles, sdUniversalRole.name))) &&
//                        (!thisUniversalRole.filler.checkCompatibility(sdUniversalRole.filler))) {
//                    return false;
//                }
//            }
        }


        return true;
    }

    public Abduction abduce(SemanticDescription demandSD) {
        SemanticDescription H = new SemanticDescription();
        double penalty = 0.0D;
        int index = -1;

        if (demandSD.atomicConcepts != null) {
            if (this.atomicConcepts != null) {
                for (AtomicConcept demandConcept : demandSD.atomicConcepts) {
                    if (!this.atomicConcepts.contains(demandConcept)) {
                        H.addConcept(demandConcept);
                        penalty += 1.0D;
                    }
                }
            } else {
                H.atomicConcepts = demandSD.atomicConcepts;
                penalty += demandSD.atomicConcepts.size();
            }
        }

        if (demandSD.greaterThanRoles != null) {
            if (this.greaterThanRoles != null) {
                for (GreaterThanRole demandGTRole : demandSD.greaterThanRoles) {
                    index = this.greaterThanRoles.indexOf(demandGTRole);
                    if (index >= 0) {
                        GreaterThanRole great = (GreaterThanRole) this.greaterThanRoles.get(index);
                        if (great.cardinality < demandGTRole.cardinality) {
                            H.addGreaterThanRole(demandGTRole);

                            penalty = penalty + (demandGTRole.cardinality - great.cardinality) / demandGTRole.cardinality;
                        }
                    } else {
                        H.addGreaterThanRole(demandGTRole);
                        penalty += 1.0D;
                    }
                }
            } else {
                H.greaterThanRoles = demandSD.greaterThanRoles;
                penalty += demandSD.greaterThanRoles.size();
            }
        }

        if (demandSD.lessThanRoles != null) {
            if (this.lessThanRoles != null) {
                for (LessThanRole demandLTRole : demandSD.lessThanRoles) {
                    index = this.lessThanRoles.indexOf(demandLTRole);
                    if (index >= 0) {
                        LessThanRole less = (LessThanRole) this.lessThanRoles.get(index);
                        if (less.cardinality > demandLTRole.cardinality) {
                            H.addLessThanRole(demandLTRole);

                            penalty = penalty + (less.cardinality - demandLTRole.cardinality) / less.cardinality;
                        }
                    } else {
                        H.addLessThanRole(demandLTRole);
                        penalty += 1.0D;
                    }
                }
            } else {
                H.lessThanRoles = demandSD.lessThanRoles;
                penalty += demandSD.lessThanRoles.size();
            }
        }

        if (demandSD.universalRoles != null) {
            if (this.universalRoles != null) {
                for (UniversalRole demandURole : demandSD.universalRoles) {
                    index = this.universalRoles.indexOf(demandURole);
                    if (index >= 0) {
                        UniversalRole thisUniversalRole = (UniversalRole) this.universalRoles.get(index);
                        Abduction uRoleAbductionResult = thisUniversalRole.filler.abduce(demandURole.filler);
                        if ((!uRoleAbductionResult.H.atomicConcepts.isEmpty()) || (!uRoleAbductionResult.H.greaterThanRoles.isEmpty()) || (!uRoleAbductionResult.H.lessThanRoles.isEmpty()) || (!uRoleAbductionResult.H.universalRoles.isEmpty())) {
                            H.addUniversalRole(new UniversalRole(demandURole.name, uRoleAbductionResult.H));
                            penalty += uRoleAbductionResult.penalty;
                        }
                    } else {
                        H.addUniversalRole(new UniversalRole(demandURole.name, demandURole.filler));
                        penalty += new SemanticDescription().abduce(demandURole.filler).penalty;
                    }
                }
            } else {
                H.universalRoles = demandSD.universalRoles;
                for (UniversalRole demandURole : demandSD.universalRoles) {
                    penalty += new SemanticDescription().abduce(demandURole.filler).penalty;
                }
            }
        }


        return new Abduction(H, penalty);
    }

    public Contraction contract(SemanticDescription demandSD) {
        SemanticDescription G = new SemanticDescription();
        SemanticDescription K = new SemanticDescription();
        double penalty = 0.0D;

        K.addAllFromSD(demandSD);
        Iterator localIterator1;
        Iterator localIterator2;
        if ((demandSD.atomicConcepts != null) &&
                (this.atomicConcepts != null)) {
            for (localIterator1 = demandSD.atomicConcepts.iterator(); localIterator1.hasNext();
                 localIterator2.hasNext()) {
                AtomicConcept demandConcept = (AtomicConcept) localIterator1.next();
                localIterator2 = this.atomicConcepts.iterator();
                continue;
//                AtomicConcept thisConcept = (AtomicConcept) localIterator2.next();
//                if ((thisConcept.name.equals(demandConcept.name)) && (thisConcept.denied != demandConcept.denied)) {
//                    G.addConcept(demandConcept);
//                    K.removeConcept(demandConcept);
//                    penalty += 1.0D;
//                }
            }
        }


        if ((demandSD.greaterThanRoles != null) &&
                (this.lessThanRoles != null)) {
            for (localIterator1 = demandSD.greaterThanRoles.iterator(); localIterator1.hasNext();
                 localIterator2.hasNext()) {
                GreaterThanRole demandGTRole = (GreaterThanRole) localIterator1.next();
                localIterator2 = this.lessThanRoles.iterator();
                continue;
//                LessThanRole thisLTRole = (LessThanRole) localIterator2.next();
//                if ((thisLTRole.name.equals(demandGTRole.name)) && (thisLTRole.cardinality < demandGTRole.cardinality)) {
//                    G.addGreaterThanRole(demandGTRole);
//                    K.removegreaterThanRoles(demandGTRole);
//                    K.addGreaterThanRole(new GreaterThanRole(thisLTRole.name, thisLTRole.cardinality));
//
//                    penalty = penalty + (demandGTRole.cardinality - thisLTRole.cardinality) / demandGTRole.cardinality;
//                }
            }
        }


        if ((demandSD.lessThanRoles != null) &&
                (this.greaterThanRoles != null)) {
            for (localIterator1 = demandSD.lessThanRoles.iterator(); localIterator1.hasNext();
                 localIterator2.hasNext()) {
                LessThanRole demandLTRole = (LessThanRole) localIterator1.next();
                localIterator2 = this.greaterThanRoles.iterator();
                continue;
//                GreaterThanRole thisGTRole = (GreaterThanRole) localIterator2.next();
//                if ((thisGTRole.name.equals(demandLTRole.name)) && (thisGTRole.cardinality > demandLTRole.cardinality)) {
//                    G.addLessThanRole(demandLTRole);
//                    K.removelessThanRoles(demandLTRole);
//                    K.addLessThanRole(new LessThanRole(thisGTRole.name, thisGTRole.cardinality));
//
//                    penalty = penalty + (thisGTRole.cardinality - demandLTRole.cardinality) / thisGTRole.cardinality;
//                }
            }
        }


        if ((demandSD.universalRoles != null) &&
                (this.universalRoles != null)) {
            for (localIterator1 = demandSD.universalRoles.iterator(); localIterator1.hasNext();
                 localIterator2.hasNext()) {
                UniversalRole demandURole = (UniversalRole) localIterator1.next();
                localIterator2 = this.universalRoles.iterator();
                continue;
//                UniversalRole thisUniversalRole = (UniversalRole) localIterator2.next();
//                if (demandURole.name.equals(thisUniversalRole.name)) {
//                    if ((demandSD.greaterThanRoles != null) && (hasRelation(demandSD.greaterThanRoles, demandURole.name))) {
//                        Contraction uRoleContractionResult = thisUniversalRole.filler.contract(demandURole.filler);
//                        if ((!uRoleContractionResult.G.atomicConcepts.isEmpty()) || (!uRoleContractionResult.G.greaterThanRoles.isEmpty()) || (!uRoleContractionResult.G.lessThanRoles.isEmpty()) || (!uRoleContractionResult.G.universalRoles.isEmpty())) {
//                            G.addUniversalRole(new UniversalRole(demandURole.name, uRoleContractionResult.G));
//                            K.removeUniversalRole(demandURole);
//                            K.addUniversalRole(new UniversalRole(demandURole.name, uRoleContractionResult.K));
//                            penalty += uRoleContractionResult.penalty;
//                        }
//                    } else if ((demandSD.lessThanRoles != null) && (this.greaterThanRoles != null)) {
//                        GreaterThanRole gtr = extractFromlistGTR(this.greaterThanRoles, thisUniversalRole.name);
//                        LessThanRole ltr = extractFromlistLTR(demandSD.lessThanRoles, thisUniversalRole.name);
//                        if (gtr.cardinality < ltr.cardinality) {
//                            Contraction uRoleContractionResult = thisUniversalRole.filler.contract(demandURole.filler);
//                            if ((!uRoleContractionResult.G.atomicConcepts.isEmpty()) || (!uRoleContractionResult.G.greaterThanRoles.isEmpty()) || (!uRoleContractionResult.G.lessThanRoles.isEmpty()) || (!uRoleContractionResult.G.universalRoles.isEmpty())) {
//                                G.addUniversalRole(new UniversalRole(demandURole.name, uRoleContractionResult.G));
//                                K.removeUniversalRole(demandURole);
//                                K.addUniversalRole(new UniversalRole(demandURole.name, uRoleContractionResult.K));
//                                penalty += uRoleContractionResult.penalty;
//                            }
//                        }
//                    }
//                }
            }
        }


        return new Contraction(G, K, penalty);
    }


    public boolean isACover(SemanticDescription demandSD) {
        if ((demandSD.atomicConcepts != null) &&
                (this.atomicConcepts != null)) {
            for (int i = 0; i < demandSD.atomicConcepts.size(); i++) {
                AtomicConcept demandConcept = (AtomicConcept) demandSD.atomicConcepts.get(i);
                if (this.atomicConcepts.contains(demandConcept)) {
                    return true;
                }
            }
        }


        if ((demandSD.greaterThanRoles != null) &&
                (this.greaterThanRoles != null)) {
            for (int i = 0; i < demandSD.greaterThanRoles.size(); i++) {
                GreaterThanRole demandGTRole = (GreaterThanRole) demandSD.greaterThanRoles.get(i);
                if (this.greaterThanRoles.contains(demandGTRole)) {
                    return true;
                }
            }
        }

        LessThanRole demandLTRole;
        if ((demandSD.lessThanRoles != null) &&
                (this.lessThanRoles != null)) {
            for (int i = 0; i < demandSD.lessThanRoles.size(); i++) {
                demandLTRole = (LessThanRole) demandSD.lessThanRoles.get(i);
                if (this.lessThanRoles.contains(demandLTRole)) {
                    return true;
                }
            }
        }


        if ((demandSD.universalRoles != null) &&
                (this.universalRoles != null)) {
            for (UniversalRole demandURole : demandSD.universalRoles) {
                if (this.universalRoles.contains(demandURole)) {
                    for (UniversalRole thisUniversalRole : this.universalRoles) {
                        if ((demandURole.name.equals(thisUniversalRole.name)) &&
                                (thisUniversalRole.filler.isACover(demandURole.filler))) {
                            return true;
                        }
                    }
                }
            }
        }


        return false;
    }


    public Composition greedyCCoP(Vector<Item> resources) {
        Vector<Item> Rc = new Vector();
        Item demand = new Item("");
        demand.description = this;
        Item Du = new Item("");
        Du.copy(demand);
        Item Hmin = new Item("");
        Hmin.copy(demand);
        int hmin = (int) Math.round(new SemanticDescription().abduce(demand.description).penalty);
        Item Smax;
        do {
            Smax = new Item("");
            Item supply = new Item("");
            for (int j = 0; j < resources.size(); j++) {
                supply = (Item) resources.elementAt(j);
                boolean compatible = true;
                if (!Rc.isEmpty()) {
                    for (int i = 0; i < Rc.size(); i++) {
                        Item resource = (Item) Rc.elementAt(i);
                        compatible = (compatible) && (resource.description.checkCompatibility(supply.description));
                    }
                }
                if ((compatible) &&
                        (supply.description.isACover(Du.description))) {
                    Item H = new Item("");
                    H.description = supply.description.abduce(Du.description).H;
                    int h = (int) Math.round(new SemanticDescription().abduce(H.description).penalty);
                    if (h < hmin) {
                        Smax.copy(supply);
                        Hmin.copy(H);
                        hmin = h;
                    }
                }
            }


            if ((!Smax.description.atomicConcepts.isEmpty()) || (!Smax.description.greaterThanRoles.isEmpty()) || (!Smax.description.lessThanRoles.isEmpty()) || (!Smax.description.universalRoles.isEmpty())) {
                for (int i = 0; i < resources.size(); i++) {
                    Item servizio = (Item) resources.elementAt(i);
                    if (servizio.name.equals(Smax.name)) {
                        resources.remove(servizio);
                    }
                }
                Rc.add(Smax);
                Du.copy(Hmin);
            }
        }
        while ((!Smax.description.atomicConcepts.isEmpty()) || (!Smax.description.greaterThanRoles.isEmpty()) || (!Smax.description.lessThanRoles.isEmpty()) || (!Smax.description.universalRoles.isEmpty()));

        return new Composition(Rc, Du);
    }


    protected boolean hasRelation(List<GreaterThanRole> greaterThanRoles, String name) {
        if (greaterThanRoles != null) {
            for (GreaterThanRole gret : greaterThanRoles) {
                if ((gret.name.equals(name)) && (gret.cardinality >= 1)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    protected boolean checkUniversal(SemanticDescription demandSD) {
        if ((demandSD.atomicConcepts != null) && (this.atomicConcepts != null)) {
            for (AtomicConcept demandConc : demandSD.atomicConcepts) {
                boolean result = conceptSearch(this.atomicConcepts, demandConc);
                if (result) {
                    return false;
                }
            }
        }
        if ((demandSD.greaterThanRoles != null) && (this.lessThanRoles != null)) {
            for (GreaterThanRole demandGT : demandSD.greaterThanRoles) {
                boolean result = numberRoleSearchGT(this.lessThanRoles, demandGT);
                if (result) {
                    return false;
                }
            }
        }
        if ((demandSD.lessThanRoles != null) && (this.greaterThanRoles != null)) {
            for (LessThanRole demandLT : demandSD.lessThanRoles) {
                boolean result = numberRoleSearchLT(this.greaterThanRoles, demandLT);
                if (result) {
                    return false;
                }
            }
        }

        if ((demandSD.universalRoles != null) && (this.universalRoles != null)) {
//            Iterator localIterator2;
//            for (??? =demandSD.universalRoles.iterator(); ???.hasNext();
//            localIterator2.hasNext())
//            {
//                UniversalRole sdUniversalRole = (UniversalRole) ???.next();
//                localIterator2 = this.universalRoles.iterator();
//                continue;
//                UniversalRole thisUniversalRole = (UniversalRole) localIterator2.next();
//                if (sdUniversalRole.name.equals(thisUniversalRole.name)) {
//                    boolean res = thisUniversalRole.filler.checkUniversal(sdUniversalRole.filler);
//                    if (!res) {
//                        LessThanRole ltrole = new LessThanRole();
//                        ltrole.name = thisUniversalRole.name;
//                        ltrole.cardinality = 0;
//                        addLessThanRole(ltrole);
//                    }
//                }
//            }
        }


        return true;
    }

    protected boolean conceptSearch(List<AtomicConcept> atomicConcepts, AtomicConcept atomicConcept) {
        for (AtomicConcept concetto : atomicConcepts) {
            if ((concetto.name.equals(atomicConcept.name)) && (concetto.denied != atomicConcept.denied)) {
                return true;
            }
        }
        return false;
    }

    protected boolean numberRoleSearchGT(List<LessThanRole> numberRoles, GreaterThanRole role) {
        for (LessThanRole less : numberRoles) {
            if ((less.name.equals(role.name)) && (role.cardinality > less.cardinality)) {
                return true;
            }
        }
        return false;
    }

    protected boolean numberRoleSearchLT(List<GreaterThanRole> numberRoles, LessThanRole role) {
        for (GreaterThanRole great : numberRoles) {
            if ((great.name.equals(role.name)) && (role.cardinality < great.cardinality)) {
                return true;
            }
        }
        return false;
    }

    protected GreaterThanRole extractFromlistGTR(List<GreaterThanRole> ls, String name) {
        GreaterThanRole gtr = new GreaterThanRole();

        for (int i = 0; i < ls.size(); i++) {
            gtr = (GreaterThanRole) ls.get(i);
            if (gtr.name.equals(name)) {
                return gtr;
            }
        }

        return gtr;
    }

    protected LessThanRole extractFromlistLTR(List<LessThanRole> ls, String name) {
        LessThanRole ltr = new LessThanRole();

        for (int i = 0; i < ls.size(); i++) {
            ltr = (LessThanRole) ls.get(i);
            if (ltr.name.equals(name)) {
                return ltr;
            }
        }

        return ltr;
    }

    protected void delete(SemanticDescription demandSD) {
        if ((demandSD.atomicConcepts != null) && (this.atomicConcepts != null)) {
            for (int i = 0; i < demandSD.atomicConcepts.size(); i++) {
                removeConcept((AtomicConcept) demandSD.atomicConcepts.get(i));
            }
        }
        if ((demandSD.greaterThanRoles != null) && (this.greaterThanRoles != null)) {
            for (int i = 0; i < demandSD.greaterThanRoles.size(); i++) {
                removegreaterThanRoles((GreaterThanRole) demandSD.greaterThanRoles.get(i));
            }
        }
        if ((demandSD.lessThanRoles != null) && (this.lessThanRoles != null)) {
            for (int i = 0; i < demandSD.lessThanRoles.size(); i++) {
                removelessThanRoles((LessThanRole) demandSD.lessThanRoles.get(i));
            }
        }
        if ((demandSD.universalRoles != null) && (this.universalRoles != null)) {
            for (int i = 0; i < demandSD.universalRoles.size(); i++) {
                UniversalRole demandUR = (UniversalRole) demandSD.universalRoles.get(i);
                for (int j = 0; j < this.universalRoles.size(); j++) {
                    UniversalRole itemUR = (UniversalRole) this.universalRoles.get(j);
                    if (itemUR.name.equals(demandUR.name)) {
                        itemUR.filler.delete(demandUR.filler);
                    }
                }
            }
        }
    }

    protected String toXML(String name) {
        return "<D name=\"" + name + "\">\n" + XMLBody() + "</D>\n";
    }

    protected String XMLBody() {
        String outputString = "";

        if ((this.atomicConcepts != null) || (this.greaterThanRoles != null) || (this.lessThanRoles != null) || (this.universalRoles != null)) {
            if ((this.atomicConcepts != null) &&
                    (this.atomicConcepts.size() > 0)) {
                AtomicConcept atomicConcept = (AtomicConcept) this.atomicConcepts.get(0);
                outputString = outputString + "<Cs>\n<C id=\"" + atomicConcept.name + "\"/>\n</Cs>\n";
            }

            if ((this.greaterThanRoles != null) &&
                    (this.greaterThanRoles.size() > 0)) {
                outputString = outputString + "<GTRs>\n";
                for (int i = 0; i < this.greaterThanRoles.size(); i++) {
                    GreaterThanRole gtr = (GreaterThanRole) this.greaterThanRoles.get(i);
                    outputString = outputString + "<GTR id=\"" + gtr.name + "\" n=\"" + gtr.cardinality + "\"/>\n";
                }
                outputString = outputString + "</GTRs>\n";
            }

            if ((this.lessThanRoles != null) &&
                    (this.lessThanRoles.size() > 0)) {
                outputString = outputString + "<LTRs>\n";
                for (int i = 0; i < this.lessThanRoles.size(); i++) {
                    LessThanRole ltr = (LessThanRole) this.lessThanRoles.get(i);
                    outputString = outputString + "<LTR id=\"" + ltr.name + "\" n=\"" + ltr.cardinality + "\"/>\n";
                }
                outputString = outputString + "</LTRs>\n";
            }

            if ((this.universalRoles != null) &&
                    (this.universalRoles.size() > 0)) {
                outputString = outputString + "<URs>\n";
                for (int i = 0; i < this.universalRoles.size(); i++) {
                    UniversalRole ur = (UniversalRole) this.universalRoles.get(i);
                    String xmlBody = ur.filler.XMLBody();
                    if (!xmlBody.equals(""))
                        outputString = outputString + "<UR id=\"" + ur.name + "\">\n" + xmlBody + "</UR>\n";
                }
                outputString = outputString + "</URs>\n";
            }
        }

        return outputString;
    }

    public SemanticDescription subtract(SemanticDescription d) {
        SemanticDescription r = new SemanticDescription();

        if (this.atomicConcepts != null) {
            for (int i = 0; i < this.atomicConcepts.size(); i++) {
                AtomicConcept c = (AtomicConcept) this.atomicConcepts.get(i);
                if ((d.atomicConcepts == null) || (!d.atomicConcepts.contains(c))) {
                    r.addConcept(c);
                }
            }
        }
        if (this.greaterThanRoles != null) {
            for (int i = 0; i < this.greaterThanRoles.size(); i++) {
                GreaterThanRole g1 = (GreaterThanRole) this.greaterThanRoles.get(i);
                if (d.greaterThanRoles == null) {
                    r.addGreaterThanRole(g1);
                } else {
                    for (int j = 0; j < d.greaterThanRoles.size(); j++) {
                        GreaterThanRole g2 = (GreaterThanRole) d.greaterThanRoles.get(j);
                        if ((g1.name.equals(g2.name)) &&
                                (g1.cardinality < g2.cardinality)) {
                            int min = g1.cardinality;
                            int max = g2.cardinality - 1;
                            GreaterThanRole g = new GreaterThanRole();
                            g.name = g1.name;
                            g.cardinality = min;
                            r.addGreaterThanRole(g);
                            LessThanRole l = new LessThanRole();
                            l.name = g1.name;
                            l.cardinality = max;
                            r.addLessThanRole(l);
                        }
                    }
                }
            }
        }

        if (this.lessThanRoles != null) {
            for (int i = 0; i < this.lessThanRoles.size(); i++) {
                LessThanRole l1 = (LessThanRole) this.lessThanRoles.get(i);
                if (d.lessThanRoles == null) {
                    r.addLessThanRole(l1);
                } else {
                    for (int j = 0; j < d.lessThanRoles.size(); j++) {
                        LessThanRole l2 = (LessThanRole) d.lessThanRoles.get(j);
                        if ((l1.name.equals(l2.name)) &&
                                (l1.cardinality > l2.cardinality)) {
                            int min = l2.cardinality + 1;
                            int max = l1.cardinality;
                            GreaterThanRole g = new GreaterThanRole();
                            g.name = l1.name;
                            g.cardinality = min;
                            r.addGreaterThanRole(g);
                            LessThanRole l = new LessThanRole();
                            l.name = l1.name;
                            l.cardinality = max;
                            r.addLessThanRole(l);
                        }
                    }
                }
            }
        }

        if (this.universalRoles != null) {
            for (int i = 0; i < this.universalRoles.size(); i++) {
                UniversalRole u = (UniversalRole) this.universalRoles.get(i);
                if ((d.universalRoles == null) || (!d.universalRoles.contains(u))) {
                    r.addUniversalRole(u);
                } else {
                    SemanticDescription f1 = u.filler;
                    SemanticDescription f2 = ((UniversalRole) d.universalRoles.get(d.universalRoles.indexOf(u))).filler;
                    SemanticDescription df = f1.subtract(f2);
                    if ((!df.atomicConcepts.isEmpty()) || (!df.lessThanRoles.isEmpty()) || (!df.greaterThanRoles.isEmpty()) || (!df.universalRoles.isEmpty())) {
                        UniversalRole ur = new UniversalRole(u.name, df);
                        r.addUniversalRole(ur);
                    }
                }
            }
        }

        return r;
    }
}


