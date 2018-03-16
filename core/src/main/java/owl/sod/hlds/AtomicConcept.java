
package owl.sod.hlds;


public class AtomicConcept extends Concept {
    public boolean denied;


    public AtomicConcept() {

        super("");

        this.denied = false;

    }


    public AtomicConcept(String name) {
        super(name);

        this.denied = false;

    }


    public AtomicConcept(String name, boolean denied) {
        super(name);

        this.denied = denied;

    }


    public boolean equals(Object obj) {

        AtomicConcept atomicConcept;

        if ((obj instanceof AtomicConcept)) {

            atomicConcept = (AtomicConcept) obj;

        } else
            return false;

        return (this.name.equals(atomicConcept.name)) && (this.denied == atomicConcept.denied);

    }

}


