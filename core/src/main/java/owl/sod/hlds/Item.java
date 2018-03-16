package owl.sod.hlds;


public class Item {
    public String name;
    public SemanticDescription description;
    public boolean equivalent;

    public Item(String name) {
        this.name = name;
        this.description = new SemanticDescription();
        this.equivalent = false;
    }

    public Item(String name, SemanticDescription sd) {
        this.name = name;
        this.description = sd;
        this.equivalent = false;
    }

    public void add(Item demandSD) {
        this.name = demandSD.name;
        if (demandSD.description.atomicConcepts != null)
            for (int i = 0; i < demandSD.description.atomicConcepts.size(); i++) {
                this.description.addConcept(
                        (AtomicConcept) demandSD.description.atomicConcepts.get(i));
            }
        if (demandSD.description.greaterThanRoles != null)
            for (int i = 0; i < demandSD.description.greaterThanRoles.size(); i++) {
                this.description.addGreaterThanRole(
                        (GreaterThanRole) demandSD.description.greaterThanRoles.get(i));
            }
        if (demandSD.description.lessThanRoles != null)
            for (int i = 0; i < demandSD.description.lessThanRoles.size(); i++) {
                this.description.addLessThanRole(
                        (LessThanRole) demandSD.description.lessThanRoles.get(i));
            }
        if (demandSD.description.universalRoles != null) {
            for (int i = 0; i < demandSD.description.universalRoles.size(); i++) {
                this.description.addUniversalRole(
                        (UniversalRole) demandSD.description.universalRoles.get(i));
            }
        }
    }

    public void copy(Item demandSD) {
        this.description = new SemanticDescription();
        add(demandSD);
    }
}


