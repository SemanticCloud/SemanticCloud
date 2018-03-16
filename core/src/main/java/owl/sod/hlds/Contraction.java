 package owl.sod.hlds;
 
 
 public class Contraction
 {
   public SemanticDescription G;
   
   public SemanticDescription K;
   
   public double penalty;
   
   public Contraction()
   {
     this.G = new SemanticDescription();
     this.K = new SemanticDescription();
     this.penalty = 0.0D;
   }
   
   public Contraction(SemanticDescription G, SemanticDescription K, double penalty) {
     this.G = G;
     this.K = K;
     this.penalty = penalty;
   }
 }


