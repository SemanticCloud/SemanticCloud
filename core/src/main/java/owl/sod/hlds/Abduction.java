
package owl.sod.hlds;


public class Abduction {
    public SemanticDescription H;

    public double penalty;


    public Abduction() {

        this.H = new SemanticDescription();

        this.penalty = 0.0D;

    }


    public Abduction(SemanticDescription H, double penalty) {

        this.H = H;

        this.penalty = penalty;

    }

}


